package com.gotkx.threadlocal.case1;

import java.util.Random;

public class ThreadScopeShareData {
    /**
     * 定义一个ThreadLocal
     */
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>();

    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int data = new Random().nextInt();

                    System.out.println(Thread.currentThread().getName() + " has put a data: " + data);

                    threadLocal.set(data);

                    //直接往threadLocal里面里面扔数据即可
                    new TestA().getData();

                    new TestB().getData();
                }
            }).start();
        }
    }

    static class TestA {
        public void getData() {
            //直接取，不用什么关键字，它直接从当前线程中取
            System.out.println("A get data from " + Thread.currentThread().getName() + ": " + threadLocal.get());
        }
    }

    static class TestB {
        public void getData() {
            //直接取，不用什么关键字，它直接从当前线程中取
            System.out.println("B get data from " + Thread.currentThread().getName() + ": " + threadLocal.get());
        }
    }
}
