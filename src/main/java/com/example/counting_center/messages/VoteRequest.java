package com.example.counting_center.messages;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
public class VoteRequest {
    @NotNull
    @NotBlank
    private String ballotID;

    @NotNull
    @NotBlank
    private String voteFor;

    @NotNull
    @NotBlank
    private String signature;
}