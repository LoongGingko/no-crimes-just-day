package com.loogingko.ncjd.controller;

import com.loogingko.ncjd.model.bo.R;
import com.loogingko.ncjd.model.dto.MemoListReq;
import com.loogingko.ncjd.model.dto.MemoSaveReq;
import com.loogingko.ncjd.service.biz.MemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 笔记条目控制器
 * @author LiuRunYu 2026-05-05
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/memo")
public class MemoController {

    private final MemoService memoService;
    
    /**
     * 预加载
     * @author LiuRunYu 2026-06-25
     */
    @PostMapping("/pre/memo")
    public R pre() {
        return memoService.pre();
    }

    /**
     * 抓数据
     * @author LiuRunYu 2026-06-25
     */
    @PostMapping("/list")
    public R list(@RequestBody MemoListReq req) {
        return memoService.listByUser(req);
    }

    /**
     * 新增/更新笔记
     * @author LiuRunYu 2026-06-25
     */
    @PostMapping("/save")
    public R save(@RequestBody MemoSaveReq req) {
        return memoService.saveOrUpdate(req);
    }

    /**
     * 删除笔记
     * @author LiuRunYu 2026-06-25
     */
    @PostMapping("/delete")
    public R delete(@RequestParam String id) {
        return memoService.deleteById(id);
    }
}
