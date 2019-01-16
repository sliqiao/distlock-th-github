
package com.th.supcom.lock.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.th.supcom.lock.aop.LockAnnotationAdvisor;
import com.th.supcom.lock.aop.LockAnnotationAdvisor.LockMethodInterceptor;
import com.th.supcom.lock.core.ILockEngine;
import com.th.supcom.lock.core.ILockEngineFactory;
import com.th.supcom.lock.core.ILockEngineFactory.ILockEngineType;
import com.th.supcom.lock.core.LockTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 分布式锁自动配置器
 *
 * @author zengzh TaoYu
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class LockAutoConfiguration
{
    @Autowired
    private MyDatabaseProperties multipleDatabaseProperties;

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
    public LockTemplate lockTemplate (ILockEngineFactory iLockEngineFactory)
    {
        //在这里切换默认的锁引擎实现 
        ILockEngine iLockEngine = iLockEngineFactory.getDefaultInstance ();
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

    @Bean (name = "myDataSource")
    @Primary
    public DataSource myDataSource (MyDatabaseProperties myDatabaseProperties)
    {

        DruidDataSource dataSource = new DruidDataSource ();
        dataSource.setDriverClassName (myDatabaseProperties.getMyDataSource ().getDriverClassName ());
        dataSource.setUrl (myDatabaseProperties.getMyDataSource ().getUrl ());
        dataSource.setUsername (myDatabaseProperties.getMyDataSource ().getUsername ());
        dataSource.setPassword (myDatabaseProperties.getMyDataSource ().getPassword ());
        dataSource.setInitialSize (10);
        dataSource.setMinIdle (10);
        dataSource.setMaxActive (300);
        dataSource.setMaxWait (30000);
        dataSource.setRemoveAbandoned (true);
        dataSource.setRemoveAbandonedTimeout (1800);
        dataSource.setLogAbandoned (false);
        dataSource.setTimeBetweenEvictionRunsMillis (60000);
        dataSource.setMinEvictableIdleTimeMillis (300000);
        dataSource.setValidationQuery ("SELECT 1 FROM DUAL");
        dataSource.setTestWhileIdle (true);
        dataSource.setTestOnBorrow (false);
        dataSource.setTestOnReturn (false);
        dataSource.setPoolPreparedStatements (true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize (500);
        dataSource.setKeepAlive (true);
         dataSource.setDbType ("mysql");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate (DataSource myDataSource)
    {
        JdbcTemplate jdbcTemplate = new JdbcTemplate (myDataSource);
        return jdbcTemplate;
    }

}
