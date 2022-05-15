package com.gotkx.threadlocal.case5;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer {

    public static void main(String[] args) {

        Buffer buffer = new Buffer();

		/**
		 * 开启5个线程往缓冲区存数据
		 */
		for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
						/**
						 * 随机存数据
						 */
						buffer.put(new Random().nextInt(1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

		/**
		 * 开启10个线程从缓冲区中取数据
		 */
		for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
						/**
						 * 从缓冲区取数据
						 */
						buffer.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}


class Buffer {
    final Lock lock = new ReentrantLock();

	/**
	 * 定义一个锁
	 */
	final Condition notFull = lock.newCondition();

	/**
	 * 定义阻塞队列满了的Condition
	 */
	final Condition notEmpty = lock.newCondition();

	/**
	 * 为了下面模拟，设置阻塞队列的大小为10，不要设太大
	 */
	final Object[] items = new Object[10];

	/**
	 *
	 */
	int putptr;

	/**
	 * 为了下面模拟，设置阻塞队列的大小为10，不要设太大
	 */
	int takeptr;

	/**
	 * 数组下标，用来标定位置的
	 */
	int count;

	/**
	 * 往队列中存数据
	 * @param x
	 * @throws InterruptedException
	 */
	public void put(Object x) throws InterruptedException {
        lock.lock();
        //上锁
        try {
            while (count == items.length) {
                System.out.println(Thread.currentThread().getName() + " 被阻塞了，暂时无法存数据！");
				//如果队列满了，那么阻塞存数据这个线程，等待被唤醒
                notFull.await();
            }

            //如果没满，按顺序往数组中存
            items[putptr] = x;
            if (++putptr == items.length) {
                //这是到达数组末端的判断，如果到了，再回到始端
                putptr = 0;
            }
            ++count;
            //消息数量
            System.out.println(Thread.currentThread().getName() + " 存好了值： " + x);
            notEmpty.signal();
            //好了，现在队列中有数据了，唤醒队列空的那个线程，可以取数据啦
        } finally {
			//放锁
            lock.unlock();
        }
    }

	/**
	 * 从队列中取数据
	 * @return
	 * @throws InterruptedException
	 */
	public Object take() throws InterruptedException {
        lock.lock();
        //上锁
        try {
            while (count == 0) {
                System.out.println(Thread.currentThread().getName() + " 被阻塞了，暂时无法取数据！");
				//如果队列是空，那么阻塞取数据这个线程，等待被唤醒
                notEmpty.await();
            }
            //如果没空，按顺序从数组中取
            Object x = items[takeptr];
            if (++takeptr == items.length) {
                //判断是否到达末端，如果到了，再回到始端
                takeptr = 0;
            }
            --count;
            //消息数量
            System.out.println(Thread.currentThread().getName() + " 取出了值： " + x);
            notFull.signal();
            //好了，现在队列中有位置了，唤醒队列满的那个线程，可以存数据啦
            return x;
        } finally {
			//放锁
			lock.unlock();
        }
    }
}
