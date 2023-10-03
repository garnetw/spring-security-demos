# 用户权限

### 自定义 SecurityFilterChain 实现角色-资源访问控制

```java
// 给路径添加角色访问控制
http.authorizeHttpRequests()
        .antMatchers("/product/**").hasRole("USER")
        .antMatchers("/admin/**").hasRole("ADMIN");

// 一个用户可以添加多个角色
UserDetails admin = User.builder()
                        .username("admin")
                        .password("admin")
                        .passwordEncoder(passwordEncoder()::encode)
                        .roles("USER", "ADMIN")
                        .build();
```