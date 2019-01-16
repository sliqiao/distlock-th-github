package com.th.supcom.lock.core.impl;

import org.springframework.stereotype.Service;
import com.th.supcom.lock.core.DistLockInfo;
import com.th.supcom.lock.core.ILockEngine;
 

/**
 * <p>
 * D:\runtime\zookeeper-3.4.5\bin\启动.cmd 
 * 这是本地安装的zookeeper服务,客户端使用D:\runtime\zookeeper-3.4.5\bin\zktools\zktools.exe
 * 
 * </p>
 * 
 * @function 锁引擎-Zookeeper实现 
 * @date 2019年1月16日 下午2:49:24
 * @author 李桥
 * @version 1.0
 */
@Service
public class ZKLockEngine implements ILockEngine
{
    

    @Override
    public boolean acquire (DistLockInfo lockInfo)
    {
        return false;
    }

    @Override
    public boolean releaseLock (DistLockInfo lockInfo)
    {
        return false;
    }

}
