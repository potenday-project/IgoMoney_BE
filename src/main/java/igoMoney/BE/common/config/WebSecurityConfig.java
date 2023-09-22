package igoMoney.BE.common.config;

//import igoMoney.BE.common.jwt.*;
//import igoMoney.BE.repository.UserRepository;
//import igoMoney.BE.common.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CorsConfig corsConfig;
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//    private final UserRepository userRepository;
//    private final JwtUtils jwtUtils;
//    private final RedisTemplate redisTemplate;
//    private final AuthenticationConfiguration authenticationConfiguration;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf(c -> c.disable())
                .httpBasic(c -> c.disable())
                .headers(c -> c.frameOptions(f -> f.disable()).disable())
                .formLogin(c -> c.disable())

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .accessDeniedHandler(jwtAccessDeniedHandler)


//                .addFilter(corsConfig.corsFilter())
//                .addFilter(jwtAuthorizationFilter())
//                .addFilterBefore(jwtExceptionFilter(), JwtAuthorizationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                    //.requestMatchers("/**").hasRole("ROLE_USER")
                    .requestMatchers("/**").permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/auth/login/apple/page").permitAll()
                    //.requestMatchers("/auth/login/kakao/**").permitAll() // 로그인 api
                    //.anyRequest().authenticated() // 그 외 인증 없이 접근X
                )
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
