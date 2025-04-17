package tn.esprit.pidevspringboot;

<<<<<<< HEAD
=======
import org.springframework.context.annotation.Bean;
>>>>>>> origin/GestionActivite-Sportive
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
<<<<<<< HEAD
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Permet toutes les routes
                .allowedOrigins("http://localhost:4200")  // URL de votre frontend Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Méthodes HTTP autorisées
                .allowedHeaders("*")  // Autorise tous les en-têtes
                .allowCredentials(true);
    }
}

=======

public class WebConfig {

        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**")
                            .allowedOrigins("http://localhost:4200") // Frontend
                            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                            .allowedHeaders("*")
                            .allowCredentials(true);

                }
            };
        }
}
>>>>>>> origin/GestionActivite-Sportive
