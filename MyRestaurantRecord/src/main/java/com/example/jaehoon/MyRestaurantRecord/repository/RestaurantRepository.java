package com.example.jaehoon.MyRestaurantRecord.repository;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {
    List<Restaurant> findByUserId(Long userId);
    List<Restaurant> findByUserIdAndCategory(Long userId, String category);
    //가게 이름에 특정 키워드가 포함된 결과를 검색, Containg키워드를사용(LIKE 쿼리)

    // 'restaurantName' 대신 'name'을 사용 (엔티티의 변수명과 일치시켜야 함)
    List<Restaurant> findByUserIdAndNameContaining(Long userId, String name);

    // 혹은 전체 검색용
    List<Restaurant> findByNameContaining(String name);

}
