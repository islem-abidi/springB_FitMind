package tn.esprit.pidevspringboot.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class IpLocationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getLocation(String ip) {
        try {
            String url = "http://ip-api.com/json/" + ip;
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            return Map.of("country", "Unknown", "city", "Unknown");
        }
    }
}
