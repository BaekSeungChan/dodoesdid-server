package com.backend.dodoesdidserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DodoesdidServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DodoesdidServerApplication.class, args);
    }

}
