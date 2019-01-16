 
package com.th.supcom.lock.core;

 

 
public interface ILockEngine {

    /**
     * 加锁
     *
     * @param lockKey     锁标识
     * @param lockValue   锁值
     * @param acquireExpire  到期时间 毫秒
     * @throws Exception
     * @return 锁信息
     */
    boolean acquire(String lockKey, String lockValue,long acquireExpire);

    /**
     * 解锁
     *
     * <pre>
     * 为何解锁需要校验lockValue
     * 客户端A加锁，一段时间之后客户端A解锁，在执行releaseLock之前，锁突然过期了。
     * 此时客户端B尝试加锁成功，然后客户端A再执行releaseLock方法，则将客户端B的锁给解除了。
     * </pre>
     *
     * @param lockInfo 获取锁返回的对象
     * @return 是否释放成功
     */
    boolean releaseLock(DistLockInfo lockInfo);

}
