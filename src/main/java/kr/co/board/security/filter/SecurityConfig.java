package kr.co.board.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.board.security.config.CookieProvider;
import kr.co.board.security.config.JwtProvider;
import kr.co.board.security.config.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final RefreshTokenService refreshTokenService;
    private final CookieProvider cookieProvider;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        LoginFilter loginFilter =
                new LoginFilter(authenticationManager, jwtProvider, objectMapper, refreshTokenService, cookieProvider);
        loginFilter.setFilterProcessesUrl("/login");

        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtFilter(jwtProvider), LoginFilter.class)
                .addFilterAt(
                        loginFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                                        .accessDeniedHandler(customAccessDeniedHandler));
        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 도메인 목록 설정
        config.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:3000", "http://localhost:8080", "http://192.168.0.12:3000", "null"));

        // 모든 HTTP 메서드 허용
        config.setAllowedMethods(
                Arrays.asList(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.OPTIONS.name()));

        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("token", "Set-Cookie"));

        // 인증 정보 포함 허용
        config.setAllowCredentials(true);

        // CORS 정책 캐시 시간 설정 (1시간)
        config.setMaxAge(3600L);

        // 모든 경로에 대해 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
