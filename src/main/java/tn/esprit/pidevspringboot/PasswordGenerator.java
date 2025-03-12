package tn.esprit.pidevspringboot;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("azerty");
        System.out.println("Mot de passe encodé : " + encodedPassword);
    }
}
