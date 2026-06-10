package com.waste;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.waste.mapper")
public class WasteManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WasteManagementApplication.class, args);
    }
}
