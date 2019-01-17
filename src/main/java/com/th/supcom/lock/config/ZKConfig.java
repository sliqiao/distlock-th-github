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
@EnableConfigurationProperties (ZKPros.class)
public class ZKConfig
{
    private final ZKPros zkProperties;

    @Autowired
    public ZKConfig (ZKPros ZKProperties)
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
