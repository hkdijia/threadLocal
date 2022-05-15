package com.gotkx.threadlocal.case4;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class LockTest {

    public static void main(String[] args) {
        new LockTest().init();
    }

    private void init() {
        final Outputer outputer = new Outputer();
        // 线程1打印：duoxiancheng
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    outputer.output("duoxiancheng");
                }

            }
        }).start();
        ;

        // 线程2打印：gotkx
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    outputer.output("gotkx");
                }

            }
        }).start();
        ;
    }

    /**
     * 自定义一个类，保存锁和待执行的任务
     */
    static class Outputer {
        //定义一个锁，Lock是个接口，需实例化一个具体的Lock
        Lock lock = new ReentrantLock();

        //字符串打印方法，一个个字符的打印
        public void output(String name) {
            int len = name.length();
            lock.lock();
            try {
                for (int i = 0; i < len; i++) {
                    System.out.print(name.charAt(i));
                }
                System.out.println("");
            } finally {
                //try起来的原因是万一一个线程进去了然后挂了或者抛异常了，那么这个锁根本没有释放
                lock.unlock();
            }
        }
    }
}