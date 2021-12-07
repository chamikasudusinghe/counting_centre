package com.example.counting_center.controllers;

import com.example.counting_center.entities.Election;
import com.example.counting_center.entities.ElectionResult;
import com.example.counting_center.messages.ErrorResponse;
import com.example.counting_center.repositories.ElectionRepository;
import com.example.counting_center.repositories.ResultRepository;
import com.example.counting_center.repositories.VoteRepository;
import com.example.counting_center.util.ErrorMessageCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/counting-center")
public class CountingCenterController {


    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @PostMapping("/start-voting")
    ResponseEntity<?> startVoting() {
        // TODO: start accepting votes
        electionRepository.save(new Election(Long.valueOf("1"),true));
        return ResponseEntity.status(200).body("success");
    }

    @PostMapping("/end-voting")
    ResponseEntity<?> endVoting() {
        // TODO: end accepting votes
        electionRepository.save(new Election(Long.valueOf("1"),false));
        List<String> candidateList;
        try{
            candidateList = voteRepository.getAllByVotedForGroup();
        }catch (Exception e){
            log.error("Can't Get all Candidates");
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }

        try {
            for (int i = 0; i < candidateList.size(); i++) {
                ElectionResult electionResult = new ElectionResult();
                Long count = voteRepository.countAllByVotedForAndAcceptedTrue(candidateList.get(i));
                electionResult.setCandidateID(candidateList.get(i));
                electionResult.setCount(count);
                resultRepository.save(electionResult);
            }
        }catch (Exception e){
            log.error("Error occur while counting");
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }
        try{
            log.info("Getting Results");
            return ResponseEntity.status(200).body(resultRepository.findAll());
        }catch (Exception e){
            log.error("Can't Get all Results");
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }

    }
}
