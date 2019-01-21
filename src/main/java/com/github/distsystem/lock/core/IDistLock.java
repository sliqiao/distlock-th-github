package com.github.distsystem.lock.core;


/**
 * 
 * @function 分布式锁
 * @date 2019年1月14日 上午10:28:51
 * @author 李桥
 * @version 1.0
 */
public interface IDistLock  
{ 
    public   boolean lock (Long expire,Long timeout);
    public   boolean lock ();
    public   boolean unlock ();
}
