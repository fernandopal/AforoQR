package es.fernandopal.aforoqr.api.security.config;

import es.fernandopal.aforoqr.api.exception.ApiUnauthorizedException;
import es.fernandopal.aforoqr.api.properties.CustomProperties;
import es.fernandopal.aforoqr.api.security.SecurityFilter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
    private CustomProperties restSecProps;
    private SecurityFilter securityFilter;

    @Bean
    public AuthenticationEntryPoint unauthorizedResponse() {
        return (request, response, e) -> {
            throw new ApiUnauthorizedException("Acceso no autorizado a un recurso protegido. Credenciales inválidas.");
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(restSecProps.getCors().getAllowedOrigins());
        configuration.setAllowedMethods(restSecProps.getCors().getAllowedMethods());
        configuration.setAllowedHeaders(restSecProps.getCors().getAllowedHeaders());
        configuration.setAllowCredentials(restSecProps.getCors().isAllowCredentials());
        configuration.setExposedHeaders(restSecProps.getCors().getExposedHeaders());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // fpalomo - Set CORS policy
                .cors()
                    .configurationSource(corsConfigurationSource())
                    .and()

                // fpalomo - Disable CSRF
                .csrf()
                    .disable()

                // fpalomo - Disable basic spring form auth
                .formLogin()
                    .disable()

                // fpalomo - Disable basic spring http auth
                .httpBasic()
                    .disable()

                // fpalomo - Public routes
                .authorizeRequests()
                    .antMatchers(restSecProps.getCors().getExposedApiRoutesAntMatch().toArray(String[]::new)).permitAll()
                    .and()

                // fpalomo - Authorize requests
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)

                // fpalomo - Handle 401 errors
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedResponse())
                    .and()

                // fpalomo - Session config
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
        ;
    }
}