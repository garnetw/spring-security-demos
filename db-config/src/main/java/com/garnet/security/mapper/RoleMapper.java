package com.garnet.security.mapper;

import com.garnet.security.model.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<Role> selectByUserId(int userId);
}
