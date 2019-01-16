
package com.th.supcom.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @function 分布式锁注解
 * @date 2019年1月16日 下午2:23:06
 * @author 李桥
 * @version 1.0
 */
@Target (value ={ ElementType.METHOD })
@Retention (value = RetentionPolicy.RUNTIME)
public @interface DistLock
{

    /**
     * KEY 默认包名+方法名
     */
    String[] keys() default "";

    /**
     * 过期时间 单位：毫秒
     * 
     * <pre>
     *     过期时间一定是要长于业务的执行时间.一定要请注意业务方法的执行时间与锁的过期时间的关系，否则会出现，方法尚未执行完，则锁会被强制收回的现象。
     * </pre>
     * <p>
     * 最好是，谁获取锁，谁释放锁，由调用者来控制，系统按照一个合理的expire来回收锁！
     * <p>
     */
    long expire() default 300 * 1000;

    /**
     * 获取锁超时时间 单位：毫秒
     * 
     * <pre>
     *     结合业务,建议该时间不宜设置过长,特别在并发高的情况下.
     * </pre>
     */
    long timeout() default 50 * 1000;

}
