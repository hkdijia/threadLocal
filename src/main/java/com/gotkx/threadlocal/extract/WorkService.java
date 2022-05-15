package com.gotkx.threadlocal.extract;

import java.util.List;

/**
 * 并发查询 接口定义
 * @author HuangKai
 * @param <E>
 */
public interface WorkService<E> {
	
	/**
	 * 执行查询操作
	 * 
	 * @param e
	 * @param args
	 * @return
	 */
	public List<E> query(E e, Object... args);
	
	/**
	 * 克隆查询条件实体
	 * 
	 * @param e
	 * @return
	 */
	public E clone(E e);
}
