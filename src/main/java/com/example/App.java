package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.logging.Logger;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

@RestController
@RequestMapping("/api")
class MyController {
    @GetMapping("/status")
    public String status() {
        return "UP";
    }
}

@Component
class LogService implements ApplicationRunner {
    private static final Logger logger = Logger.getLogger(LogService.class.getName());
    private static final String[] WORDS = {
            "cloud", "docker", "kubernetes", "java", "maven", "aws", "spring", "microservice", "scalability",
            "observability", "monitoring", "resilience", "automation", "performance", "infra", "logs",
            "debugging", "reliability", "containerization", "serverless"
    };
    private final Random random = new Random();

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Aguarda a aplicação iniciar completamente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            while (true) {
                String randomWord = WORDS[random.nextInt(WORDS.length)];
                logger.info("Log gerado: " + randomWord);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}