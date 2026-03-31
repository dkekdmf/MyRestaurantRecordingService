package com.example.jaehoon.MyRestaurantRecord.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode { // 커스텀 에러코드
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 맛집을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN,"해당 요청에 대한 권한이 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST,"입력값이 올바르지 않습니다."),
    USER_NOT_LOGGED_IN(HttpStatus.UNAUTHORIZED,"로그인이 필요합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다."),
    NOT_OWNER(HttpStatus.UNAUTHORIZED,"본인의 기록만 수정/삭제할 수 있습니다.");
    private HttpStatus Status;
    private String message;
}
