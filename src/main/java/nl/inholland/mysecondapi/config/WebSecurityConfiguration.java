package nl.inholland.mysecondapi.config;

import jakarta.servlet.http.HttpServletResponse;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.security.JwtFilter;
import nl.inholland.mysecondapi.security.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {


    private final JwtFilter jwtFilter;


    public WebSecurityConfiguration(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers-> headers.frameOptions(frameOptionsConfig-> frameOptionsConfig.sameOrigin()))
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth //dit moeten we echt nog even goed structureren
                        .requestMatchers("/api/users/login").permitAll() //log in post & get
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll() //Make new user / register
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll() //kik
                        .requestMatchers("/api/users/approve/{id}").hasAnyRole("EMPLOYEE", "ADMINISTRATOR") //aprove customer acc
                        .requestMatchers("/api/users/deny/{id}").hasAnyRole("EMPLOYEE", "ADMINISTRATOR") //deny customer acc

                        .requestMatchers(HttpMethod.GET,"/api/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/accounts/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/users").permitAll()   //meerdere api endpoints ivm verschillende update rechten??
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("EMPLOYEE", "ADMINISTRATOR")
                        .requestMatchers("/api/accounts").authenticated()
                        .requestMatchers("/api/transactions/**").permitAll() //anders doet hij het niet

                        //dit weghalen bij inleveren
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("v3/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {  // 401
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.getWriter().write("Unauthorized: Invalid credentials");
                        })
                        .accessDeniedHandler((req, res, e) -> {  // 403
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.getWriter().write("Forbidden: Missing required role");
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5174", "another URL"));
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "another URL"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
