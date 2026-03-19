package com.example.jaehoon.MyRestaurantRecord.controller;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.repository.RestaurantRepository;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import com.example.jaehoon.MyRestaurantRecord.service.RestaurantService;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/add/{userId}")
    public ResponseEntity<String> addRestaurant(@PathVariable Long userId, @RequestBody Restaurant restaurant) {
        Restaurant saved= restaurantService.addRestaurant(userId,restaurant);
        return ResponseEntity.ok("맛집등록성공");
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
    public ResponseEntity<String> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant restaurant) {
       Restaurant updated = restaurantService.updateRestaurant(id,restaurant);
       return ResponseEntity.ok("맛집 수정 성공");
    }
    @DeleteMapping("/{id}")
    public String deleteRestaurant(@PathVariable Long id){
        restaurantService.deleteRestaurant(id);
        return "맛집 삭제 성공";
    }



}
