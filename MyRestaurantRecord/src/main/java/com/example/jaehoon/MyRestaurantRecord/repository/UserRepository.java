package com.example.jaehoon.MyRestaurantRecord.repository;

import com.example.jaehoon.MyRestaurantRecord.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> { //jpa 사용(User,기본키타입)
    Optional <User> findByUsername(String username);
}
