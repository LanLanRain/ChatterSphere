package com.lb.live.user.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * 不同业务场景的code，区别不同的延迟消息
 *
 * @author RainSoul
 * @create 2024-09-23
 */
public class UserCacheAsyncDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code;

    private String json;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}

