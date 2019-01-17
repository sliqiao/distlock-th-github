package com.th.supcom.lock.service;

import org.springframework.stereotype.Service;

import com.th.supcom.lock.annotation.DistLock;
import com.th.supcom.lock.model.User;

@Service
public class UserServiceForAnnotation {

    @DistLock
    public void simple1() {
        System.out.println("开始--执行简单方法1 , 当前线程:" + Thread.currentThread().getName());
        try {
            Thread.sleep(1000*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("结束--执行简单方法1 , 当前线程:" + Thread.currentThread().getName());

    }

    @DistLock(keys = "myKey")
    public void simple2() {
        System.out.println("执行简单方法2 , 当前线程:" + Thread.currentThread().getName());

    }

    @DistLock(keys = "#user.id")
    public User method1(User user) {
        
        System.out.println("开始--执行spel方法1 , 当前线程:" + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("结束--执行spel方法1 , 当前线程:" + Thread.currentThread().getName());
        return user;
    }

    @DistLock(keys = {"#user.id", "#user.name"}, timeout = 5000, expire = 5000)
    public User method2(User user) {
        //模拟锁占用
        System.out.println("开始--执行spel方法2 , 当前线程:" + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("结束--执行spel方法2 , 当前线程:" + Thread.currentThread().getName());
        return user;
    }

}