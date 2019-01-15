package com.efangtec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = {"classpath:application-mybatis-snaker.xml"})
public class EfangSnakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EfangSnakerApplication.class, args);
    }

}

