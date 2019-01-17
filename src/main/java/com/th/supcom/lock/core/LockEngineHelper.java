package com.th.supcom.lock.core;

import com.alibaba.druid.util.StringUtils;

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
