package com.example.jaehoon.MyRestaurantRecord.controller;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.error.BusinessException;
import com.example.jaehoon.MyRestaurantRecord.error.ErrorCode;
import com.example.jaehoon.MyRestaurantRecord.repository.RestaurantRepository;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import com.example.jaehoon.MyRestaurantRecord.service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


    // POST, 신규 맛집 등록
    // consumes : json 데이터와 multipart를 동시에 받기 위한 설정
    // restaurant : 맛집상세정보 (json)
    // image : 업로드할 이미지 파일
    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}) // 이미지 불러오기
    public ResponseEntity<String> addRestaurant(
            @RequestPart("restaurant") Restaurant restaurant, // JSON 데이터
            @RequestPart(value = "image", required = false) MultipartFile image, // 이미지 파일
            HttpSession session) throws IOException {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_LOGGED_IN); // 커스텀 예외처리
        }

        try {
            // 서비스의 등록 로직 호출 (이미지 포함)
            restaurantService.saveRestaurant(restaurant, image, userId); //저장
            return ResponseEntity.ok("맛집이 성공적으로 등록되었습니다! 😋");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("등록 실패: " + e.getMessage());
        }
    }
    // GET, 내 맛집만 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyRestaurants(HttpSession session){
        Long userId = (Long) session.getAttribute("userId"); // 세션에서 가져온다.

        if(userId == null){
            throw new BusinessException(ErrorCode.USER_NOT_LOGGED_IN);
        }
        List<Restaurant> myRestaurants = restaurantService.getRestaurantsByUser(userId);
        return ResponseEntity.ok(myRestaurants);
    }
    //모든 맛집 조회 (향후 수정)
//    @GetMapping("/all")
//    public List<Restaurant> getAllRestaurants(){
//        return restaurantService.getAllRestaurants();
//    }
    @GetMapping("/user/{userId}")
    public List<Restaurant> getRestaurantByUser(@PathVariable Long userId){
        return restaurantService.getRestaurantsByUser(userId);

    }
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getRestaurantsByCategory(
            @PathVariable("category") String category,
            HttpSession session){
        // 로그인 확인
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){ // 세션에 userId 없다면
            throw new BusinessException(ErrorCode.USER_NOT_LOGGED_IN); //custom

        }
        try{
            // 서비스 호출(현재 로그인한 사용자)
            List<Restaurant> restaurants = restaurantService.getRestaurantsByCategory(userId,category);
            return ResponseEntity.ok(restaurants);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("조회 실패" + e.getMessage());
        }


    }
    //맛집 수정
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateRestaurant(
            @PathVariable("id") Long id,
            @RequestPart("restaurant") Restaurant restaurant,
            @RequestPart(value = "image", required = false) MultipartFile image, // 파일 업로드
            HttpSession session) throws IOException {

        // 로그인 여부 먼저 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) { // 세션에 userId 없다면
            throw new BusinessException(ErrorCode.USER_NOT_LOGGED_IN); // 커스텀 예외처리
        }

        try {
            // 서비스 호출 (파라미터 순서: id, 데이터, userId, image)

            restaurantService.updateRestaurant(id, restaurant, userId, image);

            return ResponseEntity.ok("맛집 수정 및 이미지 업로드 성공!");

        } catch (BusinessException e) {
            // 권한 없음 등 비즈니스 로직 에러 처리
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        } catch (Exception e) {
            // 기타 서버 에러 처리
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
    // 맛집 삭제 로직
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long id, HttpSession session){
        Long userId = (Long) session.getAttribute("userId"); // 세션저장

        if (userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // custom Exception
        }
        try {
            restaurantService.deleteRestaurant(id,userId);
            return ResponseEntity.ok("맛집 삭제 성공");
        }catch (Exception e){
            throw new BusinessException(ErrorCode.ACCESS_DENIED); // custom Exception
        }
    }
    // 맛집 검색 로직
    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurants(
            @RequestParam("keyword") String keyword, // 키워드 사용
            HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null){  //본인 게시글이 아니면,
            throw new BusinessException(ErrorCode.ACCESS_DENIED);


        }
        List<Restaurant> restaurants = restaurantService.searchRestaurants(userId,keyword);
        return ResponseEntity.ok(restaurants);
    }
}
