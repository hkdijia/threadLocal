package com.gotkx.threadlocal.case7;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierTest {

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        //设置要三个线程等待，都执行完了再往下执行
        final CyclicBarrier cb = new CyclicBarrier(3);
        System.out.println("初始化：当前有" + (cb.getNumberWaiting() + "个线程在等待"));

        //3个任务
        for (int i = 0; i < 3; i++) {

            Runnable run = new Runnable() {
                public void run() {
                    try {
                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println(Thread.currentThread().getName()
                                + "即将到达集合点1，当前已有" + (cb.getNumberWaiting() + 1) + "个线程到达，"
                                + (cb.getNumberWaiting() == 2 ? "都到齐了，去集合点2！" : "正在等候……"));
                        // 访问完后，释放 ，如果屏蔽下面的语句，则在控制台只能打印3条记录，之后线程一直阻塞
                        cb.await(); //等待 

                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println(Thread.currentThread().getName()
                                + "即将到达集合点2，当前已有" + (cb.getNumberWaiting() + 1) + "个线程到达，"
                                + (cb.getNumberWaiting() == 2 ? "都到齐了，去集合点3！" : "正在等候……"));
                        cb.await();

                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println(Thread.currentThread().getName()
                                + "即将到达集合点3，当前已有" + (cb.getNumberWaiting() + 1) + "个线程到达，"
                                + (cb.getNumberWaiting() == 2 ? "都到齐了，执行完毕！" : "正在等候……"));
                        cb.await();

                    } catch (Exception e) {
                    }
                }
            };
            //执行任务
            service.execute(run);
        }
        //关闭线程
        service.shutdown();
    }
}
