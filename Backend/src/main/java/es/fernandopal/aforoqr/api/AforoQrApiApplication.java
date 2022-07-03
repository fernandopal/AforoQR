package es.fernandopal.aforoqr.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class AforoQrApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AforoQrApiApplication.class, args);
    }
}