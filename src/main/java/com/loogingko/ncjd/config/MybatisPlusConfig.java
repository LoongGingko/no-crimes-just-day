package com.loogingko.ncjd.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    /**
     * 配置MyBatis-Plus分页插件拦截器
     * 用于实现物理分页功能，支持MySQL数据库
     *
     * @return MybatisPlusInterceptor MyBatis-Plus核心拦截器实例，包含分页插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MARIADB); // 配置分页插件，指定数据库类型为 MariaDB
        paginationInnerInterceptor.setOverflow(false); // 增强安全性：页码溢出时返回空数据
        paginationInnerInterceptor.setMaxLimit(500L); // 增强安全性：单页最大记录数限制
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}

