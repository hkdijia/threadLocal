package com.gotkx.threadlocal.case4;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheDemo {
    public static void main(String[] args) {
        Cache cac = new Cache();
		//开启三个线程去缓存中拿key为cache1的数据，
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //第一个进入的线程要先写一个数据进去（相当于第一次从数据库中取）
                    String value = (String) cac.getData("cache1");
                    System.out.println(Thread.currentThread().getName() + ": " + value);
                }
            }).start();
        }

        //开启三个线程去缓存中拿key为cacahe2的数据
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                //第一个进入的线程要先写一个数据进去（相当于第一次从数据库中取）
                @Override
                public void run() {
                    String value = (String) cac.getData("cache2");
                    System.out.println(Thread.currentThread().getName() + ": " + value);
                }
            }).start();
        }
    }
}

class Cache {
    /**
     * 存储缓存数据的Map，注意HashMap是非线程安全的，也要进行同步操作
     */
    private Map<String, Object> cache = Collections.synchronizedMap(new HashMap<>());
    /**
     * 定义读写锁
     */
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    public /*synchronized*/ Object getData(String key) {
        rwl.readLock().lock(); //上读锁
        Object value = null;
        try {
            //根据key从缓存中拿数据
            value = cache.get(key);
            //如果第一次那该key对应的数据，拿不到
            if (value == null) {
                rwl.readLock().unlock(); //释放读锁
                rwl.writeLock().lock(); //换成写锁
                try {
                    //之所以再去判断，是为了防止几个线程同时进入了上面那个if，然后一个个都来重写赋值一遍
                    if (value == null) {
                        System.out.println(Thread.currentThread().getName() + " write cache for " + key);
                        // 实际中是去数据库中取，这里只是模拟
                        value = "aaa" + System.currentTimeMillis();
                        //放到缓存中
                        cache.put(key, value);
                        System.out.println(Thread.currentThread().getName() + " has already written cache!");
                    }
                } finally {
                    rwl.writeLock().unlock(); //写完了释放写锁
                }
                rwl.readLock().lock(); //换读锁
            }
        } finally {
            rwl.readLock().unlock(); //最后呢释放读锁
        }
        //返回要取的数据
        return value;
    }
}
