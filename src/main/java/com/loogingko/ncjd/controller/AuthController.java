package com.loogingko.ncjd.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限控制器
 * @author LiuRunYu 2026-05-03
 */

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @PostMapping("/verify")
    public void verify() {
        // 空方法，交给Filter校验Token
    }
}
