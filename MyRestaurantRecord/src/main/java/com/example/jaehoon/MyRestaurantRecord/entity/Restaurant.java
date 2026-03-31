package com.example.jaehoon.MyRestaurantRecord.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
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

    @Id //기본키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키를 자동으로 생성
    private Long id;

    private String name;        // 맛집 이름
    private String location;    // 위치
    private String category;    // 음식 종류
    private Double rating;         // 평점
    private String review;      // 리뷰
    private LocalDate visitDate; // 방문 날짜
    private String imgUrl; //이미지 업로드
    @ManyToOne //다대일 관계
    @JoinColumn(name = "user_id") // 외래키설정
    private User user;
    private Double latitude; // 위도 Double형식으로 반환(소수점)
    private Double longitude; // 경도 Double형식으로 반환(소수점)


}
