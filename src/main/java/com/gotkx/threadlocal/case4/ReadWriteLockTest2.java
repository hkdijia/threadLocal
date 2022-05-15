package com.gotkx.threadlocal.case4;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest2 {
    public static void main(String[] args) {
        CacheData cache = new CacheData();

        //开启5个线程
        for (int i = 1; i <= 5; i++) {
            new Thread(new Runnable() {
                @Override
                //都去拿数据
                public void run() {
                    cache.processCache();
                }
            }).start();
        }
    }
}

class CacheData {
    /**
     * 需要缓存的数据
     */
    private Object data = null;
    /**
     * 用来标记是否有缓存数据
     */
    private boolean cacheValid;
    /**
     * 定义读写锁
     */
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    public void processCache() {
        rwl.readLock().lock(); //上读锁

        //如果没有缓存，那说明是第一次访问，需要给data赋个值
        if (!cacheValid) {
			//先把读锁释放掉
            rwl.readLock().unlock();
			//上写锁
            rwl.writeLock().lock();
            if (!cacheValid) {
                System.out.println(Thread.currentThread().getName() + ": no cache!");
                //赋值
                data = new Random().nextInt(1000);
                //标记已经有缓存了
                cacheValid = true;
                System.out.println(Thread.currentThread().getName() + ": already cached!");
            }
            rwl.readLock().lock(); //再把读锁上上
            rwl.writeLock().unlock(); //把刚刚上的写锁释放掉
        }
        System.out.println(Thread.currentThread().getName() + " get data: " + data);
        rwl.readLock().unlock(); //释放读锁
    }
}
