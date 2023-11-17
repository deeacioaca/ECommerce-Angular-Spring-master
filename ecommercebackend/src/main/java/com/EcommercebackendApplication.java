package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommercebackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommercebackendApplication.class, args);
//
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String password = "ion.ion123";
//        String encodedPassword = passwordEncoder.encode(password);
//
//        System.out.println("Encoded Password: " + encodedPassword);
    }

}
