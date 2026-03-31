package com.example.jaehoon.MyRestaurantRecord.error;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{ // 사용자 정의 예외 처리 (Runtime Exception 상속)
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
