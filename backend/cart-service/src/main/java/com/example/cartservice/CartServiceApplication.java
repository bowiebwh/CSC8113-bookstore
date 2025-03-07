package com.example.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.example.cartservice.model",   // let Spring Boot find CartItem
        "com.example.common.model"        // let Spring Boot find Book
})
@EnableJpaRepositories(basePackages = {
        "com.example.cartservice.repository",  // let Spring Boot find CartItemRepository
        "com.example.common.repository"        // let Spring Boot find BookRepository
})
public class CartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }

}
