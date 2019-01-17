 

package com.th.supcom.lock.core;

import java.util.Date;

import org.springframework.util.Assert;

import com.th.supcom.lock.util.LockUtil;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class LockTemplate
{

    
   

    @Setter
    private ILockEngine lockExecutor;

    public DistLockInfo lock (String key, long expire, long timeout) throws Exception
    {
        Assert.isTrue (timeout > 0, "tryTimeout must more than 0");
        long start = System.currentTimeMillis ();
        int acquireCount = 0;
        String lockValue =LockUtil.getLockValue ();
        DistLockInfo lockInfo=new DistLockInfo (key, lockValue, expire, timeout, acquireCount,null);
        while (System.currentTimeMillis () - start < timeout)
        {
            lockInfo.setAcquireCount (acquireCount);
            lockInfo.setCreateDate (new Date());
            boolean result = lockExecutor.acquire (lockInfo);
            acquireCount++;
            if (result)
            {
                log.info ("lock success, at times:{} ", acquireCount);
                return new DistLockInfo (key, lockValue, expire, timeout, acquireCount,new Date());
            }
            Thread.sleep (50);
        }
        log.info ("lock failed, try {} times", acquireCount);
        return null;
    }

    public boolean releaseLock (DistLockInfo lockInfo)
    {
        return lockExecutor.releaseLock (lockInfo);
    }
}
