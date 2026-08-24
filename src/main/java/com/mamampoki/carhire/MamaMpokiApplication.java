package com.mamampoki.carhire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MamaMpokiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MamaMpokiApplication.class, args);
    }
}
