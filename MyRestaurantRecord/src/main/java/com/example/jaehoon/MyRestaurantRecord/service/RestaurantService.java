package com.example.jaehoon.MyRestaurantRecord.service;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.repository.RestaurantRepository;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    @Transactional
    public Restaurant updateRestaurant(Long id, Restaurant restaurant,Long userId, MultipartFile image) throws IOException {
        Restaurant existing = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("맛집을 찾을 수 없습니다."));
        if(existing.getUser() == null){
            throw new RuntimeException("해당 게시글에 작성자 정보가 없습니다.");
        }
        if(!existing.getUser().getId().equals(userId)){
            throw new RuntimeException("본인 기록만 수정할 수 있습니다.");
        }
        if(image != null && !image.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String filePath = "C:/uploads/" + fileName;
            image.transferTo(new File(filePath));

            existing.setImgUrl("/images/" + fileName);
        }
        existing.setName(restaurant.getName());
        existing.setLocation(restaurant.getLocation());
        existing.setCategory(restaurant.getCategory());
        existing.setRating(restaurant.getRating());
        existing.setReview(restaurant.getReview());
        existing.setVisitDate(restaurant.getVisitDate());

        return restaurantRepository.save(existing);
    }
    @Transactional
    public void deleteRestaurant(Long restaurantId, Long userId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)   // 맛집의 고유번호를 받음.
                .orElseThrow(()-> new RuntimeException("존재하지 않는 맛집입니다.")); //없다면 런타임엑셉션

        if(!restaurant.getUser().getId().equals(userId)){
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        restaurantRepository.delete(restaurant);
    }
    @Transactional
    public void saveRestaurant(Restaurant restaurant, MultipartFile image, Long userId) throws IOException {
        // 1. 작성자 정보 연결
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        restaurant.setUser(user);

        // 2. 이미지 파일 처리 (이미지가 있을 때만)
        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String savePath = "C:/uploads/" + fileName;

            File file = new File(savePath);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs(); // 폴더 없으면 생성

            image.transferTo(file);
            restaurant.setImgUrl("/images/" + fileName); // DB에 저장될 경로
        }

        // 3. 최종 저장
        restaurantRepository.save(restaurant);
    }


}
