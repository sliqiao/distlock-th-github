
package com.th.supcom.lock.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @function 
 * @date 2019年1月16日 下午2:29:30
 * @author 李桥
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class DistLockInfo
{

    /**
     * 锁名称
     */
    private String lockKey;

    /**
     * 锁值
     */
    private String lockValue;

    /**
     * 过期时间
     */
    private Long expire;

    /**
     * 获取锁超时时间
     */
    private Long acquireTimeout;

    /**
     * 尝试获取锁次数
     */
    private int acquireCount = 0;
}
