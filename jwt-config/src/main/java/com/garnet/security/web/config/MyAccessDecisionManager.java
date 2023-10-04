package com.garnet.security.web.config;

import io.jsonwebtoken.lang.Collections;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

/**
 * 自定义组件：
 * 判断用户是否有当前请求的访问权限
 */
@Component
public class MyAccessDecisionManager implements AccessDecisionManager {
    /**
     * 授权操作，如果没有权限则抛出异常
     *
     * @param authentication    当前登录用户
     * @param object            FilterInvocation对象，以获取request信息
     * @param configAttributes  当前请求url的鉴权规则
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
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

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
