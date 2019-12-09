package com.haowen.videoCapturer.worker;

import java.util.Map;

public interface CommonWorker {

	void load(Map<String,Object> configs);
	void init();
	void startWork(); 
	void dataCallback(Object[] data);

}
