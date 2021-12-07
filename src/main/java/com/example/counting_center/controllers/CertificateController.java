package com.example.counting_center.controllers;

import com.example.counting_center.entities.Session;
import com.example.counting_center.messages.ErrorResponse;
import com.example.counting_center.repositories.SessionRepository;
import com.example.counting_center.util.ErrorMessageCode;
import com.example.counting_center.util.Keys;
import com.example.counting_center.util.RSA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/certificate")
public class CertificateController {
    private final RSA rsa;

    @Autowired
    private SessionRepository sessionRepository;

    CertificateController() {
        this.rsa = new RSA();
    }

    @GetMapping("/sign")
    ResponseEntity<?> getSignCertificate() {

        String certificate;
        try {
            certificate = Keys.getSignCertificateString();
        } catch (Exception e) {
            log.error("Get signing cert error", e);
            return ResponseEntity.status(500)
                    .body(new ErrorResponse(500, ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }
        return ResponseEntity.ok().body(certificate);
    }

    @GetMapping("/encrypt")
    ResponseEntity<?> getEncryptCertificate() {
        String certificate;
        try {
            certificate = Keys.getEncryptCertificateString();
        } catch (Exception e) {
            log.error("Get signing cert error", e);
            return ResponseEntity.status(500)
                    .body(new ErrorResponse(500, ErrorMessageCode.INTERNAL_SERVER_ERROR));
        }
        return ResponseEntity.ok().body(certificate);
    }

    @PostMapping("/start-session")
    ResponseEntity<?> postStartSession(@RequestBody String msg) {
        log.info("start-session msg={}", msg);
        try {
            String key = rsa.decrypt(msg);
            log.info("decrypted key={}", key);
            Session session = new Session(key);
            session = sessionRepository.save(session);

            UUID id = session.getId();
            return ResponseEntity.ok().body(id);
        } catch (Exception e) {
            log.error("start-session error", e);
            return ResponseEntity.badRequest().body("Request Failed");
        }
    }
}
