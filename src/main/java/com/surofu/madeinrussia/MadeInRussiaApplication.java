package com.surofu.madeinrussia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MadeInRussiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MadeInRussiaApplication.class, args);
    }

}
