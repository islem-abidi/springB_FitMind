package tn.esprit.pidevspringboot.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
public class CaptchaValidatorService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean isCaptchaValid(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "secret=" + recaptchaSecret + "&response=" + token;
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                VERIFY_URL, HttpMethod.POST, request, Map.class
        );

        Map<String, Object> responseBody = response.getBody();

        boolean success = (boolean) responseBody.get("success");
        double score = (Double) responseBody.get("score");

        System.out.println("✅ CAPTCHA success=" + success + " | score=" + score); // 👈 LOG DEBUG

        return success && score >= 0.5;
    }

}
