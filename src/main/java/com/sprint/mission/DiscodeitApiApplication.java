package com.sprint.mission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DiscodeitApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApiApplication.class, args);
    }
}
