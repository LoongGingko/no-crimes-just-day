package com.loogingko.ncjd.service;

import com.loogingko.ncjd.model.bo.R;
import com.loogingko.ncjd.model.dto.LoginRequest;
import com.loogingko.ncjd.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 图形验证码服务
 * @author LiuRunYu 2026-05-04
 */
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final StringRedisTemplate redisTemplate;
    
    // 校验图形验证码
    public R vaildateCaptcha(LoginRequest req) {
        String captchaId = CookieUtils.get(CookieUtils.CAPTCHA_ID);
        String code = req.getCaptcha();

        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(code)) {
            return R.fail("请输入验证码").code(400);
        }
        // 从 Redis 获取存储的验证码
        String redisKey = "captcha:" + captchaId;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) return R.fail("验证码已过期，请刷新").code(400);
        if (!storedCode.equalsIgnoreCase(code)) return R.fail("验证码错误").code(400);

        // 验证成功后从Redis删除
        redisTemplate.delete(redisKey);
        return R.succ(null);
    }
    
    // 生成图形验证码
    public String geneCode(int length) {
        // 排除 0、O、1、I 等易混淆字符
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // 生成图形验证码图片
    public BufferedImage geneCodeImg(String code) {
        int width = 100;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置背景色
        g2d.setColor(java.awt.Color.black);
        g2d.fillRect(0, 0, width, height);

        // 设置字体
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));

        // 绘制验证码字符
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g2d.setColor(new java.awt.Color(
                    (int)(Math.random() * 250),
                    (int)(Math.random() * 250),
                    (int)(Math.random() * 250)
            ));
            // 随机旋转角度
            double angle = (Math.random() - 0.5) * 0.5;
            g2d.rotate(angle, 15 + i * 20, 25);
            g2d.drawString(String.valueOf(code.charAt(i)), 10 + i * 20, 28);
            g2d.rotate(-angle, 15 + i * 20, 25); // 恢复旋转
        }

        // 添加干扰线
        for (int i = 0; i < 5; i++) {
            g2d.setColor(new java.awt.Color(
                    (int)(Math.random() * 200),
                    (int)(Math.random() * 200),
                    (int)(Math.random() * 200)
            ));
            int x1 = (int)(Math.random() * width);
            int y1 = (int)(Math.random() * height);
            int x2 = (int)(Math.random() * width);
            int y2 = (int)(Math.random() * height);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // 释放资源
        g2d.dispose();
        return image;
    }
}
