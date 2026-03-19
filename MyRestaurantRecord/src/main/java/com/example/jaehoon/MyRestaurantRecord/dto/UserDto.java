package com.example.jaehoon.MyRestaurantRecord.dto;

import lombok.Getter;
import lombok.Setter;

public class UserDto {
    // 회원가입 요청 DTO
    public record UserSignupRequest(String username, String nickname, String email, String password) {}

    // 회원가입 응답 DTO
    public record UserResponseDto(Long id, String username, String nickname, String email) {}


    @Getter
    @Setter
    public class LoginRequest{
        String username;
        String password;
    }
}
