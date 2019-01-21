package com.github.distsystem.lock.core.impl;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import com.alibaba.druid.util.StringUtils;
import com.github.distsystem.lock.core.DistLockInfo;
import com.github.distsystem.lock.core.ILockEngine;
import com.github.distsystem.lock.core.LockEngineHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @function 锁引擎-Redis实现
 * @date 2019年1月16日 下午2:49:24
 * @author 李桥
 * @version 1.0
 */
@Service
@Slf4j
public class RedisLockEngine implements ILockEngine
{
    private static RedisSerializer <String> stringSerializer = new StringRedisSerializer ();
    private static final RedisScript <String> SCRIPT_LOCK = new DefaultRedisScript<> ("return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])",
                                                                                      String.class);
    private static final RedisScript <String> SCRIPT_UNLOCK = new DefaultRedisScript<> ("if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])==1) else return 'false' end",
                                                                                        String.class);
    private static final String LOCK_SUCCESS = "OK";

    @Autowired
    private RedisTemplate <String, String > redisTemplate;

    @Override
    public boolean acquire (DistLockInfo lockInfo)
    {
        String lockKey = lockInfo.getLockKey ();
        DistLockInfo primaryDistLockInfo = getPrimaryDistLockInfo (lockKey);
        if (null !=primaryDistLockInfo )
        {
            if(LockEngineHelper.isMySelf (lockInfo, primaryDistLockInfo)){
                log.warn ("主体【{}】又过来获取这把可重入锁了，{}", lockInfo.getLockValue ());
                return true;
            }
            return false;

        }
        Object lockResult = redisTemplate.execute (SCRIPT_LOCK, stringSerializer, stringSerializer,
                                                   Collections.singletonList (lockKey), lockInfo.getLockValue (),
                                                   String.valueOf (lockInfo.getExpire ()));
        return LOCK_SUCCESS.equals (lockResult);
    }

    @Override
    public boolean releaseLock (DistLockInfo lockInfo)
    {
        Object releaseResult = redisTemplate.execute (SCRIPT_UNLOCK, stringSerializer, stringSerializer,
                                                      Collections.singletonList (lockInfo.getLockKey ()),
                                                      lockInfo.getLockValue ());
        return Boolean.valueOf (releaseResult.toString ());
    }

    @Override
    public DistLockInfo getPrimaryDistLockInfo (String lockKey)
    {
        String lockValue=redisTemplate.opsForValue().get(lockKey);
        if(StringUtils.isEmpty (lockValue)){
            return null;
        }
        DistLockInfo distLockInfo=DistLockInfo.newDistLockInfo ();
        distLockInfo.setLockKey (lockValue);
        return distLockInfo;
    }
}
