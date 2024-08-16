package com.one_ccs.easy_spring_boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.one_ccs.easy_spring_boot.entity.vo.Result;
import com.one_ccs.easy_spring_boot.entity.vo.request.DeleteVO;
import com.one_ccs.easy_spring_boot.entity.vo.request.QueryUserVO;
import com.one_ccs.easy_spring_boot.entity.vo.request.UserVO;
import com.one_ccs.easy_spring_boot.service.impl.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "1-用户", description = "用户接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Operation(summary = "获取用户信息", description = "获取用户信息接口")
    @GetMapping
    public Result<Object> get(Integer id) {
        return Result.success(userServiceImpl.getById(id));
    }

    @Operation(summary = "添加用户", description = "添加用户接口")
    @PutMapping
    public Result<Object> put(@RequestBody UserVO userVO) {
        return userServiceImpl.addUser(userVO);
    }

    @Operation(summary = "修改用户", description = "修改用户接口")
    @PostMapping
    public Result<Object> post(@RequestBody UserVO userVO) {
        return userServiceImpl.modifyUser(userVO);
    }

    @Operation(summary = "删除用户", description = "删除用户接口")
    @DeleteMapping
    public Result<Object> delete(@RequestBody DeleteVO deleteVO) {
        return userServiceImpl.deleteUser(deleteVO);
    }

    @Operation(summary = "分页查询用户列表", description = "分页查询用户列表接口")
    @GetMapping("/pageQuery")
    public Result<Object> pageQuery(QueryUserVO query) {
        return userServiceImpl.getUserPage(query);
    }

    @SecurityRequirements
    @Operation(summary = "注册", description = "用户注册接口")
    @PostMapping("/register")
    public Result<Object> register(String username, String password, @RequestParam(name = "email", defaultValue = "") String email) {
        return userServiceImpl.register(username, password, email);
    }

    @SecurityRequirements
    @Operation(summary = "修改头像", description = "修改头像接口")
    @PostMapping("/modifyAvatar")
    public Result<Object> modifyAvatar(Integer id, String filename) {
        return userServiceImpl.modifyAvatar(id, filename);
    }

}
