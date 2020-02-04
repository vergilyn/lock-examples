package com.vergilyn.examples;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

/**
 * {@linkplain Integer}、{@linkplain AtomicInteger} 分别在多线程下，从0开始递增。 <br/>
 * 因为 AtomicInteger 是基于CAS实现（乐观锁），所以可以预料其结果。而 Integer 无法预料最后结果。 <br/>
 *
 * <p>特别：<br/>
 *   AtomicInteger 可以预测最后结果，但不能预测每次的结果。即`code-1`打印的结果也不是递增。原因：<br/>
 *   假设thread-a执行到`code-01`时被挂起（`code-01`还未执行），thread-b被唤醒并又一次执行到`code-01`被挂起，此时thread-c被唤醒，执行完成并打印`atomicInteger=3`。
 *   thread-c任务执行完成，分别唤醒`thread-a`、`thread-b`，此时 a&b 的打印结果都是`atomicInteger=3`。
 * </p>
 * @author vergilyn
 * @date 2020-01-30
 */
public class AtomicObjectTestng {
    private static final int THREAD_POOL_SIZE = 10;
    private static final int INVOCATION_COUNT = 20;

    private Integer integer = 0;
    /**
     * volatile 保证了可见性，但并无法保证其最终结果正确
     */
    private volatile Integer volatileInteger = 0;
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Test(threadPoolSize = THREAD_POOL_SIZE, invocationCount = INVOCATION_COUNT)
    public void test() throws InterruptedException {
        // 底层调用了`native`方法
        atomicInteger.addAndGet(1); // 等价于 atomicInteger.incrementAndGet();

        integer = integer + 1;

        Thread.sleep(1000);
        volatileInteger = volatileInteger + 1;

        System.out.printf("thread-name: %s >>>> integer = %d, volatileInteger = %d, atomicInteger = %d \r\n",
                Thread.currentThread().getName(), integer, volatileInteger, atomicInteger.get());  // code-1

    }

    @AfterTest
    public void afterTest(){
        System.out.printf("afterTest, invocation_count: %d >>>> integer = %d, volatileInteger = %d, atomicInteger = %d",
                INVOCATION_COUNT, integer, volatileInteger, atomicInteger.get());
    }
}
