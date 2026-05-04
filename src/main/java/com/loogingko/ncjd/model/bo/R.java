package com.loogingko.ncjd.model.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一前端响应类
 * 200=成功; 500=失败
 * @author LiuRunYu 2026-04-07
 */
@Data
public class R {
    private int code;
    private Object data;
    private Map<String, Object> extra;
    private List<String> success;
    private List<String> error;

    public R() {}

    public R(Object data) {
        if (data instanceof String) {
            // 失败
            this.error = new ArrayList<>();
            this.error.add((String)data);
            this.code = 500;
        } else {
            // 成功
            this.data = data;
            this.code = 200;
        }
    }

    public static R succ(Object data) {
        return new R(data);
    }
    public static R fail(String msg) {
        return new R(msg);
    }
    public boolean hasFailed() { return this.error != null && !this.error.isEmpty(); }    

    /* ====== 链式编程增强可读性  =================================== */
    public R success(String msg) {
        if (this.success == null) this.success = new ArrayList<>();
        this.success.add(msg);
        return this;
    }

    public R error(String msg) {
        if (this.error == null) this.error = new ArrayList<>();
        this.error.add(msg);
        return this;
    }

    public R extra(String key, Object extra) {
        if (this.extra == null) this.extra = new HashMap<>();
        this.extra.put(key, extra);
        return this;
    }

    public R extra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }

    public R code(int code) {
        this.code = code;
        return this;
    }
}

