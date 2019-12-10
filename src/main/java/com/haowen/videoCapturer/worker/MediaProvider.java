package com.haowen.videoCapturer.worker;

import org.bytedeco.javacv.Frame;

public interface MediaProvider {

	Frame getVideoFrame();

	int getVideoFps();

}
