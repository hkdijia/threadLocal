package com.gotkx.threadlocal.case3;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author HuangKai
 * @date 2022/5/15 10:57
 */
public class CallableAndFutureMulti {
    public static void main(String[] args) {
        //定义一个缓存线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();
        //将线程池扔进去
        CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(threadPool);
        for (int i = 1; i <= 5; i++) {
            final int seq = i;
            //用里面装的线程去执行这些任务，每个线程都会返回一个数据
            completionService.submit( new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            Thread.sleep(new Random().nextInt(5000));
                            return seq;
                        }
                    }
            );
        }

        //执行完了后，再取出来
        for (int i = 0; i < 5; i++) {
            try {
                System.out.print(completionService.take().get() + " ");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
