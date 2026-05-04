package com.loogingko.ncjd.util;

import com.loogingko.ncjd.config.NCJDProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * Cookie工具类
 * @author LiuRunYu 2026-04-29
 */
@Component
public class CookieUtils {

    public static final String CAPTCHA_ID = "captcha_id"; // Cookie：图形验证码ID
    
    private static NCJDProperties ncjdProperties;

    public CookieUtils(NCJDProperties properties) {
        CookieUtils.ncjdProperties = properties;
    }
    
    // 添加 Cookie（默认 path="/", httpOnly=true）
    public static void add(String name, String value, int maxAge) {
        add(name, value, maxAge, "/", true);
    }
    
    // 添加cookie
    public static void add(String name, String value, int maxAge, String path, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(ncjdProperties.getCookieSecure());
        getResponse().addCookie(cookie);
    }

    /** 获取 Cookie 值 */
    public static String get(String name) {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /** 删除 Cookie */
    public static void remove(String name) {
        add(name, "", 0);
    }
    
    // ---- private ----
    private static HttpServletRequest getRequest() {
        return getAttributes().getRequest();
    }

    private static HttpServletResponse getResponse() {
        return getAttributes().getResponse();
    }

    private static ServletRequestAttributes getAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    }
}
