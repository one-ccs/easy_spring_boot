package com.one_ccs.easy_spring_boot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.one_ccs.easy_spring_boot.entity.po.Role;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> getRolesByUid(Integer uid);
}
