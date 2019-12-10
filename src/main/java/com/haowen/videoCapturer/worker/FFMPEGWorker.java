package com.haowen.videoCapturer.worker;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import com.haowen.videoCapturer.worker.listener.CommonWorker;
import com.haowen.videoCapturer.worker.listener.ConnectionEventListener;
import com.haowen.videoCapturer.worker.listener.DataReceivedListener;

public class FFMPEGWorker implements CommonWorker, Runnable {

	static int count = 0;
	int width = 800;
	int height = 600;

	String HW_CODE = "h264_qsv";
	String HWACCEL = "qsv";

	private String path = "";
	private int frameCount = 25;

	private Integer fps;
	private final Queue<DataReceivedListener> listners = new ConcurrentLinkedQueue<>();
	private final Queue<ConnectionEventListener> connectionlistners = new ConcurrentLinkedQueue<>();

	private boolean working = false;

	private Thread workingThread = null;

	@Override
	public void setDataCallBackListener(DataReceivedListener listener) {
		if (listener == null) {
			return;
		}
		for (DataReceivedListener dataCallBackListener : listners) {
			if (dataCallBackListener.equals(listener)) {
				return;
			}

		}
		listners.add(listener);
	}

	@Override
	public void load(Map<String, Object> configs) {
		this.path = (String) configs.get("path");
	}

	@Override
	public void init() {
		workingThread = new Thread(this);
		synchronized (FFMPEGWorker.class) {
			count++;
		}
		workingThread.setName("FFMPEGWorker-Thread-" + count);
	}

	@Override
	public void startWork() {
		synchronized (this) {
			if (!working) {
				workingThread.start();
			}
		}
	}

	private void getData(Frame frame) {
		for (DataReceivedListener dataReceivedListener : listners) {
			try {
				dataReceivedListener.dataAccquired(frame);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private FFmpegFrameGrabber getFFmpegFrameGrabber(String path) throws Exception {
		FFmpegFrameGrabber mFrameGrabber = FFmpegFrameGrabber.createDefault(path);
		// mFrameGrabber.setPixelFormat(avutil.AV_PIX_FMT_YUVA422P);
		mFrameGrabber.setOption("rtsp_transport", "tcp");
		mFrameGrabber.setOption("max_delay", "500000");

		mFrameGrabber.setOption("hwaccel", HWACCEL);
		// mFrameGrabber.setOption("hwaccel_device", "0");
		mFrameGrabber.setOption("c:v", HW_CODE);
		mFrameGrabber.setOption("stimeout", "20000000");// 设置超时断开连接时间 20s
		mFrameGrabber.setOption("buffer_size", "1024000");// 提高画质，减少花屏现象
		mFrameGrabber.setVideoOption("preset", "ultrafast");
		mFrameGrabber.setVideoOption("hwaccel", HWACCEL);
		mFrameGrabber.setImageWidth(800);
		mFrameGrabber.setImageHeight(600);
		mFrameGrabber.setVideoCodecName(HW_CODE);// h264_qsv
		return mFrameGrabber;

	}

	@Override
	public void run() {
		try {
			FFmpegFrameGrabber mFrameGrabber = getFFmpegFrameGrabber(path);
			// 上面的代码表示我们可以像ijkplayer一样，设置一些参数，这些参数格式我们可以参考ijkplayer也可以去ffmpeg命令行的一些设置参数文档里面去查找，这里就不多赘述了。
			avutil.av_log_set_level(avutil.AV_LOG_ERROR);
			// int avformat_open_input(AVFormatContext **ps, const char
			// *url, AVInputFormat *fmt, AVDictionary **options);
			mFrameGrabber.start();
			// CanvasFrame canvas = new CanvasFrame("摄像头");// 新建一个窗口
			// canvas.setBounds(0, 0, width, height);
			// canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// canvas.setAlwaysOnTop(false);

			while (true) {
				// if (!canvas.isDisplayable()) {// 窗口是否关闭
				// mFrameGrabber.stop();// 停止抓取
				// System.exit(2);// 退出
				// break;
				// }
				Frame frame = mFrameGrabber.grab();
				while ((frame == null)) {
					System.err.println("stop....");
					mFrameGrabber.close();
					mFrameGrabber = getFFmpegFrameGrabber(path);
					mFrameGrabber.start();
					frame = mFrameGrabber.grab();
					// 重连
				}
				// canvas.showImage(frame);// 获取摄像头图像并放到窗口上显示， 这里的Frame
				// frame=grabber.grab();
				// frame是一帧视频图像
				getData(frame);

			}
			// Mat mat =
			// converter.convertToMat(mFrameGrabber.grabFrame());
			// opencv_imgcodecs.imwrite("d:\\img\\" +
			// System.currentTimeMillis() + ".png", mat);

		} catch (Exception e) {

		}
	}
}
