package com.garnet.security.mapper;

import com.garnet.security.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserInfo selectByUsername(String username);

}
