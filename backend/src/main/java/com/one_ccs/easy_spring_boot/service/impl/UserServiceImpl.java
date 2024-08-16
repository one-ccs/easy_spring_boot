package com.one_ccs.easy_spring_boot.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.one_ccs.easy_spring_boot.entity.bo.UserBO;
import com.one_ccs.easy_spring_boot.entity.dto.MyUserDetails;
import com.one_ccs.easy_spring_boot.entity.po.Role;
import com.one_ccs.easy_spring_boot.entity.po.User;
import com.one_ccs.easy_spring_boot.entity.po.UserRole;
import com.one_ccs.easy_spring_boot.entity.vo.Result;
import com.one_ccs.easy_spring_boot.entity.vo.request.DeleteVO;
import com.one_ccs.easy_spring_boot.entity.vo.request.QueryUserVO;
import com.one_ccs.easy_spring_boot.entity.vo.request.UserVO;
import com.one_ccs.easy_spring_boot.mapper.UserMapper;
import com.one_ccs.easy_spring_boot.service.IUserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    UserRoleServiceImpl userRoleServiceImpl;

    @Autowired
    RoleServiceImpl roleServiceImpl;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.getUserByNameOrEmail(username, username);
        if (user == null) throw new UsernameNotFoundException("不存在该用户");
        List<Role> roles = roleServiceImpl.getRolesByUid(user.getId());

        return new MyUserDetails(username, user, roles);
    }

    /**
     * 通过用户名或邮箱地址查找用户
     * @param name 用户名
     * @param email 邮箱地址
     * @return
     */
    public User getUserByNameOrEmail(String username, String email) {
        return this.lambdaQuery()
            .eq(User::getUsername, username).or()
            .eq(User::getEmail, email)
            .one();
    }

    /**
     * 返回包含角色 (Role) 的 User
     * @param user
     * @return UserBO
     */
    public UserBO getUserBO(User user) {
        return user.asViewObject(UserBO.class, v -> {
            v.setRoles(roleServiceImpl.getRolesByUid(user.getId()));
        });
    }

    /**
     * 分页查询用户数据
     * @param query
     * @return
     */
    public Result<Object> getUserPage(QueryUserVO query) {
        // 构造查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(query.getStartDatetime() != null, "register_datetime", query.getStartDatetime())
            .le(query.getEndDatetime() != null, "register_datetime", query.getEndDatetime())
            .and(StringUtils.isNotBlank(query.getQuery()), i -> i
                .like("username", query.getQuery())
                .or()
                .like("email", query.getQuery())
            );

        // 分页
        Page<User> page = new Page<>(
            Optional.ofNullable(query.getPageIndex()).orElse(1),
            Optional.ofNullable(query.getPageSize()).orElse(10)
        );

        // 获取分页数据
        if (query.getFilterFlag() == 0) userMapper.selectPage(page, queryWrapper);
        if (query.getFilterFlag() == 1) {
            queryWrapper.eq("user_role.rid", 1);
            userMapper.selectPage(page, queryWrapper);
        }
        if (query.getFilterFlag() == 2) {
            queryWrapper.gt("user_role.rid", 1);
            userMapper.selectPage(page, queryWrapper);
        }

        // 构造返回结构
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("list", page.getRecords().stream()
            .map(this::getUserBO)
            .toList()
        );

        return Result.success("查询成功", map);
    }

    /**
     * 向数据库中添加用户并设置角色
     * @param userVO
     * @return
     */
    public Result<Object> addUser(UserVO userVO) {
        // 表单验证
        if (StringUtils.isBlank(userVO.getUsername()) && StringUtils.isBlank(userVO.getEmail())) {
            return Result.failure("用户名和邮箱地址不能同时为空");
        }
        if (StringUtils.isBlank(userVO.getPassword())) {
            return Result.failure("密码不能为空");
        }
        if (!StringUtils.isBlank(userVO.getUsername()) && this.query().eq("username", userVO.getUsername()).one() != null) {
            return Result.failure("用户名重复");
        }
        if (!StringUtils.isBlank(userVO.getEmail()) && this.query().eq("email", userVO.getEmail()).one() != null) {
            return Result.failure("邮箱地址重复");
        }

        // 构造 User 并清除 id、加密密码
        User newUser = userVO.asViewObject(User.class, user -> {
            user.setId(null);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        });

        // 添加用户
        if (this.save(newUser)) {
            // 设置新用户的 id
            userVO.setId(newUser.getId());
            // 设置角色
            if (roleServiceImpl.addRoles(userVO)) {
                return Result.success("添加成功");
            }
            return Result.failure("添加失败，用户角色添加失败");
        }
        return Result.failure("添加失败");
    }

    public Result<Object> modifyUser(UserVO userVO) {
        // 表单验证
        if (userVO.getId() == null) {
            return Result.failure("用户 id 不能为空");
        }
        if (StringUtils.isBlank(userVO.getUsername()) &&
                StringUtils.isBlank(userVO.getEmail())) {
            return Result.failure("用户名和邮箱地址不能同时为空");
        }
        if (!StringUtils.isBlank(userVO.getUsername()) &&
            this.query()
                .ne("id", userVO.getId())
                .eq("username", userVO.getUsername())
                .one() != null) {
            return Result.failure("修改失败，用户名重复");
        }
        if (!StringUtils.isBlank(userVO.getEmail()) &&
            this.query()
                .ne("id", userVO.getId())
                .eq("email", userVO.getEmail())
                .one() != null) {
            return Result.failure("修改失败，邮箱地址重复");
        }

        User newUser = userVO.asViewObject(User.class, user -> {
            if (StringUtils.isNotBlank(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(null);
            }
        });

        // 更新用户信息
        if (this.updateById(newUser)) {
            // 删除旧角色信息
            userRoleServiceImpl.deleteAllByUid(userVO.getId());
            // 添加新角色信息
            if (userRoleServiceImpl.saveBatch(userVO.getUserRoleList())) {
                return Result.success("修改成功");
            }
            return Result.failure("修改失败，修改用户角色失败");
        }

        return Result.failure("修改失败");
    }

    public Result<Object> deleteUser(DeleteVO deleteVO) {
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

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return
     */
    public Result<Object> register(String username, String password, String email) {
        if (this.save(new User(username, password, email))) {
            User newUser = this.getUserByNameOrEmail(username, email);
            // 添加角色为 用户
            userRoleServiceImpl.save(new UserRole(null, newUser.getId(), 1));

            return Result.success("注册成功");
        }
        return Result.success("注册失败");
    }

    /**
     * 修改用户头像
     * @param id 用户 id
     * @param filename 头像文件
     * @return
     */
    public Result<Object> modifyAvatar(Integer id, String filename) {
        if (StringUtils.isBlank(filename)) {
            return Result.failure("头像名不能为空");
        }

        if (!this.updateById(new User(id, filename))) {
            return Result.failure("头像修改失败");
        }

        return Result.success("头像修改成功");
    }
}
