package com.github.distsystem.ringqueue;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @function 环形队列
 * @date 2019年2月24日 上午9:27:20
 * @author 李桥
 * @version 1.0
 */
public class RingQueue {
	/** 默认的环形队列上插槽的数量3600个，也就是说如果指针每1s走一格，需要走3600秒，才能走完一圈 */
	private static final int DEFAULT_RING_QUEUE_SLOTS_NUM = 3600;
	/** 默认的环形队列上插槽指针每走一格时间间隔：秒 */
	private static final int DEFAULT_TICK_INTERVAL_SECONDS = 1;
	private Map<Integer, Queue<RingQueueTask>> ringQueueMap;
	private volatile int currentPointerSlotIndex = 0;
	private int ringQueueSlotsNum = DEFAULT_RING_QUEUE_SLOTS_NUM;
	private int tickIntervalSeconds = DEFAULT_TICK_INTERVAL_SECONDS;

	public RingQueue() {
		init();
	}

	public RingQueue(int ringQueueSlotsNum, int tickIntervalSeconds) {
		if (ringQueueSlotsNum <= 0)
			throw new IllegalArgumentException("ringQueueSlotsNum must be greater than 0");

		if (tickIntervalSeconds <= 0)
			throw new IllegalArgumentException("tickIntervalSeconds must be greater than 0");

		this.ringQueueSlotsNum = ringQueueSlotsNum;
		this.tickIntervalSeconds = tickIntervalSeconds;
		init();
	}

	private void init() {
		ringQueueMap = new ConcurrentHashMap<Integer, Queue<RingQueueTask>>();
		for (int i = 0; i < ringQueueSlotsNum; i++)
			ringQueueMap.put(i, new ConcurrentLinkedQueue<RingQueueTask>());
	}

	public void addTask(Runnable runnableTask, int delaySeconds) {
		if (runnableTask == null)
			throw new NullPointerException("runnableTask must not be null");

		if (delaySeconds <= 0)
			throw new IllegalArgumentException("delaySeconds must be greater than 0");

		int totalSeconds = ringQueueSlotsNum * tickIntervalSeconds;
		int cycleNum = delaySeconds / totalSeconds;
		int slotIndex = ((delaySeconds % totalSeconds) / tickIntervalSeconds) + currentPointerSlotIndex;
		RingQueueTask ringQueueTask = new RingQueueTask(cycleNum, delaySeconds, runnableTask);
		Queue<RingQueueTask> slotQueue = ringQueueMap.get(slotIndex);
		synchronized (slotQueue) {
			slotQueue.add(ringQueueTask);
		}
	}

	public int tick() {
		currentPointerSlotIndex = (currentPointerSlotIndex + 1) % ringQueueSlotsNum;
		return currentPointerSlotIndex;
	}

	/**
	 * 获取当前指针所对应的插槽上的任务队列
	 */
	public Queue<RingQueueTask> getCurPointerSlotQueue() {
		return ringQueueMap.get(currentPointerSlotIndex);
	}

	public int getCurrentPointerSlotIndex() {
		return currentPointerSlotIndex;
	}

	public int getRingQueueSlotsNum() {
		return ringQueueSlotsNum;
	}

	public int getTickIntervalSeconds() {
		return tickIntervalSeconds;
	}

	@Override
	public String toString() {
		return "RingQueue [ringQueueMap=" + ringQueueMap + ", currentPointerSlotIndex=" + currentPointerSlotIndex
				+ ", ringQueueSlotsNum=" + ringQueueSlotsNum + ", tickIntervalSeconds=" + tickIntervalSeconds + "]";
	}

}