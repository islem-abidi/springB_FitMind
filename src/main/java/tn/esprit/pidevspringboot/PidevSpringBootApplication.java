package tn.esprit.pidevspringboot;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@Configuration
public class PidevSpringBootApplication {
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Tunis"));
        System.out.println("✅ Fuseau horaire défini : " + TimeZone.getDefault());
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    public static void main(String[] args) {
        SpringApplication.run(PidevSpringBootApplication.class, args);
    }

}
