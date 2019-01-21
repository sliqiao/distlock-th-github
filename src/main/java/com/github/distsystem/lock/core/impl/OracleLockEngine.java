package com.github.distsystem.lock.core.impl;

import org.springframework.stereotype.Service;

import com.github.distsystem.lock.core.DistLockInfo;
import com.github.distsystem.lock.core.ILockEngine;

/**
 * 
 * @function 锁引擎-Oracle实现
 * @date 2019年1月16日 下午2:49:24
 * @author 李桥
 * @version 1.0
 */
@Service
public class OracleLockEngine implements ILockEngine
{

    @Override
    public boolean acquire (DistLockInfo lockInfo)
    {
        // todo 请完成代码
        return false;
    }

    @Override
    public boolean releaseLock (DistLockInfo lockInfo)
    {
        // todo 请完成代码
        return false;
    }

    @Override
    public DistLockInfo getPrimaryDistLockInfo (String lockKey)
    {
        // todo 请完成代码
        return null;
    }
}
