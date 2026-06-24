package se.lagesbild.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Tillater anrop fran en separat frontend under utveckling (t.ex. Vite pa 5173).
 * I produktion serveras frontend fran samma origin sa CORS behovs inte da.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH");
    }
}
