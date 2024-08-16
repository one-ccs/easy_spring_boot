package com.one_ccs.easy_spring_boot.entity.bo;

import java.time.LocalDateTime;
import java.util.List;

import com.one_ccs.easy_spring_boot.entity.po.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserBO {

    private Integer id;

    private String username;

    private String password;

    private String email;

    private Byte status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerDatetime;

    private List<Role> roles;

}
