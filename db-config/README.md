# 基于数据库的用户登录+权限校验

### MyBatis

* 定义数据实体`model`，数据库调用接口`mapper`，以及具体的SQL查询语句`resources/mapper/*Mapper.xml`
* 在 application.yml 中添加好 mapper.xml 和 model 的位置


### 自定义 UserDetailsService 接口

* 用户进行登录操作，传递账号密码过来，登录接口获取`DaoAuthenticationProvider`实例
* 实现 UserDetailsService 接口， 重写`loadUserByUsername()`方法
  * 通过传入的用户名，查询数据库或缓存以获取用户信息(UserDetails)
  * ```java
    import org.springframework.stereotype.Service;     
    
    @Service
    public class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        UserMapper userMapper;

        @Autowired
        RoleMapper roleMapper;
        
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            UserInfo user = userMapper.selectByUsername(username);
            List<Role> roleList = roleMapper.selectByUserId(user.getId());
            user.setRoleList(roleList);
            return user;
        }
    }
    ```
* `DaoAuthenticationProvider`实例除了接受自定义的 UserDetailsService 外，还接受自定义密码加密方法
  * 将传递过来的密码和数据库中的密码进行对比校验
  * ```java
    @RestController
    public class SysController {

        @Autowired
        DaoAuthenticationProvider authenticationProvider;

        @PostMapping("/login")
        public String login(@RequestBody UserInfo user) {

            Authentication token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication = authenticationProvider.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "登陆成功";
        }
    }
    ```
* 校验通过则将认证信息存入到上下文中：将Authentication存入到SecurityContext


### 自定义 GrantedAuthority

通过重写 `UserDetails.getAuthorities()` 方法实现，一般是返回角色code或者资源id/path，本项目采用简单的设定角色权限实现：  

```java
public class UserInfo implements UserDetails, Serializable {
    
    // ...
    private List<Role> roleList;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> auth = new ArrayList<>();

        if(roleList != null && !roleList.isEmpty()) {
            for(Role role : roleList) {
                auth.add(new SimpleGrantedAuthority(String.valueOf(role.getCode())));
            }
        }

        return auth;
    }
}
```
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers("/product/**").hasAuthority("USER")
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        return http.build();
    }
}

```

### 自定义两个 exceptionHandler

* `Http401UnauthorizedEntryPoint`: 登录验证失败处理器
* `Http403AccessDeniedHandler`: 权限认证失败处理器