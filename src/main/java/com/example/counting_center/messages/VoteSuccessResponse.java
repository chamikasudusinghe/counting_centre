package com.example.counting_center.messages;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VoteSuccessResponse extends BaseResponse{
    private final String receipt;

    public VoteSuccessResponse(String receipt) {
        super(200);
        this.receipt = receipt;
    }
}
