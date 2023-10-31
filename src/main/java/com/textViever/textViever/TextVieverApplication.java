package com.textViever.textViever;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.textViever.textViever.service.Console;

@SpringBootApplication
public class TextVieverApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(TextVieverApplication.class, args);
		Console.startClass();
		System.out.println("stated two seperate thread");
	}

}
