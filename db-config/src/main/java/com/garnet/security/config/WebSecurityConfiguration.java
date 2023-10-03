package com.garnet.security.config;

import com.garnet.security.config.filter.Http401UnauthorizedEntryPoint;
import com.garnet.security.config.filter.Http403AccessDeniedHandler;
import com.garnet.security.config.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Autowired
    private Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint; // 登录失败处理器

    @Autowired
    private Http403AccessDeniedHandler http403AccessDeniedHandler;  // 权限认证失败处理器

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // 关闭csrf和frameOptions，如果不关闭会影响前端请求接口
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.cors();    // 开启跨域以便前端调用接口，否则403 Not allowed

        http.authorizeHttpRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers("/product/**").hasAuthority("USER")
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(http401UnauthorizedEntryPoint)
                    .accessDeniedHandler(http403AccessDeniedHandler);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
