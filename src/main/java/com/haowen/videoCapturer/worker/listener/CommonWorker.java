package com.haowen.videoCapturer.worker.listener;

import java.util.Map;

public interface CommonWorker {

	void load(Map<String, Object> configs);

	void init();

	void startWork();

	void setDataCallBackListener(DataReceivedListener listener);

}
