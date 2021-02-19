package com.example.springboot;

import net.minidev.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.*;


@SpringBootApplication
public class Application {

	public static void main(String[] args) throws IOException {
//        Download archives
//		downloadFile(DOWNLOAD_LINK, "train.zip");
//        UnZip
//		unzip(new File("D:/WSB/Network/Application/train.zip"), EXTRACT_PATH);

//        Start training
//		SpringApplication.run(Application.class, args);

		SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
	}

}
