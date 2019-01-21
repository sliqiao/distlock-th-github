
package com.github.distsystem.lock.config;

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
import com.github.distsystem.lock.aop.LockAnnotationAdvisor;
import com.github.distsystem.lock.aop.LockAnnotationAdvisor.LockMethodInterceptor;
import com.github.distsystem.lock.core.ILockEngine;
import com.github.distsystem.lock.core.ILockEngineFactory;
import com.github.distsystem.lock.core.LockTemplate;

/**
 * 
 * @function
 * @date 2019年1月18日 下午2:12:48
 * @author 李桥
 * @version 1.0
 */
@Configuration
public class LockBeanConfig
{
    @SuppressWarnings ("unused")
    @Autowired
    private DBPros multipleDatabaseProperties;

    @SuppressWarnings (
    { "rawtypes", "unchecked" })
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
        ILockEngine iLockEngine = iLockEngineFactory.getDefaultInstance ();
        LockTemplate lockTemplate = new LockTemplate ();
        lockTemplate.setLockExecutor (iLockEngine);
        return lockTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public LockMethodInterceptor lockInterceptor (LockTemplate lockTemplate)
    {
        LockMethodInterceptor lockInterceptor = new LockMethodInterceptor (lockTemplate);
        return lockInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public LockAnnotationAdvisor lockAnnotationAdvisor (LockMethodInterceptor lockInterceptor)
    {
        return new LockAnnotationAdvisor (lockInterceptor);
    }

    @Bean
    @Primary
    public DataSource myDataSource (DBPros myDatabaseProperties)
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
