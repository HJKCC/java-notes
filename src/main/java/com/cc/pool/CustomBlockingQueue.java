package com.cc.pool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chencheng0816@gmail.com
 * @date 2024/11/27 9:44
 * @Description CustomBlockingQueue
 */
public class CustomBlockingQueue<T> extends LinkedBlockingQueue<T> {
	private final LinkedBlockingQueue<T> normalQueue = new LinkedBlockingQueue<>();
	private final LinkedBlockingQueue<T> priorityQueue = new LinkedBlockingQueue<>();

	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();

	// 添加任务
	@Override
	public boolean offer(T task) {
		boolean isEmpty = normalQueue.isEmpty() && priorityQueue.isEmpty();

		boolean offerFlag = false;
		Class<T> tClass = (Class<T>) task.getClass();
		try {
			Method method = tClass.getMethod("getPriority");
			if ((int)method.invoke(task) == 1) {
				offerFlag = priorityQueue.offer(task); // 放入高优先级队列
			} else {
				offerFlag = normalQueue.offer(task); // 默认放入普通队列
			}
		} catch (Exception e) {   // 捕获NoSuchMethodException、方法执行错误等异常
			offerFlag = normalQueue.offer(task); // 默认放入普通队列
		}

		if (isEmpty) {
			lock.lock();
			condition.signalAll();  // 唤醒等待队列
			lock.unlock();
		}

		return offerFlag;
	}

	// 获取任务
	@Override
	public T take() throws InterruptedException {
		lock.lock();
		try {
			while (normalQueue.isEmpty() && priorityQueue.isEmpty()) {
				condition.await(); // 等待直到有元素
				System.out.println("condition.await()" + System.currentTimeMillis());
			}
		} catch (InterruptedException e) {
			System.out.println("线程池在等待新任务过程中线程被中断：" + "condition.await() ");
		} finally {
			lock.unlock();
		}

		T task = priorityQueue.poll(); // 尝试从高优先级队列获取任务
		if (task == null) {
			task = normalQueue.poll(); // 如果没有高优先级任务，则从普通队列获取
		}

		return task;
	}
}
