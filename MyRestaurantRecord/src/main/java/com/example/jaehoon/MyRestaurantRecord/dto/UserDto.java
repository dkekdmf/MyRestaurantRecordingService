package com.example.jaehoon.MyRestaurantRecord.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserDto {
    // 회원가입 요청 dto
    public record UserSignupRequest(String username, String nickname, String email, String password) {}

    // 회원가입 응답 dto
    @Builder
    public record UserResponseDto(Long id, String username, String nickname, String email) {}


    @Getter
    @Setter
    public class LoginRequest{
        String username;
        String password;
    }
}
