package com.one_ccs.easy_spring_boot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.one_ccs.easy_spring_boot.entity.po.UserRole;
import com.one_ccs.easy_spring_boot.mapper.UserRoleMapper;
import com.one_ccs.easy_spring_boot.service.IUserRoleService;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Autowired
    UserRoleMapper userRoleMapper;

    public int deleteAllByUid(Integer uid) {
        return userRoleMapper.deleteAllByUid(uid);
    }
}
