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
@EnableConfigurationProperties (ZkProps.class)
public class ZKConfig
{
    private final ZkProps zkProps;

    @Autowired
    public ZKConfig (ZkProps zkProps)
    {
        this.zkProps = zkProps;
    }

    @Bean
    public CuratorFramework curatorFramework ()
    {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry (zkProps.getTimeout (), zkProps.getRetry ());
        CuratorFramework client = CuratorFrameworkFactory.newClient (zkProps.getUrl (), retryPolicy);
        client.start ();
        return client;
    }

  
}
