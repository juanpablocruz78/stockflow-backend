package com.stockflow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StockflowBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockflowBackendApplication.class, args);
    }

}
