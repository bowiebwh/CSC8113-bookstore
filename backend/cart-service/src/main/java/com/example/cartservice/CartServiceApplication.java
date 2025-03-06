package com.example.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.example.cartservice.model",   // 让 Spring Boot 发现 CartItem 实体类
        "com.example.common.model"        // 让 Spring Boot 发现 Book 实体类
})
@EnableJpaRepositories(basePackages = {
        "com.example.cartservice.repository",  // 让 Spring Boot 发现 CartItemRepository
        "com.example.common.repository"        // 让 Spring Boot 发现 BookRepository
})
public class CartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }

}
