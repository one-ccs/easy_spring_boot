package com.one_ccs.easy_spring_boot.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.one_ccs.easy_spring_boot.entity.dto.MyUserDetails;
import com.one_ccs.easy_spring_boot.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 每次请求都会调用该过滤器
 */
@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 从请求头获取 token
        String authorization = request.getHeader("Authorization");
        // 解析 JWT
        DecodedJWT jwt = jwtUtil.resolveJwt(authorization);
        if (jwt != null) {
            MyUserDetails myUserDetails = jwtUtil.toUserDetails(jwt);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 强制设置 Authentication 表示认证通过
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            request.setAttribute("id", jwtUtil.toId(jwt));
        }
        chain.doFilter(request, response);
    }
}
