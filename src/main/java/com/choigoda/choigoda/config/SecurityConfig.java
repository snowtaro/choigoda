package com.choigoda.choigoda.config;

import com.choigoda.choigoda.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.choigoda.choigoda.security.JwtTokenFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(CustomUserDetailsService uds, JwtTokenFilter jwtTokenFilter) {
        this.userDetailsService = uds;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/", "/index.html", "/css/**", "/js/**", "/images/**", "/favicon.ico"
        );
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // UserDetailsService를 생성자에 전달
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        // 그 뒤에 PasswordEncoder 설정
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())                                  // CORS 설정 (deprecated .cors() 대신)
                .csrf(AbstractHttpConfigurer::disable)                 // CSRF 비활성화
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션 사용 안 함
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/api/auth/**","/home","/home.html"
                        ).permitAll()                                      // 이 경로들은 모두 허용
                        .anyRequest().authenticated()                     // 그 외 요청은 인증 필요
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    // AuthenticationManager 빈 등록 (AuthController 에서 사용)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 비밀번호 암호화를 위한 PasswordEncoder 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
