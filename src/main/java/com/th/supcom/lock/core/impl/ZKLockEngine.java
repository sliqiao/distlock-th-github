package com.th.supcom.lock.core.impl;

import org.springframework.stereotype.Service;
import com.th.supcom.lock.core.DistLockInfo;
import com.th.supcom.lock.core.ILockEngine;
 

/**
 * 
 * @function 锁引擎-Zookeeper实现 
 * @date 2019年1月16日 下午2:49:24
 * @author 李桥
 * @version 1.0
 */
@Service
public class ZKLockEngine implements ILockEngine
{
    

    @Override
    public boolean acquire (String lockKey, String lockValue, long acquireExpire)
    {
        return false;
    }

    @Override
    public boolean releaseLock (DistLockInfo lockInfo)
    {
        return false;
    }

}
