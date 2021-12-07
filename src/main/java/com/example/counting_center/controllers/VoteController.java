package com.example.counting_center.controllers;

import com.example.counting_center.entities.Election;
import com.example.counting_center.entities.Session;
import com.example.counting_center.entities.Vote;
import com.example.counting_center.messages.*;
import com.example.counting_center.repositories.ElectionRepository;
import com.example.counting_center.repositories.SessionRepository;
import com.example.counting_center.repositories.VoteRepository;
import com.example.counting_center.util.AESHelper;
import com.example.counting_center.util.ErrorMessageCode;
import com.example.counting_center.util.Keys;
import com.example.counting_center.util.RSA;
import com.example.counting_center.web_clients.BallotIdVerificationWebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/vote")
public class VoteController {
    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private BallotIdVerificationWebClient ballotIdVerificationWebClient;

    RSA rsa = new RSA();

    /**
     * Get all votes (For Testing only).
     *
     * @return List of votes
     */
    @GetMapping("/votes")
    ResponseEntity<?> voteGet() {
        try {
            log.info("Getting all votes");
            return ResponseEntity.status(200).body(voteRepository.findAll());
        } catch (Exception e) {
            log.error("Can't Get all Votes");
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }

    }


    /**
     * Vote request called from mobile app
     *
     * @param encryptedVoteRequest VoteRequest
     * @return vote receipt if successful else respective error message
     */
    @PostMapping("/vote")
    ResponseEntity<?> votePost(@Valid @RequestBody EncryptedVoteRequest encryptedVoteRequest) throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeySpecException {

        try{
            Optional<Election> election = electionRepository.findById(Long.valueOf("1"));
            if(election.isEmpty()){
                log.error("Can't vote now");
                return ResponseEntity.badRequest().body(new ErrorResponse(ErrorMessageCode.VOTING_NOT_ALLOWED));
            }else if(!election.get().isCanVote()){
                log.error("Can't vote now");
                return ResponseEntity.badRequest().body(new ErrorResponse(ErrorMessageCode.VOTING_NOT_ALLOWED));
            }
        }catch (Exception e){
            log.error("Can't vote now");
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorMessageCode.VOTING_NOT_ALLOWED));

        }

        log.info("vote POST -> {}", encryptedVoteRequest);

        String msg;
        try {
            Optional<Session> optSession = sessionRepository.findById(UUID.fromString(encryptedVoteRequest.getSessionKey()));
            if (optSession.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse(ErrorMessageCode.INVALID_SESSION_KEY));
            }
            String key = optSession.get().getKey();
            log.info("Found key ={} for session key = {}", encryptedVoteRequest.getSessionKey(), key);
            msg = AESHelper.decrypt(key, encryptedVoteRequest.getMessage());
            log.info("decrypted msg={}", msg);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(500, ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        VoteRequest voteRequest;
        try {
            voteRequest = objectMapper.readValue(msg.strip(), VoteRequest.class);
        } catch (Exception e) {
            log.info("unable to convert to VoteRequest ", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(ErrorMessageCode.INVALID_REQUEST));
        }

        if (isAccepted(voteRequest.getBallotID())) {
            log.error("Cannot vote for this ballot ID");
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(ErrorMessageCode.INVALID_REQUEST));
        }


        boolean isSignatureValid = rsa.varifySignature(
                voteRequest.getSignature(),
                voteRequest.getBallotID() + voteRequest.getVoteFor(),
                Keys.getVotingCenterCertificate());
        if (!isSignatureValid) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(ErrorMessageCode.INVALID_SIGNATURE));
        }

        Vote vote = new Vote(voteRequest.getBallotID(), voteRequest.getVoteFor());
        try {
            vote = voteRepository.save(vote);
        } catch (Exception e) {
            log.error("unable to save Vote");
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }

        ValidateBallotIdRequest request = new ValidateBallotIdRequest(vote.getBallotId());
        ValidateBallotIdResponse response = new ValidateBallotIdResponse(vote.getBallotId(), "1231412");
//        try{
//            ValidateBallotIdResponse response = ballotIdVerificationWebClient.getVerifiedBallotId(request);
//        }catch (Exception e){
//        log.info("unable to Connect to voting center");
//            return ResponseEntity.internalServerError()
//                    .body(new ErrorResponse(ErrorMessageCode.INTERNAL_SERVER_ERROR));
//        }
//        boolean isValidBallotID = rsa.varifySignature(
//                response.getSignedBallotId(),
//                voteRequest.getBallotID(),
//                Keys.getVotingCenterCertificate()
//        );
//        if (!isValidBallotID) {
//            return ResponseEntity.badRequest()
//                    .body(new ErrorResponse(ErrorMessageCode.INVALID_BALLOT_ID));
//        }

        String receipt = rsa.signMessage(response.getSignedBallotId());
        vote.setAccepted(true);
        vote.setReceipt(receipt);
        try {
            vote = voteRepository.save(vote);
        } catch (Exception e) {
            log.error("unable to save Vote");
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }
        log.info("Successfully Voted");
        return ResponseEntity.ok().body(new VoteSuccessResponse(vote.getReceipt()));
    }

    /**
     * Check whether the votes marked
     *
     * @param msg vote receipt
     * @return success if valid vote
     */
    @PostMapping("/check")
    ResponseEntity<?> checkReceipt(@RequestBody String msg) {
        // TODO: check receipt validity
        // TODO: send response valid vote
        return ResponseEntity.ok().body("");
    }

    boolean isAccepted(String ballotID) {
        if (voteRepository.countAllByBallotIdAndAcceptedTrue(ballotID) == 0) {
            return false;
        }
        return true;
    }
}
