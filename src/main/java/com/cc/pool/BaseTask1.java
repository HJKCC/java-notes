package com.cc.pool;
/**
* @author chencheng0816@gmail.com 
* @date 2024/11/27 16:11
* @Description pool
*/
public abstract class BaseTask1 implements Runnable, Comparable<BaseTask1> {
	private int priority;
	private long timestamp;

	public BaseTask1(int priority) {
		this.priority = priority;
		this.timestamp = System.currentTimeMillis();
	}

	public int getPriority() {
		return priority;
	}

	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return  < 0 时，先执行；
	 *          = 0 时，优先级相同；
	 *          > 0 时，优先级较低；
	 */
	@Override
	public int compareTo(BaseTask1 other) {
		int res = Integer.compare(this.priority, other.priority);

		if (res == 0) {
			res = Long.compare(this.timestamp, other.timestamp);
		}
		return res;
	}
}
