package propofol.userservice.api.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import propofol.userservice.api.common.filter.PreFilter;

// Spring Security 설정
@Configuration
// 스프링 시큐리티 필터가 스프링 필터 체인에 등록된다.
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PreFilter preFilter;

    // 빈 등록
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // 기본으로 제공하는 formLogin 비활성화
        http.formLogin().disable()
                // http basic auth 사용 x
                .httpBasic().disable()
                // // 인증 정보를 서버에 담아두지 않음. (원래는 서버 측에서 세션에 넣어두는데 이를 비활성화)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors();

        http
                // 요청에 대한 권한 지정
                .authorizeHttpRequests()
//                // 우선 모든 경로에 대해 접근 허용
//                .antMatchers("/**")
//                .permitAll();

                // /members/**로 들어오는 사용자들은 인증 필요
                .antMatchers("/api/v1/members/**")
                .authenticated()
                .and()
                // 커스텀 필터 추가
                .addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class);

        // h2-console을 보기 위한 설정
        http.headers()
                .frameOptions().disable();
    }

    // cors 관련 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 서버가 응답 시 json을 자바스크립트에서 처리할 수 있도록 할지 설정
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 모든 IP 응답 허용
        config.addAllowedHeader("*"); // 모든 헤더에 응답을 허용
        config.addAllowedMethod("*"); // 모든 post, get, delete, fetch 허용

        // /api로 들어오는 주소들은 모두 이 config 설정 적용
        source.registerCorsConfiguration("/api/**", config);

        return source;
    }




}
