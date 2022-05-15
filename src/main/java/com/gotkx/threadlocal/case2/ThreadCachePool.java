package com.gotkx.threadlocal.case2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池的概念与Executors类的使用
 * 缓存的线程池
 *
 * @author HuangKai
 * @date 2022/5/15 10:28
 */
public class ThreadCachePool {
    public static void main(String[] args) {
        //自动根据任务数量来设定线程数去服务，多了就增加线程数，少了就减少线程数
        //这貌似跟一般情况相同，因为一般也是一个线程执行一个任务，但是这里的好处是：如果有个线程死了，它又会产生一个新的来执行任务
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 1; i <= 10; i++) {
            //扔5个任务
            final int task = i;
            threadPool.execute(new Runnable() {
                //向池子中扔任务，任务即一个Runnable
                @Override
                public void run() {
                    for (int j = 1; j <= 5; j++) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + " looping of " + j + " for task of " + task);
                    }
                }
            });
        }

        System.out.println("all of 10 tasks have committed!");

        //执行完任务后关闭
        threadPool.shutdown();
    }
}

