package com.example.counting_center.messages;

import lombok.Getter;

@Getter
public abstract class BaseResponse {
    private final int statusCode;

    public BaseResponse(int statusCode) {
        this.statusCode = statusCode;
    }
}
