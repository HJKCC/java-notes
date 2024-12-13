package com.cc.pool;
/**
* @author chencheng0816@gmail.com 
* @date 2024/11/27 16:11
* @Description pool
*/
public abstract class BaseTask2 implements Runnable {
	private int priority;

	public BaseTask2(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
}
