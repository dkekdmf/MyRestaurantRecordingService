package com.example.jaehoon.MyRestaurantRecord.controller;


import com.example.jaehoon.MyRestaurantRecord.dto.UserDto;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.error.BusinessException;
import com.example.jaehoon.MyRestaurantRecord.error.ErrorCode;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController //JSOn 형태로 반환하는 Restfulcontroller임을 선언
@RequestMapping("/api/users") // 이 클래스내의 모든 메소드는 /api/users 경로를 가진다.
public class UserContorller {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입
    public UserContorller(UserRepository userRepository, PasswordEncoder passwordEncoder){ //생성자 주입
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // ResponseEntity : Spring Framework에서 제공하는 클래스로, HTTP 응답(Response)의 전체 메시지를 구성하는 역할을 합니다.
    @PostMapping("/signup") //HTTP POST 요청을 처리하여 새로운 사용자를 생성
    public ResponseEntity<String> signup (@RequestBody UserDto.UserSignupRequest request){ //RequestBody 클라이언트가 보낸 JSON 데이터를 UserSignupRequest 객체로 변환
        if(userRepository.findByUsername(request.username()).isPresent()){ // 아이디 중복 체크, 이미 존재하는 username인지 db에서 확인
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용중인 계정입니다.");
        }
        // User entity 생성 및 비밀번호 암호화
        // security에서 제공하는 passwordEncoder를 사용해 비밀번호를 해싱하여 저장
        // Builder패턴
        User user = User.builder()
                .username(request.username())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build(); //실제 객체 생성
        User savedUser = userRepository.save(user); //db에 저장
        UserDto.UserResponseDto response = UserDto.UserResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .build();
        return ResponseEntity.ok("회원가입 성공");




    }
    // 로그인 기능
    // HttpSession : 서버 측에 사용자 정보를 유지하기 위한 세션 객체
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request, HttpSession session) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            User user = userRepository.findByUsername(username).orElse(null);
            // 사용자 존재 여부 확인
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("존재하지 않는 사용자입니다.");
            }
            // 비밀번호 일치 확인
            // 입력박은 생 비밀번호와 db의 암호화 비밀번호를 비교
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("비밀번호가 틀렸습니다.");
            }
            // 데이터 검증: 세션에 넣응 정보가 비어있지 않은지 체크
            // 세션 저장 시 데이터가 null인지 확인
            if (user.getId() == null || user.getNickname() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보(ID/닉네임)가 누락되었습니다.");
            }
            // 세션 생성 : 서버 메모리에 사용자 식별 정보를 저장
            // 이후 클라이언트는 쿠키를 통해 본인임을 증명
            session.setAttribute("userId", user.getId());
            session.setAttribute("userNickname", user.getNickname());

            return ResponseEntity.ok("로그인 성공");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 id/닉네임 누락");
        }

    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session){
        //현재 요청과 연결된 세션을 완전히 무효화
        session.invalidate();;
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
    // 현재 로그인한 사용자의 닉네임 가져오기
    // 세션에 저장된 정보를 바탕으로 누가 로그인 중인지 확인
    @GetMapping("/nickname")
    public ResponseEntity<?> getUserNickname(HttpSession session){
        //  세션에서 nickname 속성을 읽어온다.
        String nickname = (String) session.getAttribute("userNickname");
        // 만약 닉네임이 없다면 로그인되지 않은 상태
        if(nickname == null){
            throw new BusinessException(ErrorCode.USER_NOT_LOGGED_IN);
        }
        // JSON 형태로 반환하기 위해 MAP(KEY-VALUE) 구조를 사용한다.
        return ResponseEntity.ok(Collections.singletonMap("nickname",nickname));
    }

    // 전체 회원조회
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll(); // DB에 저장된 모든 User 엔티티 리스트를 반환
    }
}
