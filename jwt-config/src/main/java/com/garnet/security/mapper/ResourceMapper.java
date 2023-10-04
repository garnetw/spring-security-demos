package com.garnet.security.mapper;

import com.garnet.security.model.Resource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceMapper {

    List<Resource> selectAll();

    List<Integer> getIdsByUserId(int userId);

}
