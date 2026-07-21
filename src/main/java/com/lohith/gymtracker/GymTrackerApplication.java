package com.lohith.gymtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GymTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymTrackerApplication.class, args);
    }
}
