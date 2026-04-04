package com.popo2381.coffeeshop.global.error.code;

import lombok.Getter;
import com.popo2381.coffeeshop.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public enum MenuErrorCode implements ErrorCode {

    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_404", "존재하지 않는 메뉴입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    MenuErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}