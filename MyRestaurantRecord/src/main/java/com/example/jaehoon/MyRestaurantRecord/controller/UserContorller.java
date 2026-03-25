package com.example.jaehoon.MyRestaurantRecord.controller;


import com.example.jaehoon.MyRestaurantRecord.dto.UserDto;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<String>signup (@RequestBody UserDto.UserSignupRequest request){
        User user = User.builder()
                .username(request.username())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build(); //실제 객체 생성
        User savedUser = userRepository.save(user);
        UserDto.UserResponseDto response = UserDto.UserResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .build();
        return ResponseEntity.ok("회원가입 성공");




//        User user = new User();
//        user.setUsername(request.username());
//        user.setNickname(request.nickname());
//        user.setPassword(passwordEncoder.encode(request.password()));
//        user.setEmail(request.email());
//
//        User savedUser = userRepository.save(user);
//
//        UserDto.UserResponseDto response = new UserDto.UserResponseDto(
//                savedUser.getId(),
//                savedUser.getUsername(),
//                savedUser.getEmail(),
//                savedUser.getNickname()
//        );
//        return ResponseEntity.ok("회원가입 성공");
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request, HttpSession session) {
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
        session.setAttribute("userId",user.getId());
        session.setAttribute("userNickname",user.getNickname());

        return ResponseEntity.ok("로그인 성공");
    }


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll(); // DB에 저장된 모든 회원 조회
    }
}
