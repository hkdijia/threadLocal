package com.gotkx.threadlocal.case5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 有三个线程，子线程1先执行10次，然后子线程2执行10次，然后主线程执行5次，然后再切换到子线程1执行10次，子线程2执行10次，主线程执行5次……如此往返执行50次。
 */
public class ThreeConditionCommunication {

    public static void main(String[] args) {
        Business bussiness = new Business();

        // 开启一个子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 50; i++) {
                    bussiness.sub1(i);
                }

            }
        }).start();

        // 开启另一个子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 50; i++) {
                    bussiness.sub2(i);
                }

            }
        }).start();

        // main方法主线程
        for (int i = 1; i <= 50; i++) {
            bussiness.main(i);
        }
    }

    static class Business {
        Lock lock = new ReentrantLock();
        Condition condition1 = lock.newCondition(); //Condition是在具体的lock之上的
        Condition condition2 = lock.newCondition();
        Condition conditionMain = lock.newCondition();

        private int bShouldSub = 0;

        public void sub1(int i) {
            lock.lock();
            try {
                while (bShouldSub != 0) {
                    try {
                        condition1.await(); //用condition来调用await方法
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for (int j = 1; j <= 10; j++) {
                    System.out.println("sub1 thread sequence of " + j + ", loop of " + i);
                }
                bShouldSub = 1;
                condition2.signal(); //让线程2执行
            } finally {
                lock.unlock();
            }
        }

        public void sub2(int i) {
            lock.lock();
            try {
                while (bShouldSub != 1) {
                    try {
                        condition2.await(); //用condition来调用await方法
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for (int j = 1; j <= 10; j++) {
                    System.out.println("sub2 thread sequence of " + j + ", loop of " + i);
                }
                bShouldSub = 2;
                conditionMain.signal(); //让主线程执行
            } finally {
                lock.unlock();
            }
        }

        public void main(int i) {
            lock.lock();
            try {
                while (bShouldSub != 2) {
                    try {
                        conditionMain.await(); //用condition来调用await方法
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for (int j = 1; j <= 5; j++) {
                    System.out.println("main thread sequence of " + j + ", loop of " + i);
                }
                bShouldSub = 0;
                condition1.signal(); //让线程1执行
            } finally {
                lock.unlock();
            }
        }
    }
}
