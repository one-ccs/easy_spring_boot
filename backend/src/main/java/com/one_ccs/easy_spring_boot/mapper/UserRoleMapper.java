package com.one_ccs.easy_spring_boot.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.one_ccs.easy_spring_boot.entity.po.UserRole;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    int deleteAllByUid(Integer uid);

}
