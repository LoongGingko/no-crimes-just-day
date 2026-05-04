package com.loogingko.ncjd.controller;

import cn.hutool.core.bean.BeanUtil;
import com.loogingko.ncjd.config.JwtProperties;
import com.loogingko.ncjd.constant.Constants;
import com.loogingko.ncjd.model.bo.R;
import com.loogingko.ncjd.model.dto.LoginRequest;
import com.loogingko.ncjd.model.dto.LogoutRequest;
import com.loogingko.ncjd.model.dto.RegisterRequest;
import com.loogingko.ncjd.model.entity.User;
import com.loogingko.ncjd.model.vo.LoginResponse;
import com.loogingko.ncjd.service.CaptchaService;
import com.loogingko.ncjd.service.JwtService;
import com.loogingko.ncjd.service.LimitService;
import com.loogingko.ncjd.service.UserService;
import com.loogingko.ncjd.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 登录主入口
 * @author LiuRunYu 2026-04-10
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final LimitService limitService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final CaptchaService captchaService;
    private final StringRedisTemplate redisTemplate;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录入口
     */
    @PostMapping("/login")
    public R login(@Valid @RequestBody LoginRequest req) {
        String username = req.getUsername();
        log.info("登录请求: username={}", username);

        // 安全性增强：限制登录频率
        if (limitService.isLoginBlocked(username)) {
            long remainingTime = limitService.getRemainingTime("rate:login:", username);
            log.warn("登录频率限制: username={}, 剩余锁定时间={}s", username, remainingTime);
            return R.fail("登录尝试次数过多，请" + remainingTime + "秒后再试").code(429);
        }
        // 安全性增强：校验图形验证码
        R r = captchaService.vaildateCaptcha(req);
        if (r.hasFailed()) return r;
        
        try {
            // 1. 创建未认证的 Token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, req.getPassword());

            // 2. 调用 AuthenticationManager 进行认证 (会触发loadUserByUsername)
            Authentication authentication = authManager.authenticate(authToken);

            // 3. 生成令牌
            User userDb = userService.lambdaQuery().eq(User::getUsername, username).one();
            String token = jwtService.createToken(userDb);
            
            // 4. 创建JWT Token的HttpOnly Cookie
            CookieUtils.add(Constants.TOKEN_COOKIE_NAME, token, (int) jwtProperties.getExpiration());
            
            // 5. 返回成功消息
            log.info("登录成功: username={}", username);
            limitService.clearLoginLimit(username);
            return R.succ(BeanUtil.copyProperties(userDb, LoginResponse.class));
            
        } catch (BadCredentialsException e) {
            log.warn("登录失败: username={}, 原因=用户名或密码错误", username);
            limitService.recordLoginFailure(username);
            return R.fail("用户名或密码错误").code(401);
            
        } catch (Exception e) {
            log.error("登录异常: username={}", username, e);
            return R.fail("登录失败: " + e.getMessage()).code(500);
        }
    }

    /**
     * 用户注册入口
     */
    @PostMapping("/register")
    public R register(@Valid @RequestBody RegisterRequest req) {
        String username = req.getUsername();
        log.info("注册请求: username={}", username);

        // 安全性增强：限制注册频率
        if (limitService.isRegisterBlocked(username)) {
            long remainingTime = limitService.getRemainingTime("rate:register:", username);
            log.warn("注册频率限制: username={}, 剩余锁定时间={}s", username, remainingTime);
            return R.fail("注册尝试次数过多，请" + remainingTime + "秒后再试").code(429);
        }

        // 1. 检查用户名是否已存在
        long count = userService.lambdaQuery().eq(User::getUsername, username).count();
        if (count > 0) return R.fail("用户名已存在").code(409);

        // 2. 数据库创建用户
        User user = new User(username, passwordEncoder.encode(req.getPassword()));  // 对密码进行BCrypt加密

        // 3. 保存到数据库
        boolean saved = userService.save(user);
        if (!saved) {
            log.error("注册失败: username={}", username);
            return R.fail("注册失败，请稍后重试").code(500);
        }
        log.info("注册成功: username={}", username);
        return R.succ(BeanUtil.copyProperties(user, LoginResponse.class));
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequest req) {
        String username = req.getUsername();
        CookieUtils.remove(Constants.TOKEN_COOKIE_NAME);
        log.info("退出登录成功: username={}", username);
    }

    /**
     * 检测后台启动状态
     */
    @PostMapping("/ping")
    public void ping() {
    }
    
    /**
     * 生成图形验证码，存入Redis
     */
    @RequestMapping("/captcha")
    public void captcha(HttpServletResponse resp) {
        // 1. 生成4位验证码和ID
        String captchaId = UUID.randomUUID().toString();
        String code = captchaService.geneCode(4);
        
        // 2. 生成验证码2D图片
        BufferedImage image = captchaService.geneCodeImg(code);

        // 3. 将验证码存入Redis，Key 格式为 "captcha:{captchaId}"，有效期设置为 1 分钟
        redisTemplate.opsForValue().set("captcha:" + captchaId, code.toLowerCase(), 60, TimeUnit.SECONDS);
        
        // 4. 将验证码ID存入HttpOnly Cookie
        CookieUtils.add(CookieUtils.CAPTCHA_ID, captchaId, 60); // 有效期与Redis中验证码保持一致，60秒

        // 5. 输出图片
        resp.setContentType(MediaType.IMAGE_JPEG_VALUE);
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Content-Disposition", "inline");
        try (OutputStream os = resp.getOutputStream()) {
            ImageIO.setUseCache(false);
            ImageIO.write(image, "JPEG", os);
            os.flush();
        } catch (IOException e) {
            log.error("验证码生成失败: captchaId={}", captchaId, e);
            throw new RuntimeException("验证码生成失败，请重试");
        }
    }
    
    
    
}
