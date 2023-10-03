package com.garnet.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.garnet.security.mapper")	// myBatis 配置
public class DbConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbConfigApplication.class, args);
	}

}
