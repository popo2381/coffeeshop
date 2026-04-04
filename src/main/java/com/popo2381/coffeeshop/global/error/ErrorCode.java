package com.popo2381.coffeeshop.global.error;

import org.springframework.http.HttpStatus;

// 도메인별 ErrorCode enum이 공통으로 구현하는 인터페이스
public interface ErrorCode {

    HttpStatus getStatus();

    String getCode();

    String getMessage();
}