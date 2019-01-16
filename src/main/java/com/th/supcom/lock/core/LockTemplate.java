 

package com.th.supcom.lock.core;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import com.th.supcom.lock.util.LockUtil;
@Slf4j
public class LockTemplate
{

    private static final String DEFAULT_DELIMITER="-";
    private static final String PROCESS_ID = LockUtil.getLocalIpByNetcard () + DEFAULT_DELIMITER + LockUtil.getJvmPid ();

    @Setter
    private ILockEngine lockExecutor;

    public DistLockInfo lock (String key, long expire, long timeout) throws Exception
    {
        Assert.isTrue (timeout > 0, "tryTimeout must more than 0");
        long start = System.currentTimeMillis ();
        int acquireCount = 0;
        String value = PROCESS_ID + DEFAULT_DELIMITER + Thread.currentThread ().getId ();

        while (System.currentTimeMillis () - start < timeout)
        {
            boolean result = lockExecutor.acquire (key, value, expire);
            acquireCount++;
            if (result)
            {
                return new DistLockInfo (key, value, expire, timeout, acquireCount);
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
