package com.vergilyn.examples.spin;

import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

/**
 * 最基本的自旋锁：<br/>
 *   1. 不可重入
 *   2. 非公平锁
 * @author vergilyn
 * @date 2020-02-03
 */
public class SpinLockTestng {
    private AtomicBoolean available = new AtomicBoolean(false);
    private int num = 0;

    @Test(invocationCount = 10, threadPoolSize = 5)
    public void test() throws InterruptedException {
        String thread = Thread.currentThread().getName();
        System.out.println(thread + " >>>> begin");

        lock();

        System.out.println(thread + " >>>> body, num = " + (++num));

        unLock();

        System.out.println(thread + " >>>> end");

    }

    @AfterTest
    private void afterTest(){
        System.out.println("afterTest >>>> num = " + num);
    }

    /**
     * 自旋锁核心
     */
    private void lock() throws InterruptedException{
        String thread = Thread.currentThread().getName();

        // 循环检测尝试获取锁
        while (!tryLock()){
            // doSomething...
            // Thread.sleep(1000);  // code-01
        }

        System.out.println(thread + " acquired lock!");
    }

    private boolean tryLock(){
        // 尝试获取锁，成功返回true，失败返回false
        return available.compareAndSet(false,true);
    }

    private void unLock(){
        String thread = Thread.currentThread().getName();

        if(!available.compareAndSet(true,false)){
            throw new RuntimeException("释放锁失败");
        }

        System.out.println(thread+ " unlock!");
    }

}
