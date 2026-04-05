package com.popo2381.coffeeshop.global.error.code;

import com.popo2381.coffeeshop.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다");

    private final HttpStatus status;
    private final String message;
    private final String code;

    UserErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.code = name();
    }
}