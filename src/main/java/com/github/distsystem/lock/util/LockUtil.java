
package com.github.distsystem.lock.util;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 锁工具类
 * 
 * @function LockUtil
 * @date 2019年1月16日 下午2:27:29
 * @author 李桥
 * @version 1.0
 */
public class LockUtil
{
    /** 获取锁默认超时时间：秒 */
    public static final long DEFAULT_TIMEOUT = 30 * 1000;
    /** 锁默认过期时间：秒 */
    public static final long DEFAULT_EXPIRE = 300 * 1000;
    private static final String DEFAULT_DELIMITER = "-";

    private LockUtil ()
    {
    }

    public static String getJvmPid ()
    {
        String pid = ManagementFactory.getRuntimeMXBean ().getName ();
        int indexOf = pid.indexOf ('@');
        if (indexOf > 0)
        {
            pid = pid.substring (0, indexOf);
            return pid;
        }
        throw new IllegalStateException ("ManagementFactory error");
    }

    public static String getLocalIpByNetcard ()
    {
        try
        {
            for (Enumeration <NetworkInterface> e = NetworkInterface.getNetworkInterfaces (); e.hasMoreElements ();)
            {
                NetworkInterface item = e.nextElement ();
                for (InterfaceAddress address : item.getInterfaceAddresses ())
                {
                    if (item.isLoopback () || !item.isUp ())
                    {
                        continue;
                    }
                    if (address.getAddress () instanceof Inet4Address)
                    {
                        Inet4Address inet4Address = (Inet4Address) address.getAddress ();
                        return inet4Address.getHostAddress ();
                    }
                }
            }
            return InetAddress.getLocalHost ().getHostAddress ();
        }
        catch (SocketException | UnknownHostException e)
        {
            throw new RuntimeException (e);
        }
    }

    public static String getLockValue ()
    {
        String lockValue = LockUtil.getLocalIpByNetcard () + DEFAULT_DELIMITER + LockUtil.getJvmPid ()
                           + DEFAULT_DELIMITER + Thread.currentThread ().getId ();
        return lockValue;
    }

}
