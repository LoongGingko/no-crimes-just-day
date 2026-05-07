package com.loogingko.ncjd.controller;

import com.loogingko.ncjd.model.bo.R;
import com.loogingko.ncjd.service.auth.JwtService;
import com.loogingko.ncjd.service.biz.ManualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 手册控制器
 * @author LiuRunYu 2026-05-05
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/manual")
public class ManualController {

    private final ManualService manualService;
    private final JwtService jwtService;
    
    /**
     * 手册首页数据
     */
    @PostMapping("/pre/manual")
    public R fetchAll() {
        return manualService.getManualList(jwtService.getCurrentUserId());
    }
}
