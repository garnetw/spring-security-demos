package com.garnet.security.web.filter;

import com.garnet.security.mapper.ResourceMapper;
import com.garnet.security.mapper.UserMapper;
import com.garnet.security.model.UserInfo;
import com.garnet.security.web.service.JWTokenService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 自定义Jwt认证过滤器：
 * 每当一个请求进来时都会通过JWTokenService解析请求头中带的token，
 * 有效则将用户信息存入SecurityContextHolder中。
 */
@Component
public class JwtLoginFilter extends OncePerRequestFilter {

    @Autowired
    JWTokenService jwTokenService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ResourceMapper resourceMapper;

    @Value("${jwt.header}")
    private String header;

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
