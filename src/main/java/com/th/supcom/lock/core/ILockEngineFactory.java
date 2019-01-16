
package com.th.supcom.lock.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.supcom.lock.core.impl.MysqlLockEngine;
import com.th.supcom.lock.core.impl.OracleLockEngine;
import com.th.supcom.lock.core.impl.RedisLockEngine;
import com.th.supcom.lock.core.impl.ZKLockEngine;

/**
 * 
 * @function
 * @date 2019年1月16日 下午2:29:30
 * @author 李桥
 * @version 1.0
 */
@Service
public class ILockEngineFactory
{
    @Autowired
    private RedisLockEngine redisLockEngine;
    @Autowired
    private ZKLockEngine zkLockEngine;
    @Autowired
    private OracleLockEngine oracleLockEngine;
    @Autowired
    private MysqlLockEngine mysqlLockEngine;

    public ILockEngine getInstance (ILockEngineType iLockEngineType)
    {
        if (ILockEngineType.Redis == iLockEngineType)
        {
            return redisLockEngine;
        }
        if (ILockEngineType.Mysql == iLockEngineType)
        {
            return mysqlLockEngine;
        }
        if (ILockEngineType.Oracle == iLockEngineType)
        {
            return oracleLockEngine;
        }
        if (ILockEngineType.Zookeeper == iLockEngineType)
        {
            return zkLockEngine;
        }
        return null;
    }

    public ILockEngine getDefaultInstance ()
    {
        return mysqlLockEngine;
    }

    public static enum ILockEngineType
    {
     Redis, Mysql, Oracle, Zookeeper;
    }
}
