package com.example.counting_center.messages;

import lombok.Getter;

@Getter
public class ValidateBallotIdResponse {
   private String ballotID;
   private String signedBallotId;

   public ValidateBallotIdResponse(String ballotID, String signedBallotId) {
      this.ballotID = ballotID;
      this.signedBallotId = signedBallotId;
   }
}
