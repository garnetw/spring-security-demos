# 用户权限

### 自定义 SecurityFilterChain 实现角色-资源访问控制

```java
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    /** 给路径添加角色访问控制 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // ...
        http.authorizeHttpRequests()
                .antMatchers("/product/**").hasRole("USER")
                .antMatchers("/admin/**").hasRole("ADMIN");
        // ...
        return http.build();
    }

    /** 一个用户可以添加多个角色 */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password("admin")
                .passwordEncoder(passwordEncoder()::encode)
                .roles("USER", "ADMIN")
                .build();
        // ...
        return new InMemoryUserDetailsManager(admin);
    }
}
```