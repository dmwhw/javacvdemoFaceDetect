package com.haowen.videoCapturer.worker;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.bytedeco.javacv.Frame;

import com.haowen.videoCapturer.utils.ThreadUtils;
import com.haowen.videoCapturer.worker.listener.DataReceivedListener;

public class FrameIntervalCachingWorker implements MediaProvider, Runnable, DataReceivedListener {

	private Thread workThread = null;

	private Integer requireFps = 25;
	private Integer captureInterval = 1000 / requireFps;

	/**
	 * 需要进行跳过的大小。
	 */
	private Integer needSkipCacheSize = 10;
	/**
	 * 一次跳过的数量
	 */
	private Integer skipCount = 6;

	/**
	 * cache中最多允许的数量
	 */
	private Integer cleanCacheSize = 50;

	private final ConcurrentLinkedQueue<Frame> cache = new ConcurrentLinkedQueue<>();

	private volatile Frame currentFrame;

	@Override
	public void run() {
		while (true) {
			ThreadUtils.sleep(captureInterval);// 顺便转换？
			if (currentFrame != null) {
				cache.add(currentFrame);
				System.out.println("add data...");
				int size = cache.size();
				if (size > cleanCacheSize) {// 触发清理
					cache.clear();
				}
			}
		}
	}

	@Override
	public void dataAccquired(Object data) {
		// this.currentFrame = (Frame) data;
		// System.out.println("getdata....");
		Frame f = (Frame) data;
		this.currentFrame = f.clone();

		// if (f != null && f.image != null) {
		// cache.add(f.clone());
		// }

	}

	public void startWork() {
		workThread = new Thread(this);
		workThread.start();
	}

	@Override
	public Frame getVideoFrame() {
		// 如果发现cache过大，超过needSkipCacheSize，跳skipCount个，防止延后
		int size = cache.size();
		System.out.println("poll data...now cache size:" + size);
		if (size > needSkipCacheSize) {
			for (int i = 0; i < skipCount; i++) {
				cache.poll();
			}
			System.out.println("skip done...");
		}
		return cache.poll();

	}

	public Integer getRequireFps() {
		return requireFps;
	}

	@Override
	public int getVideoFps() {
		return requireFps;
	}

}
