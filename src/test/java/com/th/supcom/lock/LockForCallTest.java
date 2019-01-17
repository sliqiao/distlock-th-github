package com.th.supcom.lock;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.th.supcom.lock.model.User;
import com.th.supcom.lock.service.UserServiceForCall;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LockForCallTest.class)
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class LockForCallTest {

    private static final Random RANDOM = new Random();

    @Autowired
    UserServiceForCall userService;

    public static void main(String[] args) {
        SpringApplication.run(LockForCallTest.class, args);
    }

    @Test
    public void lockTest() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            public void run() {
                try {
                    userService.simple1();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 1; i++) {
            executorService.submit(task);
        }
        System.in.read ();
    }
    
    @Test
    public void simple1Test() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            public void run() {
                try {
                    userService.simple1();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }
        System.in.read ();
    }

    @Test
    public void simple2Test() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            public void run() {
                try {
                    userService.simple2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }
        System.in.read ();
    }

    @Test
    public void spel1Test() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            public void run() {
                try {
                    userService.method1(new User(RANDOM.nextLong(), "苞米豆"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }
        System.in.read ();
    }

    @Test
    public void spel2Test() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        final User user=new User(RANDOM.nextLong(), "苞米豆");
        Runnable task = new Runnable() {
            public void run() {
                try {
                    userService.method2(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 5; i++) {
            executorService.submit(task);
        }
         System.in.read ();
    }
}