package com.haowen.videoCapturer.consumer;

import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;

import com.haowen.videoCapturer.utils.ThreadUtils;
import com.haowen.videoCapturer.worker.MediaProvider;

public class VideoShowingConsumer implements Runnable {

	CanvasFrame jframe = new CanvasFrame("ddddd");

	private MediaProvider frameProvider;

	@Override
	public void run() {
		while (true) {
			try {
				if (frameProvider == null) {
					continue;
				}

				Frame frame = frameProvider.getVideoFrame();
				if (frame != null && frame.image != null) {
					// BufferedImage frameToImage =
					// IplIImageUtils.frameToImage(frame);
					long time = System.currentTimeMillis();
					// BufferedImage image =
					// Java2DFrameUtils.toBufferedImage(frame);
					System.err.println("frame not null...!!!!!!!!! " + (System.currentTimeMillis() - time) + "ms");
					// jframe.showImage(image);
					System.out.println("show data...");

				} else {
					System.err.println("frame is null*******");
				}

				ThreadUtils.sleep(1000 / frameProvider.getVideoFps());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void startShow() {
		jframe.setBounds(0, 0, 800, 600);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setAlwaysOnTop(false);
		jframe.setVisible(true);
		new Thread(this).start();

	}

	public void setFrameProvider(MediaProvider frameProvider) {
		this.frameProvider = frameProvider;
	}
}
