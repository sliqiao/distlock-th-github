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
 * bin\zktools\zktools.exe
 * ,开发环境连接地址：10.0.2.190:2181
 * </p>
 * 
 * @function 锁引擎-Zookeeper实现
 * @date 2019年1月16日 下午2:49:24
 * @author 李桥
 * @version 1.0
 */
@Service
@Slf4j
public class ZKLockEngine_备份 implements ILockEngine
{
    @Autowired
    private CuratorFramework zkClient;

    private static final String LOCK_PATH = "/distlock";
    private static final String ENCODING = "utf-8";

    private static final ThreadLocal <InterProcessMutex> InterProcessMutex = new ThreadLocal <InterProcessMutex> ();

    private static InterProcessMutex getInterProcessMutex ()
    {
        return InterProcessMutex.get ();
    }

    private static void setInterProcessMutex (InterProcessMutex interProcessMutex)
    {
        InterProcessMutex.set (interProcessMutex);
    }
    private static void clearInterProcessMutex ()
    {
        InterProcessMutex.remove ();
    }

    @Override
    public boolean acquire (DistLockInfo lockInfo)
    {
        
        String encodeZooKeeperPath = encodeZooKeeperPath (lockInfo);
        InterProcessMutex lock = new InterProcessMutex (zkClient, encodeZooKeeperPath);
        if(lock.isAcquiredInThisProcess ()&&lock.isOwnedByCurrentThread ()){
            log.warn ("该subject又过来获取这把可重入锁了，{}",lockInfo);
            return true;
        }
    
        try
        {
            ZKLockEngine_备份.setInterProcessMutex (lock);
            return lock.acquire (200, TimeUnit.MILLISECONDS);
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
        InterProcessMutex lock = getInterProcessMutex ();
        if (null == lock)
        {
            return true;
        }
        try
        {
            lock.release ();
            return true;
        }
        catch (Exception e)
        {
            log.error ("执行ZKLockEngine.releaseLock()异常", e);
        }finally{
            ZKLockEngine_备份.clearInterProcessMutex ();
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
