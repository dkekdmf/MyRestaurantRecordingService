package com.example.jaehoon.MyRestaurantRecord.service;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.repository.RestaurantRepository;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public RestaurantService(RestaurantRepository restaurantRepository,UserRepository userRepository){
        this.restaurantRepository= restaurantRepository;
        this.userRepository = userRepository;
    }
    public Restaurant addRestaurant(Long userId,Restaurant restaurant){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("회원이 존재하지 않습니다."));
        restaurant.setUser(user);
        return restaurantRepository.save(restaurant);
    }
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getRestaurantsByUser(Long userId) {
        return restaurantRepository.findByUserId(userId);
    }

    public List<Restaurant> getRestaurantsByCategory(String category) {
        return restaurantRepository.findByCategory(category);
    }
    public Restaurant updateRestaurant(Long id, Restaurant restaurant) {
        Restaurant existing = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("맛집을 찾을 수 없습니다."));
        existing.setName(restaurant.getName());
        existing.setLocation(restaurant.getLocation());
        existing.setCategory(restaurant.getCategory());
        existing.setRating(restaurant.getRating());
        existing.setReview(restaurant.getReview());
        existing.setVisitDate(restaurant.getVisitDate());
        existing.setImgUrl(restaurant.getImgUrl());
        return restaurantRepository.save(existing);
    }

    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }


}
