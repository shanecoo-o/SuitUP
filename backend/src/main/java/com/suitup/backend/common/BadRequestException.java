package com.suitup.backend.common;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
