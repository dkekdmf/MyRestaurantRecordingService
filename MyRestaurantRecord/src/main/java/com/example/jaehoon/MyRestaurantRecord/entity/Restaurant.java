package com.example.jaehoon.MyRestaurantRecord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "restaurant")
@NoArgsConstructor
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 맛집 이름
    private String location;    // 위치
    private String category;    // 음식 종류
    private Double rating;         // 평점
    private String review;      // 리뷰
    private LocalDate visitDate; // 방문 날짜
    private String imgUrl; //이미지 업로드
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
