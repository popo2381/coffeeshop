package com.popo2381.coffeeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CoffeeshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeshopApplication.class, args);
    }

}
