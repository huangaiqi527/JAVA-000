package com.huang.homework.week04;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Homework04 {
    public static void main(String[] args) throws InterruptedException {
        /**
         * 1.通过私有属性（公有属性亦可）获取线程执行结果
         */
        MyThread1 myThread1 = new MyThread1();
        Thread t = new Thread(myThread1);
        t.start();
        t.join();
        System.out.println("1.通过私有属性执行结果：" +  myThread1.getRetVal());

        /**
         * 2.使用Future获取执行结果
         */
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Integer> result = executor.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                return MyFibo.fibo(36);
            }
        });
        try {
            System.out.println("2.使用Future获取执行结果：" +  result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        /**
         * 3.使用FutureTask创建线程获取执行结果
         */
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
            public Integer call() throws Exception {
                return MyFibo.fibo(36);
            }} );
        new Thread(task).start();
        try {
            System.out.println("3.使用FutureTask创建线程获取执行结果：" +  task.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /**
         * 4.使用FutureTask提交到线程池获取执行结果
         */
        executor.submit(task);
        try {
            System.out.println("4.使用FutureTask提交到线程池获取执行结果：" +  task.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        /**
         * 6.使用CountDownLatch获取执行结果
         */
        Integer retVal = CompletableFuture.supplyAsync(()->{return MyFibo.fibo(36);}).join();
        System.out.println("5.使用CompletableFuture获取执行结果：" +  retVal);


        CountDownLatch countDownLatch= new CountDownLatch(1);
        Map<String, Integer> context = new HashMap<String, Integer>();
        Runnable myThread2 = new Runnable(){
            @Override
            public void run() {
                context.put("retVal",MyFibo.fibo(36));
                countDownLatch.countDown();
            }
        };
        new Thread(myThread2).start();
        countDownLatch.await();
        System.out.println("6.使用CountDownLatch获取执行结果：" +  context.get("retVal"));
        


    }



}


/**
 * 通过私有属性返回线程执行结果
 */
class MyThread1 implements Runnable{

    private int retVal = 0;

    @Override
    public void run() {
        retVal = MyFibo.fibo(36);
    }

    public int getRetVal() {
        return retVal;
    }

    public void setRetVal(int retVal) {
        this.retVal = retVal;
    }
}
