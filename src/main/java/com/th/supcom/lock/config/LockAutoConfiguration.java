 
package com.th.supcom.lock.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.th.supcom.lock.aop.LockAnnotationAdvisor;
import com.th.supcom.lock.aop.LockAnnotationAdvisor.LockMethodInterceptor;
import com.th.supcom.lock.core.ILockEngine;
import com.th.supcom.lock.core.ILockEngineFactory;
import com.th.supcom.lock.core.ILockEngineFactory.ILockEngineType;
import com.th.supcom.lock.core.LockTemplate;

/**
 * 分布式锁自动配置器
 *
 * @author zengzh TaoYu
 * @since 1.0.0
 */
@Configuration
public class LockAutoConfiguration
{

    @Autowired (required = false)
    public void setRedisTemplate (RedisTemplate redisTemplate)
    {
        RedisSerializer stringSerializer = new StringRedisSerializer ();
        redisTemplate.setKeySerializer (stringSerializer);
        redisTemplate.setValueSerializer (stringSerializer);
        redisTemplate.setHashKeySerializer (stringSerializer);
        redisTemplate.setHashValueSerializer (stringSerializer);
    }

  

    @Bean
    @ConditionalOnMissingBean
    public LockTemplate lockTemplate (ILockEngineFactory iLockEngineFactory )
    {
        ILockEngine iLockEngine = iLockEngineFactory.getInstance (ILockEngineType.Redis);
        LockTemplate lockTemplate = new LockTemplate ();
        lockTemplate.setLockExecutor (iLockEngine);
        return lockTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public LockMethodInterceptor lockInterceptor (LockTemplate lockTemplate)
    {
        LockMethodInterceptor lockInterceptor = new LockMethodInterceptor ();
        lockInterceptor.setLockTemplate (lockTemplate);
        return lockInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public LockAnnotationAdvisor lockAnnotationAdvisor (LockMethodInterceptor lockInterceptor)
    {
        return new LockAnnotationAdvisor (lockInterceptor);
    }

}
