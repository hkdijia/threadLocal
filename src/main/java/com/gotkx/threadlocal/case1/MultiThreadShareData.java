package com.gotkx.threadlocal.case1;

public class MultiThreadShareData {
    public static void main(String[] args) {
        ShareData task = new ShareData();

        //公共数据和任务放在task中
        for (int i = 0; i < 2; i++) {
            //开启两个线程增加data            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    task.increment();
                }
            }).start();
        }

        for (int i = 0; i < 2; i++) {
            //开启两个线程减少data            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    task.decrement();
                }
            }).start();
        }
    }
}


class ShareData/*implements Runnable*/ {
    private int data = 0;

    public synchronized void increment() {
        //增加data
        System.out.println(Thread.currentThread().getName() + ": before : " + data);
        data++;
        System.out.println(Thread.currentThread().getName() + ": after : " + data);
    }

    public synchronized void decrement() {
        //减少data
        System.out.println(Thread.currentThread().getName() + ": before : " + data);
        data--;
        System.out.println(Thread.currentThread().getName() + ": after : " + data);
    }
}
