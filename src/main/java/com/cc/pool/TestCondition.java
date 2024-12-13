package com.cc.pool;

import cn.hutool.core.thread.NamedThreadFactory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chencheng0816@gmail.com
 * @date 2024/11/29 14:38
 * @Description TestCondition
 */
public class TestCondition {
	ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(), new NamedThreadFactory("commonPoolExcutor", false), new ThreadPoolExecutor.CallerRunsPolicy());


	private static LinkedBlockingQueue<BaseTask2> normalQueue = new LinkedBlockingQueue<>();
	private static LinkedBlockingQueue<BaseTask2> priorityQueue = new LinkedBlockingQueue<>();
	Lock lock = new ReentrantLock();
	Condition condition = lock.newCondition();
	Boolean waitFlag = false;


	@Test
	public void testMain() {
		CountDownLatch countDownLatch = new CountDownLatch(3);

		threadPoolExecutor.execute(() -> {
			this.testCondition();
			countDownLatch.countDown();
		});
		threadPoolExecutor.execute(() -> {
			this.addPriorityQueue();
			countDownLatch.countDown();
		});
		threadPoolExecutor.execute(() -> {
			this.addNormalQueue();
			countDownLatch.countDown();
		});
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testCondition() {
		while (true) {
			lock.lock();
			try {
				if (normalQueue.isEmpty() && priorityQueue.isEmpty()) {
					condition.await(); // 等待直到有元素
					System.out.println("condition.await()" + System.currentTimeMillis());
				}
				// 处理队列中的元素
				BaseTask2 task = priorityQueue.poll();
				if (task == null) {
					task = normalQueue.poll(); // 如果没有高优先级任务，则从普通队列获取
				}
				task.run();
			} catch (InterruptedException e) {
				e.printStackTrace();

			} finally {
				lock.unlock();
			}
		}
	}

	@Test
	public void addNormalQueue() {

		normalQueue.offer(new BaseTask2(0) {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				System.out.println(Thread.currentThread() + "Executing prioritized task with priority: NormalTask"+ this.getPriority());
			}
		});
		System.out.println("addNormalQueue");

		lock.lock();
		try {
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	@Test
	public void addPriorityQueue() {
		priorityQueue.offer(new BaseTask2(1) {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				System.out.println(Thread.currentThread() + "Executing prioritized task with priority: PriorityTask"+ this.getPriority());
			}
		});
		System.out.println("addPriorityQueue");

		lock.lock();
		try {
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}


}
