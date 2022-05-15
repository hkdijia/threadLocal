package com.gotkx.threadlocal.extract;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 提交请求，获取响应
 *
 * @author HuangKai
 * @date 2022/5/15 17:34
 */
@Slf4j
public class WorkSubmitTask<E> {
    /**
     * 业务方法
     */
    private WorkService<E> workService;

    /**
     * 线程池服务
     */
    private ExecutorService executorService;

    /**
     * job集
     */
    private List<FutureTask<ExecResultSet<List<E>>>> futureJobList;

    /**
     * 日志标记
     */
    private String logMark = "";

    /**
     * 构造方法
     * @param workService
     * @param concurrNum
     * @param logMark
     */
    public WorkSubmitTask(WorkService<E> workService,int concurrNum, String logMark) {
        this.workService = workService;
        this.executorService = Executors.newFixedThreadPool(concurrNum);;
        this.futureJobList = new ArrayList<>();;
        this.logMark = logMark;
    }

    /**
     * 提交job
     *
     * @param e
     */
    public void sumbitJob(E e) {
        // 重新组装查询条件  防止并发修改
        E newE = workService.clone(e);
        // 提交工作线程
        FutureTask<ExecResultSet<List<E>>> futureJob = new FutureTask<ExecResultSet<List<E>>>(executeExp(newE));
        executorService.submit(futureJob);
        futureJobList.add(futureJob);
    }

    /**
     * @param e
     * @return
     */
    private Callable<ExecResultSet<List<E>>> executeExp(final E e) {
        return new Callable<ExecResultSet<List<E>>>() {
            @Override
            public ExecResultSet<List<E>> call() {

                ExecResultSet<List<E>> execResultSet = new ExecResultSet<List<E>>();
                List<E> payOrderlist = null;
                try {
                    payOrderlist = workService.query(e, logMark);
                } catch (Exception e) {
                    log.error(logMark + "-查询订单数据：执行数据库查询出错，原因：", e);
                    execResultSet.setErrorFlag(false);
                    execResultSet.setErrorStr(e.getMessage());
                    return execResultSet;
                }
                // 执行成功
                execResultSet.setV(payOrderlist);
                execResultSet.setErrorFlag(true);
                execResultSet.setErrorStr("success");
                return execResultSet;
            }
        };
    }


    /**
     * 拉取结果
     *
     * @param paySuccessOrders
     */
    public void takeResults(List<E> paySuccessOrders) {
        if (paySuccessOrders == null) {
            return;
        }
        // 获取结果
        FutureTask<ExecResultSet<List<E>>> futureTask = null;
        ExecResultSet<List<E>> execResultSet = null;
        try {
            do {
                // 遍历
                Iterator<FutureTask<ExecResultSet<List<E>>>> it = futureJobList.iterator();
                while (it.hasNext()) {
                    futureTask = it.next();
                    try {
                        execResultSet = futureTask.get();
                    } catch (Exception e) {
                        log.error(logMark + "-获取[订单查询任务]结果出错，原因：", e);
                        throw new IllegalArgumentException(logMark + "-执行数据库查询订单出错，原因：线程出错");
                    }

                    if (execResultSet == null) {
                        throw new IllegalArgumentException(logMark + "-执行数据库查询订单出错，获取结果为空");
                    }

                    // 执行出错 返回处理
                    if (!execResultSet.errorFlag) {
                        throw new IllegalArgumentException(String.format("%s-执行数据库查询订单出错，原因：%s", logMark, execResultSet.errorStr));
                    }

                    // 执行成功
                    if (execResultSet.getV() != null) {
                        paySuccessOrders.addAll(execResultSet.getV());
                    }
                    it.remove();
                }

            } while (futureJobList.size() != 0);
        } catch (Exception e) {
            // 强制中断
            executorService.shutdownNow();
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            // 关闭线程池
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
        }
    }
}
