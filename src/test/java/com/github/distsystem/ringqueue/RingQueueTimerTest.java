package com.github.distsystem.ringqueue;

import java.io.IOException;

/**
 * RingQueueTimer测试用例
 */
public class RingQueueTimerTest {

	public static void main(String[] args) throws IOException {
		RingQueue ringQueue = new RingQueue(3600, 1);
		RingQueueTimer ringQueueTimer = new RingQueueTimer(ringQueue);
		ringQueueTimer.addTask(new Runnable() {
			@Override
			public void run() {
				System.out.println(DateUtils.now());
			}
		}, 10);

		System.out.println(DateUtils.now());
		ringQueueTimer.start();

		System.in.read();
	}

}