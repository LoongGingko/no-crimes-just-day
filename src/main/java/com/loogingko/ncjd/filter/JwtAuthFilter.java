package com.loogingko.ncjd.filter;

import com.loogingko.ncjd.config.SecurityConfig;
import com.loogingko.ncjd.constant.Constants;
import com.loogingko.ncjd.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 业务接口须在请求头携带 {@code token}。未登录或令牌无效时返回 401。
 * Filter过滤器，用于过滤请求，如果请求头中没有token，则返回401状态码。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // 判断是否跳过过滤（true=跳过; false=执行）
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getServletPath();
        // 白名单放行
        for (String path : SecurityConfig.EXCLUDE_PATHS) {
            if (uri.contains(path)) {
                return true;
            }
        }
        return false;
    }

    // 核心逻辑：拦截器校验Token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 0. 打印 HTTP 请求
        logRequest(request);

        // 1. 获取Cookie中的token
        String token = extractTokenFromCookie(request);

        // 2. 如果token为空，则返回401状态码
        if (!StringUtils.hasText(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"令牌为空\"}");
            return;
        }

        // 3. 验证token是否有效
        try {
            jwtService.assertValid(token.trim());
        } catch (JwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"令牌无效\"}");
            return;
        }

        // 4. 写入 SecurityContext，告诉 SpringSecurity 此请求已认证 (由于是线程级别的，每个请求结束后自动清空)
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(token, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. 通过验证，向下传递
        filterChain.doFilter(request, response);
    }
    
    // 从Cookie获取token
    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (var cookie : request.getCookies()) {
            if (Constants.TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // 打印 HTTP 请求便于调试
    private void logRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if ("/auth/verify".equals(uri)) return;

        System.out.printf("""
        
        ╔══════════════════════════════════════════╗
        ║           REQUEST DETECTED               ║
        ╠══════════════════════════════════════════╣
        ║  URI     : %s
        ║  Method  : %s
        ║  IP      : %s
        ║  Time    : %s
        ╚══════════════════════════════════════════╝
        
        """,
                uri,
                request.getMethod(),
                request.getRemoteAddr(),
                new java.util.Date()
        );
    }
}
