
package com.github.distsystem.lock.core;

import java.util.Date;

import org.springframework.util.Assert;

import com.github.distsystem.lock.util.LockUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class LockTemplate
{
    private static long TRY_LOCK_INTERVAL=500;
    
    private ILockEngine lockExecutor;

    public DistLockInfo lock (DistLockInfo lockInfo)
    {
        Assert.isTrue (lockInfo.getAcquireTimeout () > 0, "tryTimeout must more than 0");
        long start = System.currentTimeMillis ();
        int acquireCount = 0;
        String lockValue = LockUtil.getLockValue ();
        lockInfo.setLockValue (lockValue);
      
        while (System.currentTimeMillis () - start < lockInfo.getAcquireTimeout ())
        {
            lockInfo.setAcquireCount (acquireCount);
            lockInfo.setCreateDate (new Date ());
            boolean result = lockExecutor.acquire (lockInfo);
            if (result)
            {
                log.info ("lock success, at times:{} ,subject:{}", acquireCount, lockValue);
                return lockInfo;
            }
            acquireCount++;
            try
            {
                Thread.sleep (TRY_LOCK_INTERVAL);
            }
            catch (InterruptedException e)
            {
                log.error ("执行LockTemplate.lock()方法异常：", e);
            }
        }
        log.info ("lock failed, try {} times,subject:{}", acquireCount, lockValue);
        return null;
    }

    public boolean releaseLock (DistLockInfo lockInfo)
    {
        return lockExecutor.releaseLock (lockInfo);
    }
}
