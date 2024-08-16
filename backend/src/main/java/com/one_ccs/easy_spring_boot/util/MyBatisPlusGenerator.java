package com.one_ccs.easy_spring_boot.util;

import java.sql.Types;
import java.util.Collections;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

public class MyBatisPlusGenerator {

    private static final String url = "jdbc:mysql://localhost:3306/easy_spring_boot";
    private static final String username = "root";
    private static final String password = "123456";

    public static void main(String[] args) {
        FastAutoGenerator.create(url, username, password)
            .globalConfig(builder -> builder
                .enableSpringdoc()
                .author("one-ccs")
                .outputDir("D://easy_spring_boot")
            )
            .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                if (typeCode == Types.SMALLINT) {
                    // 自定义类型转换
                    return DbColumnType.INTEGER;
                }
                return typeRegistry.getColumnType(metaInfo);
            }))
            .packageConfig(builder -> builder
                // 设置父包名
                .parent("com.one_ccs")
                // 设置父包模块名
                .moduleName("easy_spring_boot")
                // 设置 mapperXml 生成路径
                .pathInfo(Collections.singletonMap(OutputFile.xml, "D://easy_spring_boot"))
            )
            .strategyConfig(builder -> builder
                // 设置需要生成的表名
                .addInclude("users")
                .addInclude("roles")
                // 设置过滤表前缀
                .addTablePrefix("t_", "c_")
            )
            // 使用 Freemarker 引擎模板 (需安装 spring-boot-starter-freemarker 依赖)
            // 默认 Velocity 引擎模板
            .templateEngine(new FreemarkerTemplateEngine())
            .execute();
    }
}
