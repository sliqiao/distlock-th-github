package com.th.supcom.lock.service;

import org.springframework.stereotype.Service;

import com.th.supcom.lock.core.DistLockInfo;
import com.th.supcom.lock.core.IDistLock;
import com.th.supcom.lock.model.User;

@Service
public class UserServiceForCall
{

    public void simple1 ()
    {
   
        IDistLock distLock = DistLockInfo.newLock ("simple1");
        try
        {
            distLock.lock ();
            System.out.println ("开始--执行simple1 , 当前线程:" + Thread.currentThread ().getName ());
            Thread.sleep (1000 * 30);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace ();
        }
        finally
        {
            System.out.println ("结束--执行simple1 , 当前线程:" + Thread.currentThread ().getName ());
            distLock.unlock ();
        }
      

    }

    public void simple2 ()
    {
      
        IDistLock distLock = DistLockInfo.newLock ("simple2");
        try
        {
            distLock.lock ();
            System.out.println ("开始--执行simple2方法 , 当前线程:" + Thread.currentThread ().getName ());
            Thread.sleep (1000 * 30);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace ();
        }
        finally
        {
            System.out.println ("结束--执行simple2方法 , 当前线程:" + Thread.currentThread ().getName ());
            distLock.unlock ();
        }
       

    }

    public User method1 (User user)
    {

      
        IDistLock distLock = DistLockInfo.newLock ("method1");
        try
        {
            distLock.lock ();
            System.out.println ("开始--执行method1 , 当前线程:" + Thread.currentThread ().getName ());
            Thread.sleep (1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace ();
        }
        finally
        {
            System.out.println ("结束--执行method1 , 当前线程:" + Thread.currentThread ().getName ());
            distLock.unlock ();
        }
      
        return user;
    }

    public User method2 (User user)
    {
     
        IDistLock distLock = DistLockInfo.newLock (user.getId ()+"");
        try
        {
            
            distLock.lock ();
            System.out.println ("开始--执行method2 , 当前线程:" + Thread.currentThread ().getName ());
            Thread.sleep (1000*20);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace ();
        }
        finally
        {
            System.out.println ("结束--执行method2 , 当前线程:" + Thread.currentThread ().getName ());
            distLock.unlock ();
        }
       
        return user;
    }

}