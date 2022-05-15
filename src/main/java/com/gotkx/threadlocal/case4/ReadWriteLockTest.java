package com.gotkx.threadlocal.case4;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest {

    public static void main(String[] args) {
		//封装共享的数据、读写锁和待执行的任务的类
        final Queue3 q3 = new Queue3();

		// 开启三个线程写数据
        for (int i = 0; i < 3; i++) {
            new Thread() {
                public void run() {
                    while (true) {
                        q3.put(new Random().nextInt(10000));
                    }
                }
            }.start();

            new Thread() { // 开启三个线程读数据
                public void run() {
                    while (true) {
                        q3.get();
                    }
                }
            }.start();
        }
    }
}

class Queue3 {
	/**
	 * 共享的数据
	 */
    private Object data = null;
	/**
	 * 定义读写锁
	 */
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

	/**
	 * 读取数据的任务方法
	 */
	public void get() {
        rwl.readLock().lock(); // 上读锁
        try {
			// 读之前打印数据显示
            System.out.println(Thread.currentThread().getName() + ":before read: " + data);
			// 睡一会儿~
            Thread.sleep((long) (Math.random() * 1000));
			// 读之后打印数据显示
            System.out.println(Thread.currentThread().getName()  + ":after read: " + data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwl.readLock().unlock();// 释放读锁
        }
    }

	/**
	 * 写数据的任务方法
	 * @param data
	 */
	public void put(Object data) {
        rwl.writeLock().lock(); // 上写锁
        try {
			// 读之前打印数据显示
            System.out.println(Thread.currentThread().getName() + ":before write: " + this.data);
			// 睡一会儿~
            Thread.sleep((long) (Math.random() * 1000));
			//写数据
            this.data = data;
			// 读之后打印数据显示
            System.out.println(Thread.currentThread().getName() + ":after write: " + this.data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwl.writeLock().unlock();// 释放写锁
        }
    }
}