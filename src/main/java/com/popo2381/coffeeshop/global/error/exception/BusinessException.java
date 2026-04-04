package com.popo2381.coffeeshop.global.error.exception;

import com.popo2381.coffeeshop.global.error.ErrorCode;
import lombok.Getter;

// 서비스 전역에서 사용할 공통 비즈니스 예외
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }
}