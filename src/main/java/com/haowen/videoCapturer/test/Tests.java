package com.haowen.videoCapturer.test;

import java.util.HashMap;
import java.util.Map;

import com.haowen.videoCapturer.consumer.VideoShowingConsumer;
import com.haowen.videoCapturer.worker.FFMPEGWorker;
import com.haowen.videoCapturer.worker.FrameIntervalCachingWorker;

public class Tests {
	public static void main(String[] args) {
		FrameIntervalCachingWorker cacheIntervalFrameWorker = new FrameIntervalCachingWorker();
		VideoShowingConsumer imsc = new VideoShowingConsumer();
		imsc.setFrameProvider(cacheIntervalFrameWorker);

		FFMPEGWorker ffmpegWorker = new FFMPEGWorker();
		ffmpegWorker.setDataCallBackListener(cacheIntervalFrameWorker);
		Map<String, Object> configs = new HashMap<>();
		configs.put("path", "rtsp://admin:2284424q@192.168.1.68:5103/h265/ch1/main/av_stream");

		ffmpegWorker.load(configs);
		ffmpegWorker.init();

		ffmpegWorker.startWork();
		cacheIntervalFrameWorker.startWork();
		imsc.startShow();

	}
}
