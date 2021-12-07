package com.example.counting_center.messages;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
public class EncryptedVoteRequest {
    @NotNull
    @NotBlank
    private String sessionKey;

    @NotNull
    @NotBlank
    private String message;
}
