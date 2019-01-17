package com.th.supcom.lock.core.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.supcom.lock.core.DistLockInfo;
import com.th.supcom.lock.core.ILockEngine;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * D:\runtime\zookeeper-3.5.4-beta\zookeeper-3.5.4-beta\bin\启动.cmd
 * 这是本地安装的zookeeper服务,客户端使用D:\runtime\zookeeper-3.5.4-beta\zookeeper-3.5.4-beta\
 * bin\zktools\zktools.exe ,开发环境连接地址：10.0.2.190:2181
 * </p>
 * 
 * @function 锁引擎-Zookeeper实现
 * @date 2019年1月16日 下午2:49:24
 * @author 李桥
 * @version 1.0
 */
@Service
@Slf4j
public class ZKLockEngine implements ILockEngine
{
    @Autowired
    private CuratorFramework zkClient;

    private static final String LOCK_PATH = "/distlock";
    private static final String ENCODING = "utf-8";

    @Override
    public boolean acquire (DistLockInfo lockInfo)
    {
        // 不是可重入锁，容易引起死锁
        InterProcessMutex oldzkInterProcessMutex = lockInfo.getZkInterProcessMutex ();
        if (oldzkInterProcessMutex != null && oldzkInterProcessMutex.isAcquiredInThisProcess ()
            && oldzkInterProcessMutex.isOwnedByCurrentThread ())
        {
            log.warn ("该subject又过来获取这把可重入锁了，{}",lockInfo);
            return true;
        }
        String encodeZooKeeperPath = encodeZooKeeperPath (lockInfo);
        InterProcessMutex zkInterProcessMutex = new InterProcessMutex (zkClient, encodeZooKeeperPath);
        lockInfo.setZkInterProcessMutex (zkInterProcessMutex);
        try
        {
            return zkInterProcessMutex.acquire (2000, TimeUnit.MILLISECONDS);
        }
        catch (Exception e)
        {
            log.error ("执行ZKLockEngine.acquire()异常", e);
        }

        return false;
    }

    @Override
    public boolean releaseLock (DistLockInfo lockInfo)
    {
        InterProcessMutex lock = lockInfo.getZkInterProcessMutex ();
        if (null == lock)
        {
            return true;
        }
        try
        {
            if (lock.isAcquiredInThisProcess () && lock.isOwnedByCurrentThread ())
            {
                lock.release ();
                lockInfo.setZkInterProcessMutex (null);
                return true;
            }
            log.error ("执行ZKLockEngine.releaseLock()失败，当前InterProcessMutex的owner不是当前线程！");

        }
        catch (Exception e)
        {
            log.error ("执行ZKLockEngine.releaseLock()异常", e);
        }

        return false;
    }

    private String encodeZooKeeperPath (DistLockInfo lockInfo)
    {
        String originalStr = LOCK_PATH + File.separator + lockInfo.getLockKey ();
        String resultStr = originalStr;
        try
        {
            resultStr = LOCK_PATH + File.separator + java.net.URLEncoder.encode (lockInfo.getLockKey (), ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error ("执行ZKLockEngine.encodeZooKeeperPath()异常", e);
        }
        return resultStr;
    }

    @Override
    public DistLockInfo getPrimaryDistLockInfo (String lockKey)
    {

        return null;
    }
}
