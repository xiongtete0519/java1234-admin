package com.java1234;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.java1234.mapper")
@SpringBootApplication
public class Java1234AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(Java1234AdminApplication.class, args);
    }

}
