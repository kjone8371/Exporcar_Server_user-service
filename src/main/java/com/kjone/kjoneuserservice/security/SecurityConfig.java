package com.kjone.kjoneuserservice.security;



import com.kjone.kjoneuserservice.security.cookie.CookieProvider;
import com.kjone.kjoneuserservice.security.jwt.JwtAuthenticationFilter;
import com.kjone.kjoneuserservice.security.jwt.JwtProvider;
import com.kjone.kjoneuserservice.security.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailService customUserDetailService;
    private final CookieProvider cookieProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors().and() // CORS 설정을 활성화한다.
//                .csrf().disable() // CSRF 기능을 비활성화한다.
//                .authorizeRequests()
//                .requestMatchers("/public/**").permitAll() // 특정 경로에 대한 인증 없이 접근을 허용한다.
//                .anyRequest().authenticated(); // 그 외의 모든 요청은 인증을 필요로 한다.
//    }
//
//
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:8000")); // 허용할 출처 설정
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // 허용할 HTTP 메소드 설정
//        configuration.setAllowedHeaders(List.of("*")); // 허용할 헤더 설정
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정을 적용한다.
//        return source;
//    }
//
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // CSRF 토큰을 쿠키에 저장
//                )
//                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리 비활성화
//                .authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests
//                                .requestMatchers(HttpMethod.POST, "/v1/sign/signup", "/v1/sign/signin").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/auth/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/v1/user/me").hasAnyRole("USER", "ADMIN", "OWNER", "EMPLOYEE")
//                                .requestMatchers(HttpMethod.GET, "/v1/sign/signout").authenticated()
//                                .requestMatchers(HttpMethod.DELETE, "/v1/sign/delete").hasRole("USER")
//                                .requestMatchers("/favicon.ico").permitAll() // Favicon 접근 허용
//                                .anyRequest().denyAll()
//                )
//                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, cookieProvider, customUserDetailService), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // 프론트엔드 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                        .allowedHeaders("*") // 허용할 헤더
                        .allowCredentials(true); // 자격 증명(쿠키 등)을 허용할지 여부
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST, "/v1/sign/signup", "/v1/sign/signin").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/user/me").hasAnyRole("USER", "ADMIN", "OWNER", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/v1/sign/signout").authenticated() // 인증된 유저만
                        .requestMatchers(HttpMethod.DELETE, "/v1/sign/delete").authenticated() // 인증된 유저만
                        .requestMatchers("/favicon.ico").permitAll() // Favicon에 대한 접근 허용
                        .anyRequest().denyAll());

                http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider,cookieProvider, customUserDetailService),  UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
