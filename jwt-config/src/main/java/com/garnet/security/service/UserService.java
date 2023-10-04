package com.garnet.security.service;

import com.garnet.security.mapper.ResourceMapper;
import com.garnet.security.mapper.UserMapper;
import com.garnet.security.model.UserInfo;
import com.garnet.security.web.service.JWTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ResourceMapper resourceMapper;

    @Autowired
    JWTokenService jwTokenService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public UserInfo login(UserInfo userInfo) {
        UserInfo userDb = userMapper.selectByUsername(userInfo.getUsername());
        // 验证密码是否正确
        if(userDb == null || !passwordEncoder.matches(userInfo.getPassword(), userDb.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误!");
        }

        userDb.setToken(jwTokenService.generateToken(userDb.getUsername()));
        userDb.setResourceList(resourceMapper.getIdsByUserId(userDb.getId()));

        return userDb;
    }

}
