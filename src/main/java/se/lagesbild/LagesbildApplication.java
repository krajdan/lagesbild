package se.lagesbild;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LagesbildApplication {

    public static void main(String[] args) {
        SpringApplication.run(LagesbildApplication.class, args);
    }
}
