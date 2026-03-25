package com.example.jaehoon.MyRestaurantRecord.controller;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.repository.RestaurantRepository;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import com.example.jaehoon.MyRestaurantRecord.service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.List;

@RestController
@RequestMapping("/restaurant")

public class RestaurantController {
    private final RestaurantService restaurantService;
    public RestaurantController(RestaurantService restaurantService){
        this.restaurantService = restaurantService;
    }


    //맛집 등록
    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> addRestaurant(
            @RequestPart("restaurant") Restaurant restaurant, // JSON 데이터
            @RequestPart(value = "image", required = false) MultipartFile image, // 이미지 파일
            HttpSession session) throws IOException {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // 서비스의 등록 로직 호출 (이미지 포함)
            restaurantService.saveRestaurant(restaurant, image, userId);
            return ResponseEntity.ok("맛집이 성공적으로 등록되었습니다! 😋");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("등록 실패: " + e.getMessage());
        }
    }
    // 내 맛집만 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyRestaurants(HttpSession session){
        Long userId = (Long) session.getAttribute("userId");

        if(userId == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        List<Restaurant> myRestaurants = restaurantService.getRestaurantsByUser(userId);
        return ResponseEntity.ok(myRestaurants);
    }
    //모든 맛집 조회
    @GetMapping("/all")
    public List<Restaurant> getAllRestaurants(){
        return restaurantService.getAllRestaurants();
    }
    @GetMapping("/user/{userId}")
    public List<Restaurant> getRestaurantByUser(@PathVariable Long userId){
        return restaurantService.getRestaurantsByUser(userId);

    }
    @GetMapping("/category/{category}")
    public List<Restaurant> getRestaurantByCategory(@PathVariable String category){
        return restaurantService.getRestaurantsByCategory(category);
    }
    //맛집 수정
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateRestaurant(
            @PathVariable("id") Long id,
            @RequestPart("restaurant") Restaurant restaurant,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpSession session) throws IOException {

        // 1. 로그인 여부 먼저 확인 (보안)
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // 2. 서비스 호출 (파라미터 순서: id, 데이터, 유저ID, 이미지)
            // 서비스 메서드 정의의 매개변수 순서와 일치하는지 꼭 확인하세요!
            restaurantService.updateRestaurant(id, restaurant, userId, image);

            return ResponseEntity.ok("맛집 수정 및 이미지 업로드 성공! ✨");

        } catch (RuntimeException e) {
            // 권한 없음 등 비즈니스 로직 에러 처리
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 실패: " + e.getMessage());
        } catch (Exception e) {
            // 기타 서버 에러 처리
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long id, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            restaurantService.deleteRestaurant(id,userId);
            return ResponseEntity.ok("맛집 삭제 성공");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }




}
