package com.one_ccs.easy_spring_boot.entity.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.one_ccs.easy_spring_boot.entity.po.Role;
import com.one_ccs.easy_spring_boot.entity.po.User;

import lombok.Data;

@Data
public class MyUserDetails implements UserDetails {

	private String username;

	private String password;

	private boolean enabled;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	private List<Role> roles = new ArrayList<>();

    private User user;

	public MyUserDetails(String username, String password, List<Role> roles) {
		this(username, password, true, true, true, true, roles, null);
	}

	public MyUserDetails(String username, User user, List<Role> roles) {
		this(username, user.getPassword(), user.getStatus() == null || user.getStatus() == 1, true, true, true, roles, user);
	}

	public MyUserDetails(
        String username, String password, boolean enabled,
        boolean accountNonExpired, boolean credentialsNonExpired,
        boolean accountNonLocked, List<Role> roles, User user
    ) {
		Assert.isTrue(username != null && !"".equals(username) && password != null,
				"MyUserDetails{用户名和密码不能为空}");
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.roles = roles;
        this.user = user;
	}

    /**
     * 返回角色英文名列表
     * @return eg. [admin, user]
     */
    public List<String> getRoleNames() {
        return this.roles.stream()
            .map(Role::getName)
            .toList();
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : this.roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName()).append(" [");
		sb.append("Username=").append(this.username).append(", ");
		sb.append("Password=[PROTECTED], ");
		sb.append("Enabled=").append(this.enabled).append(", ");
		sb.append("AccountNonExpired=").append(this.accountNonExpired).append(", ");
		sb.append("CredentialsNonExpired=").append(this.credentialsNonExpired).append(", ");
		sb.append("AccountNonLocked=").append(this.accountNonLocked).append(", ");
		sb.append("Roles=").append(this.roles).append(", ");
		sb.append("User=").append(this.user).append("]");
		return sb.toString();
	}
}
