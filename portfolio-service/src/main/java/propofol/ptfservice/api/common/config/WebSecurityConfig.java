package propofol.ptfservice.api.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import propofol.ptfservice.api.common.filter.PreFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PreFilter preFilter;

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.formLogin().disable()
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors();

        http.authorizeRequests()
                .antMatchers("/api/v1/**")
                .authenticated()
                .and()
                .addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class);
    }

    // cors 관련 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 모든 IP 응답 허용
        config.addAllowedHeader("*"); // 모든 헤더에 응답을 허용
        config.addAllowedMethod("*"); // 모든 post, get, delete, fetch 허용

        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
