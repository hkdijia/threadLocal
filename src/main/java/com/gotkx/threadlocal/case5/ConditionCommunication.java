package com.gotkx.threadlocal.case5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有两个线程，子线程先执行10次，然后主线程执行10次，然后再切换到子线程执行10，再主线程执行10次……如此往返执行50次。
 */
public class ConditionCommunication {

    public static void main(String[] args) {
        Business bussiness = new Business();

        // 开启一个子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 50; i++) {
                    bussiness.sub(i);
                }

            }
        }).start();

        // main方法主线程
        for (int i = 1; i <= 50; i++) {
            bussiness.main(i);
        }
    }
}

class Business {
    Lock lock = new ReentrantLock();
    /**
     * Condition是在具体的lock之上的
     */
    Condition condition = lock.newCondition();
    private boolean bShouldSub = true;

    public void sub(int i) {
        lock.lock();
        try {
            while (!bShouldSub) {
                try {
                    //用condition来调用await方法
                    condition.await();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            for (int j = 1; j <= 10; j++) {
                System.out.println("sub thread sequence of " + j + ", loop of " + i);
            }
            bShouldSub = false;
            //用condition来发出唤醒信号，唤醒某一个
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void main(int i) {
        lock.lock();
        try {
            while (bShouldSub) {
                try {
                    //用condition来调用await方法
                    condition.await();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            for (int j = 1; j <= 10; j++) {
                System.out.println("main thread sequence of " + j + ", loop of " + i);
            }
            bShouldSub = true;
            //用condition来发出唤醒信号么，唤醒某一个
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
