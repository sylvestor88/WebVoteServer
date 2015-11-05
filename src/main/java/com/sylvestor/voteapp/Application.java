package com.sylvestor.voteapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Application {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(Application.class);
		
		CassOperations.cachedStatements();
	}

}
