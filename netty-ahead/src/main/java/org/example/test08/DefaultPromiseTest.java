package org.example.test08;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 测试 DefaultPromise
 *
 * Promise 让它具有 Runnable 身份
 *
 * @author wangfeie
 * @date 2023/12/13
 */
public class DefaultPromiseTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        //创建一个Callable
        Callable<Integer> callable = () -> {
            System.out.println(Thread.currentThread().getName());
            //睡眠一会
            Thread.sleep(5000);
            return 1314;
        };

        //创建一个DefaultPromise，把任务传进DefaultPromise中
        Promise<Integer> promise = new DefaultPromise<Integer>(callable);
        //创建一个线程
        Thread t = new Thread(promise);
        t.start();
        //无超时获取结果
        //System.out.println(promise.get());
        //有超时获取结果
        System.out.println(promise.get(2000, TimeUnit.MILLISECONDS));
    }
}