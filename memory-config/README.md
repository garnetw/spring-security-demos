#  在内存中添加用户 

在Spring Security 中自定义用户账号信息，用于访问系统。  
共有两种实现方法，其中第一种方法已经弃用，目前框架更推荐第二种方法。  
值得一提的是，这两种方法都可以在内存中添加多个账号信息，比起默认配置更方便。  

输入用户名和密码后，成功登录的用户信息会保存在`SecurityContextHolder`中，便可在系统各处获取当前用户信息。

### 继承 WebSecurityConfigurerAdapter

重写`configure()`方法，创建基于内存的用户信息:
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("auth").password("{noop}123123").roles("GUEST");
    }
}
```

### 自定义 UserDetails 实例
返回一个`InMemoryUserDetailsManager` 实例，里面包含具体的用户账号信息。
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username("config")
                .password("123123").passwordEncoder(passwordEncoder()::encode)
                .roles("GUEST")
                .build();

        // InMemoryUserDetailsManager() 可以传递多个 user 对象。
        return new InMemoryUserDetailsManager(user);
    }
}
```

### 在其他地方获取当前用户信息
```java
@RestController
public class IndexController {
    @GetMapping("/")
    public String welcome() {

        // ...
        /* 获取当前登录用户信息 */
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return "";
    }
}
```