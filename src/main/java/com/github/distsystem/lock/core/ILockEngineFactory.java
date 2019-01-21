
package com.github.distsystem.lock.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.github.distsystem.lock.core.impl.MysqlLockEngine;
import com.github.distsystem.lock.core.impl.OracleLockEngine;
import com.github.distsystem.lock.core.impl.RedisLockEngine;
import com.github.distsystem.lock.core.impl.ZKLockEngine;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @function 锁引擎工厂类 
 * @date 2019年1月16日 下午2:29:30
 * @author 李桥
 * @version 1.0
 */
@Service
@Slf4j
public class ILockEngineFactory
{
    @Value("${distlock.implementor}")
    private String distlockImplementor;
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
        ILockEngineType lockEngineType= ILockEngineType.getByName (distlockImplementor);
        log.info ("默认的分布锁引擎是："+lockEngineType.name ());
        return getInstance (lockEngineType);
    }

    public static enum ILockEngineType
    {
      Redis, Mysql, Oracle, Zookeeper;
     public static ILockEngineType getByName (String name)
        {
          for(ILockEngineType e:ILockEngineType.values ()){
              if(StringUtils.equals (name, e.name ())){
                  return e;
              }
          }
          return Zookeeper;
        }
    }

}
