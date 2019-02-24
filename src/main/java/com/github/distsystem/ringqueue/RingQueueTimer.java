package com.github.distsystem.ringqueue;

import java.util.Iterator;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @function 环形队列定时任务
 * @date 2019年2月24日 上午9:27:20
 * @author 李桥
 * @version 1.0
 */
public class RingQueueTimer extends TimerTask {

	private RingQueue ringQueue;
	private ExecutorService executorService = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	private Timer timer = new Timer();

	public RingQueueTimer() {
		this(null);
	}

	public RingQueueTimer(RingQueue ringQueue) {
		this.ringQueue = ringQueue;
	}

	@Override
	public void run() {
		if (ringQueue == null)
			return;

		Queue<RingQueueTask> curPointerSlotQueue = ringQueue.getCurPointerSlotQueue();
		synchronized (curPointerSlotQueue) {
			Iterator<RingQueueTask> iterator = curPointerSlotQueue.iterator();
			while (iterator.hasNext()) {
				RingQueueTask ringQueueTask = iterator.next();
				if (ringQueueTask.isTimeOut()) {
					iterator.remove();
					executorService.execute(ringQueueTask.getRunnableTask());
				} else {
					ringQueueTask.countDown();
				}
			}

		}

		ringQueue.tick();
	}

	public void addTask(Runnable runnableTask, int delaySeconds) {
		ringQueue.addTask(runnableTask, delaySeconds);
	}

	public RingQueue getRingQueue() {
		return ringQueue;
	}

	public void setRingQueue(RingQueue ringQueue) {
		this.ringQueue = ringQueue;
	}

	public void start() {
		timer.scheduleAtFixedRate(this, 0, ringQueue.getTickIntervalSeconds() * 1000L);
	}
}