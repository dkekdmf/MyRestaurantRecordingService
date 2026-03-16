package com.example.jaehoon.MyRestaurantRecord.controller;


import com.example.jaehoon.MyRestaurantRecord.dto.UserDto;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserContorller {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserContorller(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signup (@RequestBody UserDto.UserSignupRequest request){
        User user = new User();
        user.setUsername(request.username());
        user.setNickname(request.nickname());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());

        User savedUser = userRepository.save(user);

        UserDto.UserResponseDto response = new UserDto.UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getNickname()
        );
        return ResponseEntity.ok("회원가입 성공");
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("비밀번호가 틀렸습니다.");
        }

        return ResponseEntity.ok("로그인 성공");
    }


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll(); // DB에 저장된 모든 회원 조회
    }
}
