package com.xiaoi.wxgzh.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kevin.zhu
 * @date 2020/2/21 18:37
 */
@Data
public class TokenInfo {
    /**
     * token
     */
    private String token;

    /**
     * 过期时间
     */
    private Long expireTime;

    public TokenInfo(String token, Long expireIn) {
        this.token = token;
        this.expireTime = System.currentTimeMillis() + expireIn * 1000;
    }

    /**
     * 是否过期
     *
     * @return
     */
    public boolean isExpire() {
        if (token == null || System.currentTimeMillis() > expireTime) {
            return true;
        }
        return false;
    }

}
