package com.example.counting_center.messages;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse extends BaseResponse{
    private final String error;

    public ErrorResponse(int statusCode, String error) {
        super(statusCode);
        this.error = error;
    }

    public ErrorResponse(String error) {
        super(400);
        this.error = error;
    }
}
