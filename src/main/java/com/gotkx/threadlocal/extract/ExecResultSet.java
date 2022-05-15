package com.gotkx.threadlocal.extract;

/**
 * 线程执行结果集
 * @author HuangKai
 * @date 2022/5/15 17:36
 */
public class ExecResultSet<V> {
    public V v;

    public boolean errorFlag;

    public String errorStr;

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    public boolean isErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }

    public String getErrorStr() {
        return errorStr;
    }

    public void setErrorStr(String errorStr) {
        this.errorStr = errorStr;
    }
}

