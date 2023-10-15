package igoMoney.BE.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "/auth/**"
    };
//    private final CorsConfig corsConfig;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
//    private final LogoutHandler logoutHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // token을 사용하는 방식이기 때문에 csrf를 disable
                .csrf(c -> c.disable())
                .httpBasic(c -> c.disable())
                .formLogin(c -> c.disable())

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//                .exceptionHandling((exception)-> exception.authenticationEntryPoint(CustomAuthenticationEntryPoint
//                    .accessDeniedHandler(CustomAccessDeniedHandler)))

                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .logout(logout ->
//                        logout.logoutUrl("/auth/logout")
//                                .addLogoutHandler(logoutHandler)
//                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
//                )
                .build(); // JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig class 적용

    }

//    private final AuthenticationEntryPoint unauthorizedEntryPoint =
//            (request, response, authException) -> {
//                ErrorResponse fail = ...; // Custom error response.
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                String json = objectMapper.writeValueAsString(fail);
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                PrintWriter writer = response.getWriter();
//                writer.write(json);
//                writer.flush();
//            };

//    // PasswordEncoder는 BCryptPasswordEncoder를 사용
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
//        return new JwtAuthorizationFilter(authenticationManagerBean(), userRepository, jwtUtils, redisTemplate);
//    }
//
//    @Bean
//    public JwtExceptionFilter jwtExceptionFilter() {
//        return new JwtExceptionFilter();
//    }


}
