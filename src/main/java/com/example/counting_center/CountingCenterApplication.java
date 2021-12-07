package com.example.counting_center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CountingCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CountingCenterApplication.class, args);
    }

}
