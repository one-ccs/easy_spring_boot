package com.one_ccs.easy_spring_boot.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.baomidou.mybatisplus.extension.service.IService;
import com.one_ccs.easy_spring_boot.entity.po.User;

public interface IUserService extends IService<User>, UserDetailsService {

}
