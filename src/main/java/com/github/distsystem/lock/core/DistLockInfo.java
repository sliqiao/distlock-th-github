
package com.github.distsystem.lock.core;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.github.distsystem.lock.util.LockUtil;
import com.github.distsystem.lock.util.SpringCxtUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @function 分布式锁具体实现
 * @date 2019年1月16日 下午2:29:30
 * @author 李桥
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class DistLockInfo implements IDistLock
{
    private static final ThreadLocal <AtomicInteger> REENTRANT_COUNT = new ThreadLocal <AtomicInteger> ()
    {
        @Override
        protected AtomicInteger initialValue ()
        {
            return new AtomicInteger (0);
        }
    };

    /** 锁名称 */
    private String lockKey;
    /** 锁值 */
    private String lockValue;
    /** 过期时间 */
    private Long expire;
    /** 获取锁超时时间 */
    private Long acquireTimeout;
    /** 尝试获取锁次数 */
    private int acquireCount = 0;
    /** 创建时间 */
    private Date createDate;

    private InterProcessMutex zkInterProcessMutex = null;

    private DistLockInfo ()
    {

    }

    public static int getReentrantCount ()
    {
        return REENTRANT_COUNT.get ().get ();
    }

    public static void addReentrantCount ()
    {
        REENTRANT_COUNT.get ().incrementAndGet ();

    }

    public static void subReentrantCount ()
    {
        REENTRANT_COUNT.get ().decrementAndGet ();

    }

    public static void clearReentrantCount ()
    {
        REENTRANT_COUNT.remove ();

    }

    @Override
    public boolean lock (Long expire, Long timeout)
    {
        this.setExpire (expire);
        this.setAcquireTimeout (timeout);
        LockTemplate lockTemplate = SpringCxtUtil.getBean (LockTemplate.class);
        return null != lockTemplate.lock (this);
    }

    @Override
    public boolean lock ()
    {

        return lock (LockUtil.DEFAULT_EXPIRE, LockUtil.DEFAULT_TIMEOUT);
    }

    @Override
    public boolean unlock ()
    {
        LockTemplate lockTemplate = SpringCxtUtil.getBean (LockTemplate.class);
        return lockTemplate.releaseLock (this);

    }

    public static IDistLock newLock (String lockKey)
    {
        DistLockInfo distLock = new DistLockInfo ();
        distLock.setCreateDate (new Date ());
        distLock.setLockKey (lockKey);
        distLock.setLockValue (LockUtil.getLockValue ());
        return distLock;
    }

    public static DistLockInfo newDistLockInfo (String lockKey, Long expire, Long acquireTimeout)
    {
        DistLockInfo distLock = new DistLockInfo ();
        distLock.lockKey = lockKey;
        distLock.expire = expire;
        distLock.acquireTimeout = acquireTimeout;
        return distLock;
    }
    
    public static DistLockInfo newDistLockInfo ()
    {
        DistLockInfo distLock = new DistLockInfo ();
        return distLock;
    }

}
