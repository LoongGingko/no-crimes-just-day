package com.loogingko.ncjd.config;

import com.loogingko.ncjd.filter.JwtAuthFilter;
import com.loogingko.ncjd.service.NCJDUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SpringSecurity 安全配置
 * @author LiuRunYu 2026-04-10
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final NCJDUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    // 放行接口白名单
    public static final String[] EXCLUDE_PATHS = {
        "/ping",            // 检测后台心跳
        "/captcha",         // 图形验证码
        "/login",           // 登录
        "/register",        // 注册
        "/logout",          // 退出登录
//        "/error"            // 错误
    };
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（前后端分离必须）
                .csrf(csrf -> csrf.disable())

                // 禁用表单登录和 HTTP Basic 认证
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 配置自定义登出行为（JWT 仅删除Cookie即可，默认登出会重定向到/login必须禁用）
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .disable()
                )
                
                // 配置 Session 为无状态（JWT 为无状态鉴权！不需要服务器端存储Session！）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EXCLUDE_PATHS).permitAll()  // 放行接口
                        .anyRequest().authenticated()  // 其他任何接口都需要登录
                )
                // 注册 JWT 过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 配置 AuthenticationManager
     * 这是 Spring Security 认证的核心管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
    
    // 对密码进行BCrypt加密，适合生产环境
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 会自动加盐，每次 encode() 生成密文都不一样
    }
}
