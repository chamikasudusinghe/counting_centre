package com.example.counting_center.web_clients;

import com.example.counting_center.messages.ValidateBallotIdRequest;
import com.example.counting_center.messages.ValidateBallotIdResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BallotIdVerificationWebClient {

   public ValidateBallotIdResponse getVerifiedBallotId(ValidateBallotIdRequest validateBallotIdRequest){
       WebClient webClient = WebClient.create("http://localhost:8080");

      return webClient.post()
               .uri("/counting")
               .body(Mono.just(validateBallotIdRequest), ValidateBallotIdRequest.class)
               .retrieve()
               .bodyToMono(ValidateBallotIdResponse.class)
               .block();
   }
}
