
package com.th.supcom.lock.core;

import java.util.Date;

import com.th.supcom.lock.util.LockUtil;
import com.th.supcom.lock.util.SpringContextUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @function
 * @date 2019年1月16日 下午2:29:30
 * @author 李桥
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistLockInfo implements IDistLock
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
    /**
     * 创建时间
     */
    private Date createDate;

    @Override
    public boolean lock (Long expire, Long acquireTimeout)
    {
        this.setExpire (expire);
        this.setAcquireTimeout (acquireTimeout);
        ILockEngineFactory lockEngineFactory = SpringContextUtil.getBean (ILockEngineFactory.class);
        ILockEngine lockEngine = lockEngineFactory.getDefaultInstance ();
        return lockEngine.acquire (this);
    }

    @Override
    public boolean lock ()
    {

        return lock (LockUtil.DEFAULT_EXPIRE, LockUtil.DEFAULT_TIMEOUT);
    }

    @Override
    public boolean unlock ()
    {
        ILockEngineFactory lockEngineFactory = SpringContextUtil.getBean (ILockEngineFactory.class);
        ILockEngine lockEngine = lockEngineFactory.getDefaultInstance ();
        return lockEngine.releaseLock (this);

    }

    public static IDistLock newLock (String lockKey)
    {
        DistLockInfo distLock = new DistLockInfo ();
        distLock.setCreateDate (new Date());
        distLock.setLockKey (lockKey);
        distLock.setLockValue (LockUtil.getLockValue ());
        return distLock;
    }
}
