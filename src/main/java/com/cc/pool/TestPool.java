package com.cc.pool;

import cn.hutool.core.thread.NamedThreadFactory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chencheng0816@gmail.com
 * @date 2024/11/21 15:46
 * @Description TestPool
 */
public class TestPool {

	@Test
	public void testPriorityBlockingQueue() throws InterruptedException {
		long start = System.currentTimeMillis();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,
				new PriorityBlockingQueue(), new NamedThreadFactory("commonPoolExcutor", false), new ThreadPoolExecutor.CallerRunsPolicy());  // PriorityBlockingQueue, LinkedBlockingQueue

		int beforeNormalTask = 10;
		int prioritizedTask = 5;
		int afterNormalTask = 10;
		int totalTask = beforeNormalTask + prioritizedTask + afterNormalTask;
		final CountDownLatch countDownLatch = new CountDownLatch(totalTask);

		while (beforeNormalTask > 0) {
			final int finalI = beforeNormalTask;
			threadPoolExecutor.execute(new BaseTask1(2) {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					System.out.println(Thread.currentThread() + "Executing prioritized task with priority: beforeNormalTask"+ this.getPriority() + "===========" + finalI + "===========" + this.getTimestamp());
					countDownLatch.countDown();
				}
			});
			beforeNormalTask--;
			Thread.sleep(1);  // 保证每个任务的timestamp时间戳不一样
		}

		while (prioritizedTask > 0) {
			final int finalI = prioritizedTask;
			threadPoolExecutor.execute(new BaseTask1(1) {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					System.out.println(Thread.currentThread() + "Executing prioritized task with priority: prioritizedTask"+ this.getPriority() + "===========" + finalI + "===========" + this.getTimestamp());
					countDownLatch.countDown();
				}
			});
			prioritizedTask--;
			Thread.sleep(1);
		}

		while (afterNormalTask > 0) {
			final int finalI = afterNormalTask;
			threadPoolExecutor.execute(new BaseTask1(2) {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					System.out.println(Thread.currentThread() + "Executing prioritized task with priority: afterNormalTask"+ this.getPriority() + "===========" + finalI + "===========" + this.getTimestamp());
					countDownLatch.countDown();
				}
			});
			afterNormalTask--;
			Thread.sleep(1);
		}

		countDownLatch.await();

		threadPoolExecutor.shutdown();

		System.out.println(System.currentTimeMillis() - start);
	}

	@Test
	public void testCustomBlockingQueue() throws InterruptedException {
		long start = System.currentTimeMillis();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,
				new CustomBlockingQueue(), new NamedThreadFactory("commonPoolExcutor", false), new ThreadPoolExecutor.CallerRunsPolicy());  // PriorityBlockingQueue, LinkedBlockingQueue

		int beforeNormalTask = 5;
		int prioritizedTask = 2;
		int afterNormalTask = 5;
		int totalTask = beforeNormalTask + prioritizedTask + afterNormalTask;
		final CountDownLatch countDownLatch = new CountDownLatch(totalTask);

		while (beforeNormalTask > 0) {
			final int finalI = beforeNormalTask;
			threadPoolExecutor.execute(new BaseTask2(2) {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					System.out.println(Thread.currentThread() + "Executing prioritized task with priority: beforeNormalTask"+ this.getPriority() + "===========" + finalI);
					countDownLatch.countDown();
				}
			});
			beforeNormalTask--;
		}

		while (prioritizedTask > 0) {
			final int finalI = prioritizedTask;
			threadPoolExecutor.execute(new BaseTask2(1) {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					System.out.println(Thread.currentThread() + "Executing prioritized task with priority: prioritizedTask"+ this.getPriority() + "===========" + finalI);
					countDownLatch.countDown();
				}
			});
			prioritizedTask--;
		}

		while (afterNormalTask > 0) {
			final int finalI = afterNormalTask;
			threadPoolExecutor.execute(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				System.out.println(Thread.currentThread() + "Executing prioritized task with priority: afterNormalTask" + "===========" + finalI);
				countDownLatch.countDown();
			});
			afterNormalTask--;
		}

		countDownLatch.await();

		threadPoolExecutor.shutdown();

		System.out.println(System.currentTimeMillis() - start);
	}
}
