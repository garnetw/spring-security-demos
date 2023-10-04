# 集成JWT + 菜单权限校验

### 集成JWT

* JWTokenService：实现一个JWT工具类，用于生成token、解析token。
* 集成JWT，先禁用 Security 框架的 session
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // ...
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //...
        return http.build();
    }
}
```

* 每当一个请求来时我们都会校验JWT进行认证，上下文对象中有了Authentication后续过滤器就会知道该请求已经认证过了。

```java
@Component
public class JwtLoginFilter extends OncePerRequestFilter {
    // ...
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Claims claims = jwTokenService.parseToken(request.getHeader(header));
        /* token 合法 */
        if(claims != null) {
            String username = claims.getSubject();
            UserInfo userInfo = userMapper.selectByUsername(username);
            if(userInfo == null) {
                throw new UsernameNotFoundException("用户不存在");
            }

            /* 获取用户权限id，存入SecurityContext中 */
            List<SimpleGrantedAuthority> authorities = resourceMapper.getIdsByUserId(userInfo.getId())
                    .stream()
                    .map(String::valueOf)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            Authentication auth = new UsernamePasswordAuthenticationToken(userInfo, userInfo.getPassword(), authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
```

* 这个自定义的认证过滤器需要插入到默认的认证过滤器之前(`UsernamePasswordAuthenticationFilter`负责登录认证)，这样我们的过滤器才能生效，所以需要进行如下配置：  
`.addFilterBefore(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)`


### 菜单权限校验

1. 首先调用的是 SecurityMetadataSource，来判断当前访问的url是否需要权限，需要则返回该资源的Id
2. 然后通过 SecurityConte 中保存的 Authentication 获取当前登录用户所有权限数据： GrantedAuthority，这个我们前面提过，认证对象里存放这权限数据
3. 再调用 AccessDecisionManager 来判断当前用户是否拥有该资源的访问权限
```java
@Component
public class MyAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 如果授权规则为空则代表此URL无需授权就能访问
        if(Collections.isEmpty(configAttributes)) {
            return;
        }

        for(ConfigAttribute ca : configAttributes) {
            for(GrantedAuthority authority : authentication.getAuthorities()) {
                if(Objects.equals(authority.getAuthority(), ca.getAttribute())) {
                    return;
                }
            }
        }

        // 走到这里就代表没有权限，必须要抛出异常，否则错误处理器捕捉不到
        throw new AccessDeniedException("您没有权限");
    }
}
```
4. 如果有就放行接口，没有则抛出异常，该异常会被 AccessDeniedHandler 处理


