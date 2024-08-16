package com.one_ccs.easy_spring_boot.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.one_ccs.easy_spring_boot.entity.po.Role;
import com.one_ccs.easy_spring_boot.entity.vo.BasePageQuery;
import com.one_ccs.easy_spring_boot.entity.vo.Result;
import com.one_ccs.easy_spring_boot.entity.vo.request.DeleteVO;
import com.one_ccs.easy_spring_boot.entity.vo.request.UserVO;
import com.one_ccs.easy_spring_boot.mapper.RoleMapper;
import com.one_ccs.easy_spring_boot.service.IRoleService;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    UserRoleServiceImpl userRoleServiceImpl;

    @Autowired
    RoleMapper roleMapper;

    /**
     * 返回用户的角色列表
     * @param uid 用户 id
     * @return
     */
    public List<Role> getRolesByUid(Integer uid) {
        return roleMapper.getRolesByUid(uid);
    }

    /**
     * 给用户添加多个角色
     * @param userVO userVO 包装类
     * @return 是否添加成功
     */
    public boolean addRoles(UserVO userVO) {
        return userRoleServiceImpl.saveOrUpdateBatch(userVO.getUserRoleList());
    }

    /**
     * 模糊查询角色列表
     * @param query 英文名或中文名
     * @return
     */
    public Result<Object> getRoles(BasePageQuery query) {
        // 构造查询条件
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(query.getQuery()), Role::getName, query.getQuery())
            .or()
            .like(StringUtils.isNotBlank(query.getQuery()), Role::getNameZh, query.getQuery());

        // 分页
        Page<Role> page = new Page<>(
            Optional.ofNullable(query.getPageIndex()).orElse(1),
            Optional.ofNullable(query.getPageSize()).orElse(10)
        );

        // 获取分页数据
        this.page(page, queryWrapper);

        // 构造返回结构
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("list", page.getRecords());

        return Result.success("查询成功", map);
    }

    /**
     * 添加角色
     * @param role
     * @return
     */
    public Result<Object> addRole(Role role) {
        // 表单验证
        if (!StringUtils.isBlank(role.getName()) && this.query().eq("name", role.getName()).one() != null) {
            return Result.failure("角色英文名重复");
        }
        if (!StringUtils.isBlank(role.getNameZh()) && this.query().eq("name_zh", role.getNameZh()).one() != null) {
            return Result.failure("角色中文名重复");
        }
        if (StringUtils.isBlank(role.getName())) {
            return Result.failure("角色英文名不能为空");
        }
        if (StringUtils.isBlank(role.getNameZh())) {
            return Result.failure("角色中文名不能为空");
        }

        if (this.save(role)) {
            return Result.success("添加成功");
        }
        return Result.failure("添加失败");
    }

    /**
     * 修改角色
     * @param role
     * @return
     */
    public Result<Object> modifyRole(Role role) {
        // 表单验证
        if (role.getId() == null) {
            return Result.failure("角色 id 不能为空");
        }
        if (!StringUtils.isBlank(role.getName()) && this.query().eq("name", role.getName()).one() != null) {
            return Result.failure("角色英文名重复");
        }
        if (!StringUtils.isBlank(role.getNameZh()) && this.query().eq("name_zh", role.getNameZh()).one() != null) {
            return Result.failure("角色中文名重复");
        }
        if (StringUtils.isBlank(role.getName())) {
            return Result.failure("角色英文名不能为空");
        }
        if (StringUtils.isBlank(role.getNameZh())) {
            return Result.failure("角色中文名不能为空");
        }

        if (this.updateById(role)) {
            return Result.success("修改成功");
        }
        return Result.failure("修改失败");
    }

    /**
     * 删除角色
     * @param deleteVO
     * @return
     */
    public Result<Object> deleteRole(DeleteVO deleteVO) {
        if (deleteVO.getIds() != null) {
            // 批量删除
            if (this.removeBatchByIds(deleteVO.getIds())) {
                return Result.success("批量删除成功");
            }
            return Result.failure("批量删除失败");
        }
        else if (deleteVO.getId() != null) {
            // 单个删除
            if (this.removeById(deleteVO.getId())) {
                return Result.success("删除成功");
            }
            return Result.failure("删除失败");
        }
        return Result.failure("删除失败，参数 id 和 ids 不能同时为空");
    }
}
