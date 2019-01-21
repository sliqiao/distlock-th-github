package com.github.distsystem.lock.core;

import com.alibaba.druid.util.StringUtils;

/**
 * 
 * @function 锁引擎辅助类：LockEngineHelper
 * @date 2019年1月18日 下午2:15:07
 * @author 李桥
 * @version 1.0
 */
public class LockEngineHelper
{
    private LockEngineHelper ()
    {
    }

    public static boolean isMySelf (DistLockInfo lockInfoContext, DistLockInfo distLockInfo)
    {
        if (null == lockInfoContext)
        {
            return false;
        }
        if (null == distLockInfo)
        {
            return false;
        }
        if (StringUtils.equals (lockInfoContext.getLockValue (), distLockInfo.getLockValue ()))
        {
            return true;
        }

        return false;
    }
}
