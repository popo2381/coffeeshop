package com.popo2381.coffeeshop.global.error.code;

import com.popo2381.coffeeshop.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PointErrorCode implements ErrorCode {

    INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "충전 금액은 1 이상이어야 합니다"),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다");

    private final HttpStatus status;
    private final String message;

    PointErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }
}