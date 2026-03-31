package com.example.jaehoon.MyRestaurantRecord.dto;

import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestaurantRequestDto {
    private String name;
    private String location;
    private Double latitude;  // 카카오맵에서 넘어오는 위도
    private Double longitude; // 카카오맵에서 넘어오는 경도
    private String category;
    private Double rating;
    private String review;
    private String visitDate;
}
