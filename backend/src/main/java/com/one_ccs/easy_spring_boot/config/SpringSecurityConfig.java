package com.one_ccs.easy_spring_boot.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.one_ccs.easy_spring_boot.entity.dto.MyUserDetails;
import com.one_ccs.easy_spring_boot.entity.vo.Result;
import com.one_ccs.easy_spring_boot.entity.vo.response.AuthorizeVO;
import com.one_ccs.easy_spring_boot.filter.JwtAuthorizeFilter;
import com.one_ccs.easy_spring_boot.service.impl.UserServiceImpl;
import com.one_ccs.easy_spring_boot.util.JwtUtil;
import com.one_ccs.easy_spring_boot.util.annotation.AnonymousAuth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Autowired
    private DataSource dataSource;

    @Bean
    PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        // 自动建表，仅在第一次启动时设置为 true
        jdbcTokenRepositoryImpl.setCreateTableOnStartup(false);
        jdbcTokenRepositoryImpl.setDataSource(dataSource);
        return jdbcTokenRepositoryImpl;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(conf -> {
            // 基本配置
            conf
                // 放行 error 接口
                .requestMatchers("/error").permitAll()
                // 放行 SpringDoc
                .requestMatchers("/api-json/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                // 放行 SpringDoc 的 actuator
                .requestMatchers("/actuator/**").permitAll()
                // 放行 登录、登出、注册接口
                .requestMatchers("/api/user/login").permitAll()
                .requestMatchers("/api/user/logout").permitAll()
                .requestMatchers("/api/user/register").permitAll()
                // 放行查询接口
                .requestMatchers(HttpMethod.GET).permitAll()
                // 放行失物招领相关接口
                .requestMatchers("/api/lost/**").permitAll()
                // 放行文件接口
                .requestMatchers("/api/file/**").permitAll();

            // 放行 AnonymousAuth 注解的接口
            requestMappingHandlerMapping
                .getHandlerMethods()
                .forEach((info, method) -> {
                    PathPatternsRequestCondition pathPatternsRequestCondition = info.getPathPatternsCondition();

                    if (method.getMethodAnnotation(AnonymousAuth.class) != null &&
                        pathPatternsRequestCondition != null
                    ) {
                        pathPatternsRequestCondition.getPatterns().forEach(pattern -> {
                            try {
                                conf.requestMatchers(pattern.getPatternString()).permitAll();
                            }
                            catch (Exception e) {
                                System.err.println("放行匿名接口 " + pattern.getPatternString() + " 失败");
                                System.err.println(e);
                            }
                        });
                    }
            });

            // 其它所有接口均需 管理员权限
            conf.anyRequest().hasAnyRole("admin", "userAdmin", "systemAdmin", "superAdmin");
        })
        .formLogin(conf -> conf
            // 登录接口默认自动放行
            .loginProcessingUrl("/api/user/login")
            .failureHandler(this::onAuthenticationFailure)
            .successHandler(this::onAuthenticationSuccess)
        )
        .rememberMe(conf -> conf
            .useSecureCookie(true)
            // HTTP 请求参数中的名字
            .rememberMeParameter("remember")
            // 保存在 Cookie 中的名字
            .rememberMeCookieName("remember")
            // 有效时长 秒
            .tokenValiditySeconds(3600 * 24 * 7)
            // 持久层对象
            .tokenRepository(persistentTokenRepository())
            .userDetailsService(userServiceImpl)
        )
        .logout(conf -> conf
            // 登出接口默认自动放行
            .logoutUrl("/api/user/logout")
            .logoutSuccessHandler(this::onLogoutSuccess)
            .invalidateHttpSession(true)
            .clearAuthentication(true)
        )
        .exceptionHandling(conf -> conf
            .authenticationEntryPoint(this::onUnauthorized)
            .accessDeniedHandler(this::onAccessDeny)
        )
        .cors(conf -> conf
            .disable()
        )
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(conf -> conf
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
    }

    /**
     * 无权访问
     */
    public void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(Result.forbidden().toJsonString());
    }

    /**
     * 未登录
     */
    public void onUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(Result.unauthorized().toJsonString());
    }

    /**
     * 登录成功
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        String token = jwtUtil.createJWT(myUserDetails);

        AuthorizeVO authorizeVO = myUserDetails.getUser().asViewObject(AuthorizeVO.class, v -> {
            v.setToken(token);
            v.setRoles(myUserDetails.getRoleNames());
        });

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(Result.success(authorizeVO).toJsonString());
    }

    /**
     * 登录失败
     */
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(Result.failure(exception.getMessage()).toJsonString());
    }

    /**
     * 登出成功
     */
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (jwtUtil.invalidateJwt(authorization)) {
            writer.write(Result.success("登出成功").toJsonString());
        } else {
            writer.write(Result.failure("登出失败").toJsonString());
        }
    }
}
