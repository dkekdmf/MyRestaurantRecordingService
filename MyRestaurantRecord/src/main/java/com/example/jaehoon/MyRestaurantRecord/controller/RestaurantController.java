package com.example.jaehoon.MyRestaurantRecord.controller;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.repository.RestaurantRepository;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import com.example.jaehoon.MyRestaurantRecord.service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
//    @PostMapping("/add/{userId}")
//    public ResponseEntity<String> addRestaurant(@PathVariable Long userId, @RequestBody Restaurant restaurant) {
//        Restaurant saved= restaurantService.addRestaurant(userId,restaurant);
//        return ResponseEntity.ok("맛집등록성공");
//    }
    //맛집 등록
    @PostMapping("/add")
    public ResponseEntity<String> addRestaurant(HttpSession session,@RequestBody Restaurant restaurant){
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        restaurantService.addRestaurant(userId,restaurant);
        return ResponseEntity.ok("맛집 등록 성공");
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
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant restaurant,HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

        }
        try{
            restaurantService.updateRestaurant(id,restaurant,userId);
            return ResponseEntity.ok("맛집 수정 성공");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패:" + e.getMessage());
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
