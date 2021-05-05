package com.modzo.foodnutrition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FoodNutritionApplication {

	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "/home/modestas/TOOLS/geckodriver-v0.24.0-linux64/geckodriver");
		SpringApplication.run(FoodNutritionApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

}