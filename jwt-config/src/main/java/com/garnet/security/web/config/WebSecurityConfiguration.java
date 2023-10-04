package com.garnet.security.web.config;

import com.garnet.security.web.filter.Http401UnauthorizedEntryPoint;
import com.garnet.security.web.filter.Http403AccessDeniedHandler;
import com.garnet.security.web.filter.JwtLoginFilter;
import com.garnet.security.web.filter.MyAccessFilter;
import com.garnet.security.web.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Autowired
    Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint; // 登录失败处理器

    @Autowired
    Http403AccessDeniedHandler http403AccessDeniedHandler;  // 权限认证失败处理器

    @Autowired
    JwtLoginFilter jwtLoginFilter;  // JWT自定义认证处理器

    @Autowired
    MyAccessFilter myAccessFilter;  // 自定义权限认证处理器

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // 关闭csrf和frameOptions，如果不关闭会影响前端请求接口
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.cors();    // 开启跨域以便前端调用接口，否则403 Not allowed

        // 禁用Session，启用JWT
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
//                .antMatchers("/product/**").authenticated()
//                .antMatchers("/admin/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                    .addFilterBefore(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(myAccessFilter, FilterSecurityInterceptor.class)
                .exceptionHandling()
                    .authenticationEntryPoint(http401UnauthorizedEntryPoint)
                    .accessDeniedHandler(http403AccessDeniedHandler);

        return http.build();
    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userDetailsService());
//        auth.setPasswordEncoder(passwordEncoder());
//        return auth;
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new CustomUserDetailsService();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
