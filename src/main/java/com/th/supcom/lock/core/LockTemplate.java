 

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

    public DistLockInfo lock (String key, long expire, long timeout) 
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
            if (result)
            {
                log.info ("lock success, at times:{} ,subject:{}", acquireCount,lockValue);
                return new DistLockInfo (key, lockValue, expire, timeout, acquireCount,new Date());
            }
            acquireCount++;
            try
            {
                Thread.sleep (50);
            }
            catch (InterruptedException e)
            {
              log.error ("执行LockTemplate.lock()方法异常：",e);
            }
        }
        log.info ("lock failed, try {} times,subject:{}", acquireCount,lockValue);
        return null;
    }

    public boolean releaseLock (DistLockInfo lockInfo)
    {
        return lockExecutor.releaseLock (lockInfo);
    }
}
