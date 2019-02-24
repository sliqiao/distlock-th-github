package com.github.distsystem.ringqueue;

/**
 * @function 环形队列任务
 * @date 2019年2月24日 上午9:27:20
 * @author 李桥
 * @version 1.0
 */
class RingQueueTask {
	private int cycleNum;
	private int delaySeconds;
	private Runnable runnableTask;

	public RingQueueTask() {
	}

	public RingQueueTask(int cycleNum, int delaySeconds, Runnable runnableTask) {
		this.cycleNum = cycleNum;
		this.delaySeconds = delaySeconds;
		this.runnableTask = runnableTask;
	}

	public boolean isTimeOut() {
		return cycleNum <= 0;
	}

	public void countDown() {
		cycleNum--;
	}

	public int getCycleNum() {
		return cycleNum;
	}

	public void setCycleNum(int cycleNum) {
		this.cycleNum = cycleNum;
	}

	public int getDelaySeconds() {

		return delaySeconds;
	}

	public void setDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
	}

	public Runnable getRunnableTask() {
		return runnableTask;
	}

	public void setRunnableTask(Runnable runnableTask) {
		this.runnableTask = runnableTask;
	}

	@Override
	public String toString() {
		return "RingQueueTask [cycleNum=" + cycleNum + ", delaySeconds=" + delaySeconds + ", runnableTask="
				+ runnableTask + "]";
	}

}