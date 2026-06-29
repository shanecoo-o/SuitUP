package com.suitup.backend.common;

public class InvalidStateException extends BusinessException {

    public InvalidStateException(String message) {
        super(ErrorCode.INVALID_STATE, message);
    }
}
