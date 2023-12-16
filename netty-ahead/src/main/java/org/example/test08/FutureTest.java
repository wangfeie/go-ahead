package org.example.test08;

import java.util.concurrent.*;

/**
 * FutureTask
 *
 */
public class FutureTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //test1();
    }


    public static void test1() throws ExecutionException, InterruptedException {
        //创建一个Callable
        Callable<Integer> callable = () -> 1314;
        //创建一个FutureTask，把任务传进FutureTask中
        FutureTask<Integer> future = new FutureTask<Integer>(callable);
        //创建一个线程
        Thread t = new Thread(future);
        t.start();
        //无超时获取结果
        System.out.println(future.get());
    }


    public static void test2() throws ExecutionException, InterruptedException {
        //创建一个Callable
        Callable<Integer> callable = () -> 1314;
        //创建一个FutureTask，把任务传进FutureTask中
        ExecutorService threadPool = Executors.newCachedThreadPool();
        Future<?> submit = threadPool.submit(callable);
        //无超时获取结果
        System.out.println(submit.get());
    }


    public static void test3() throws ExecutionException, InterruptedException {
        //创建一个Callable
        Callable<Integer> callable = () -> 1314;
        //创建一个FutureTask，把任务传进FutureTask中
        FutureTask<Integer> future = new FutureTask<Integer>(callable);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.submit(future);
        //无超时获取结果
        System.out.println(future.get());
    }
}