package com.popo2381.coffeeshop.global.error;

import java.time.LocalDateTime;

// 예외 발생 시 내려줄 공통 에러 응답
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message
) {

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}