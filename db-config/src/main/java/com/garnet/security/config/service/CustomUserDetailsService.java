package com.garnet.security.config.service;

import com.garnet.security.mapper.RoleMapper;
import com.garnet.security.mapper.UserMapper;
import com.garnet.security.model.Resource;
import com.garnet.security.model.Role;
import com.garnet.security.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义类：用户信息查询
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RoleMapper roleMapper;
    /**
     * 用户登录校验，通过输入的用户名，去数据库/缓存查询是否有合法的用户信息。
     * @param username 用户名
     * @return UserDetails (注意密码必须要是加密过的密码)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo user = userMapper.selectByUsername(username);
        List<Role> roleList = roleMapper.selectByUserId(user.getId());
        user.setRoleList(roleList);
        return user;
    }
}
