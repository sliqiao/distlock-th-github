package com.th.supcom.lock.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@EnableConfigurationProperties (ZKProperties.class)
public class ZKConfig
{
    private final ZKProperties zkProperties;

    @Autowired
    public ZKConfig (ZKProperties ZKProperties)
    {
        this.zkProperties = ZKProperties;
    }

    @Bean
    public CuratorFramework curatorFramework ()
    {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry (zkProperties.getTimeout (), zkProperties.getRetry ());
        CuratorFramework client = CuratorFrameworkFactory.newClient (zkProperties.getUrl (), retryPolicy);
        client.start ();
        return client;
    }

  
}
