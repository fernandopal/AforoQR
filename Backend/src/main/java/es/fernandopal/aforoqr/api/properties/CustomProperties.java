package es.fernandopal.aforoqr.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class CustomProperties {
    CorsConfig cors;
    List<String> admins;
    List<String> roles;

    @Data
    public static class CorsConfig {
        boolean allowCredentials;
        List<String> allowedOrigins;
        List<String> allowedHeaders;
        List<String> exposedHeaders;
        List<String> allowedMethods;
        List<String> exposedApiRoutesAntMatch;
        List<String> exposedApiRoutesRgxMatch;
    }
}

