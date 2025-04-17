package tn.esprit.pidevspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "tn.esprit.pidevspringboot")
public class PidevSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PidevSpringBootApplication.class, args);
    }

}
