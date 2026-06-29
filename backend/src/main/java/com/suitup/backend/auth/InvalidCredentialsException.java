package com.suitup.backend.auth;

import com.suitup.backend.common.BusinessException;
import com.suitup.backend.common.ErrorCode;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super(ErrorCode.UNAUTHORIZED, "Email ou palavra-passe inválidos");
    }

    public InvalidCredentialsException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
