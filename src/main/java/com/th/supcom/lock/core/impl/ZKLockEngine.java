package com.th.supcom.lock.core.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.supcom.lock.core.DistLockInfo;
import com.th.supcom.lock.core.ILockEngine;
import com.th.supcom.lock.core.LockEngineHelper;

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
        // 不是可重入锁，容易引起死锁，此问题尚未解决
        DistLockInfo primaryDistLockInfo = getPrimaryDistLockInfo (lockInfo.getLockKey ());
        if (null != primaryDistLockInfo)
        {
            if (LockEngineHelper.isMySelf (lockInfo, primaryDistLockInfo))
            {
                log.warn ("该subject又过来获取这把可重入锁了，{}", lockInfo);
                return true;
            }
            return false;

        }
        try
        {
            String encodeZooKeeperPath = encodeZooKeeperPath (lockInfo.getLockKey ());
            zkClient.create ().creatingParentsIfNeeded ().withMode (CreateMode.EPHEMERAL)
                    .forPath (encodeZooKeeperPath, lockInfo.getLockValue ().getBytes ());
            return true;
        }
        catch (Exception e)
        {
            log.error ("锁被其他subject争抢了:{}", lockInfo);
        }

        return false;
    }

    @Override
    public boolean releaseLock (DistLockInfo lockInfo)
    {
        DistLockInfo primaryDistLockInfo = getPrimaryDistLockInfo (lockInfo.getLockKey ());
        String encodeZooKeeperPath = encodeZooKeeperPath (lockInfo.getLockKey ());
        if (null == primaryDistLockInfo)
        {
            return false;
        }

        if (LockEngineHelper.isMySelf (lockInfo, primaryDistLockInfo))
        {
            try
            {
                zkClient.delete ().forPath (encodeZooKeeperPath);
                return true;
            }
            catch (Exception e)
            {
                log.error ("执行ZKLockEngine.releaseLock()异常", e);
            }
            return false;
        }
        return false;

    }

    @Override
    public DistLockInfo getPrimaryDistLockInfo (String lockKey)
    {
        String encodeZooKeeperPath = encodeZooKeeperPath (lockKey);
        DistLockInfo lockInfo = null;
        try
        {
            byte[] lockValueByteArray = zkClient.getData ().forPath (encodeZooKeeperPath);
            lockInfo = new DistLockInfo ();
            lockInfo.setLockKey (lockKey);
            lockInfo.setLockValue (new String (lockValueByteArray));
            return lockInfo;
        }
        catch (Exception e)
        {
           // log.error ("执行ZKLockEngine.getPrimaryDistLockInfo()异常", e);

        }
        return null;
    }

    private String encodeZooKeeperPath (String lockKey)
    {
        String originalStr = LOCK_PATH + File.separator + lockKey;
        String resultStr = originalStr;
        try
        {
            resultStr = LOCK_PATH + File.separator + java.net.URLEncoder.encode (lockKey, ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error ("执行ZKLockEngine.encodeZooKeeperPath()异常", e);
        }
        return resultStr;
    }

}
