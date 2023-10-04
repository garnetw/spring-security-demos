package com.garnet.security.web.config;

import com.garnet.security.mapper.ResourceMapper;
import com.garnet.security.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class MySecurityMetadataSource implements SecurityMetadataSource {
    /**
     * 当前系统所有接口资源对象，放在这里相当于一个缓存的功能。
     * 你可以在应用启动时将该缓存给初始化，也可以在使用过程中加载数据，这里我就不多展开说明了
     */
    private static final Set<Resource> RESOURCES = new HashSet<>();

    @Autowired
    ResourceMapper resourceMapper;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        /** 获取当前系统所有接口资源对象 */
        if(RESOURCES.size() == 0)
            initResourceList();

        // 该对象是Spring Security帮我们封装好的，可以通过该对象获取request等信息
        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        /** 遍历所有权限资源，以和当前请求进行匹配 */
        for(Resource resource : RESOURCES) {
            AntPathRequestMatcher ant = new AntPathRequestMatcher(resource.getPath());

            if (ant.matches(request)) { // 如果请求路径匹配上了，则代表找到了这个请求需要权限进行访问
                // 将资源id返回，这个SecurityConfig就是ConfigAttribute一个简单实现
                return Collections.singletonList(new SecurityConfig(String.valueOf(resource.getId())));
            }
        }
        // 走到这里就代表该请求无需授权即可访问，返回空
        return null;
    }

    /**
     * 当前系统所有接口资源对象
     */
    public void initResourceList() {
        RESOURCES.addAll(resourceMapper.selectAll());
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
