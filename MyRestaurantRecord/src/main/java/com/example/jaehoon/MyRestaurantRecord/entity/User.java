package com.example.jaehoon.MyRestaurantRecord.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;


@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id// 기본키지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키를 자동으로 생성
    private long id;

    private String username;
    private String password;
    private String email;
    private String nickname;



    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getNickname(){
        return nickname;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
