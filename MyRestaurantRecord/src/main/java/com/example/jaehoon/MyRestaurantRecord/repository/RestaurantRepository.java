package com.example.jaehoon.MyRestaurantRecord.repository;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {
    List<Restaurant> findByUserId(Long userId);
    List<Restaurant> findByCategory(String category);
}
