package com.example.jaehoon.MyRestaurantRecord.service;

import com.example.jaehoon.MyRestaurantRecord.entity.Restaurant;
import com.example.jaehoon.MyRestaurantRecord.entity.User;
import com.example.jaehoon.MyRestaurantRecord.error.BusinessException;
import com.example.jaehoon.MyRestaurantRecord.error.ErrorCode;
import com.example.jaehoon.MyRestaurantRecord.repository.RestaurantRepository;
import com.example.jaehoon.MyRestaurantRecord.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

// 맛집 기록 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    // 생성자 주입(repository)
    public RestaurantService(RestaurantRepository restaurantRepository,UserRepository userRepository){
        this.restaurantRepository= restaurantRepository;
        this.userRepository = userRepository;
    }
    // 맛집 추가 userId: 작성자 Id Restaurant: 맛집 정보 객체
    public Restaurant addRestaurant(Long userId,Restaurant restaurant){
        User user = userRepository.findById(userId) //사용자 존재 여부 확인
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND)); //커스텀 예외
        restaurant.setUser(user);
        return restaurantRepository.save(restaurant); // 저장된 맛집 엔티티
    }
    // 향후 수정
    //public List<Restaurant> getAllRestaurants() {
      //  return restaurantRepository.findAll();
    // }

    public List<Restaurant> getRestaurantsByUser(Long userId) {
        return restaurantRepository.findByUserId(userId);
    }

    public List<Restaurant> getRestaurantsByCategory(Long userId,String category) {
        return restaurantRepository.findByUserIdAndCategory(userId,category);
    }
    // id : 수정할 맛집 Primarykey restaurant : 수정할 맛집객체 userId : 수정 요청자 id image: 새이미지 파일
    @Transactional
    public Restaurant updateRestaurant(Long id, Restaurant restaurant,Long userId, MultipartFile image) throws IOException {
        // 기존 게시글 조회
        Restaurant existing = restaurantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        // 권한 확인
        if(existing.getUser() == null){
            throw new BusinessException(ErrorCode.NOT_OWNER);
        }
        if(!existing.getUser().getId().equals(userId)){
            throw new BusinessException(ErrorCode.NOT_OWNER);
        }
        // 이미지 파일처리
        if(image != null && !image.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String filePath = "C:/uploads/" + fileName;
            image.transferTo(new File(filePath));
            // 웹에서 접근 가능한 url 경로 설정
            existing.setImgUrl("/images/" + fileName);
        }
        // 나머지 필드 업데이트
        existing.setName(restaurant.getName());
        existing.setLocation(restaurant.getLocation());
        existing.setCategory(restaurant.getCategory());
        existing.setRating(restaurant.getRating());
        existing.setReview(restaurant.getReview());
        existing.setVisitDate(restaurant.getVisitDate());
        existing.setLatitude(restaurant.getLatitude());
        existing.setLongitude(restaurant.getLongitude());
        return restaurantRepository.save(existing);
    }
    // 맛집 삭제
    @Transactional //삭제 중 예기치 못한 에러 발생 방지
    // restaurantId : 삭제할 맛집 primarykey userId : 삭제 요청자 id
    public void deleteRestaurant(Long restaurantId, Long userId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)   // 맛집의 고유번호
                .orElseThrow(()-> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND)); //없다면 custom exception

        if(!restaurant.getUser().getId().equals(userId)){
            throw new BusinessException(ErrorCode.NOT_OWNER); // custom exception
        }
        restaurantRepository.delete(restaurant);
    }
    @Transactional //저장 중 예기치 못한 에러 발생 방지
    // restaurant : 맛집 엔티티 image : 이미지 파일 userId : 작성자 id
    public void saveRestaurant(Restaurant restaurant, MultipartFile image, Long userId) throws IOException {
        // 1. 작성자 정보 연결
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
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
    // 키워드를 이용한 맛집 검색 keyword : 검색어
    public List<Restaurant> searchRestaurants(Long userId,String keyword){
        //키워드가 비어있을경우 전체조회
        if(keyword == null || keyword.isEmpty()){
            return restaurantRepository.findByUserId(userId);
        }
        //키워드가 포함된 데이터 조회
        return restaurantRepository.findByUserIdAndNameContaining(userId,keyword);


    }



}
