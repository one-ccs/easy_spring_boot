package com.one_ccs.easy_spring_boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.one_ccs.easy_spring_boot.entity.po.Role;
import com.one_ccs.easy_spring_boot.entity.vo.BasePageQuery;
import com.one_ccs.easy_spring_boot.entity.vo.Result;
import com.one_ccs.easy_spring_boot.entity.vo.request.DeleteVO;
import com.one_ccs.easy_spring_boot.service.impl.RoleServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "2-角色", description = "角色接口")
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    RoleServiceImpl roleServiceImpl;

    @Operation(summary = "获取角色信息", description = "获取角色信息接口")
    @GetMapping
    public Result<Object> get(Integer id) {
        return Result.success(roleServiceImpl.getById(id));
    }

    @Operation(summary = "添加角色", description = "添加角色接口")
    @PutMapping
    public Result<Object> put(@RequestBody Role role) {
        return roleServiceImpl.addRole(role);
    }

    @Operation(summary = "修改角色", description = "修改角色接口")
    @PostMapping
    public Result<Object> post(@RequestBody Role role) {
        return roleServiceImpl.modifyRole(role);
    }

    @Operation(summary = "删除角色", description = "删除角色接口")
    @DeleteMapping
    public Result<Object> delete(@RequestBody DeleteVO deleteVO) {
        return roleServiceImpl.deleteRole(deleteVO);
    }

    @Operation(summary = "分页查询角色列表", description = "分页查询角色列表接口")
    @GetMapping("/pageQuery")
    public Result<Object> pageQuery(BasePageQuery query) {
        return roleServiceImpl.getRoles(query);
    }

}
