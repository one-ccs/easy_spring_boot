package com.one_ccs.easy_spring_boot.entity.po;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "Role", description = "角色表")
@TableName("role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色 id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "角色")
    @NotBlank
    private String name;

    @Schema(description = "中文名称")
    @NotBlank
    private String nameZh;
}
