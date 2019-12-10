package com.haowen.videoCapturer.worker.listener;

import java.util.Map;

public interface ConnectionEventListener {

	void preConnected(Map<String, Object> extras);

	void connected(Long connectTime, Map<String, Object> extras);

	boolean connectLost(Long disconnectTime, Map<String, Object> extras);

}
