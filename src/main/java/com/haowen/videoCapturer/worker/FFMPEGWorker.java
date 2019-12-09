package com.haowen.videoCapturer.worker;

import java.util.Date;
import java.util.Map;

import javax.swing.JFrame;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class FFMPEGWorker implements CommonWorker {
	static int width = 800;
	static int height = 600;

	static String HW_CODE = "h264_qsv";
	static String HWACCEL = "qsv";

	private String path="";
	private int frameCount=25;


	private Integer framesPerTwoSecond;
	
	@Override
	public void load(Map<String, Object> configs) {
		this.path = (String) configs.get("path");
		this.frameCount= (int) configs.get("frameCount");
	}

	@Override
	public void init() {

	}

	@Override
	public void startWork() {
		try {
			FFmpegFrameGrabber mFrameGrabber = getFFmpegFrameGrabber(path);
			// 上面的代码表示我们可以像ijkplayer一样，设置一些参数，这些参数格式我们可以参考ijkplayer也可以去ffmpeg命令行的一些设置参数文档里面去查找，这里就不多赘述了。
			avutil.av_log_set_level(avutil.AV_LOG_ERROR);
			// int avformat_open_input(AVFormatContext **ps, const char
			// *url, AVInputFormat *fmt, AVDictionary **options);
			mFrameGrabber.start();
			CanvasFrame canvas = new CanvasFrame("摄像头");// 新建一个窗口
			canvas.setBounds(0, 0, width, height);
			canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			canvas.setAlwaysOnTop(false);

			while (true) {
				if (!canvas.isDisplayable()) {// 窗口是否关闭
					mFrameGrabber.stop();// 停止抓取
					System.exit(2);// 退出
					break;
				}
				Frame frame = mFrameGrabber.grab();
				while ((frame == null)) {
					System.err.println("stop....");
					mFrameGrabber.close();
					mFrameGrabber = getFFmpegFrameGrabber(path);
					mFrameGrabber.start();
					frame = mFrameGrabber.grab();
					// 重连
				}
				canvas.showImage(frame);// 获取摄像头图像并放到窗口上显示， 这里的Frame
										// frame=grabber.grab();
										// frame是一帧视频图像
				System.out.println(Thread.currentThread().getName() + "-" + new Date(frame.timestamp));

				// Thread.sleep(800);//50毫秒刷新一次图像
			}
			// Mat mat =
			// converter.convertToMat(mFrameGrabber.grabFrame());
			// opencv_imgcodecs.imwrite("d:\\img\\" +
			// System.currentTimeMillis() + ".png", mat);

		} catch (Exception e) {

		}

	}

	@Override
	public void dataCallback(Object[] data) {
		// TODO Auto-generated method stub

	}

	private static FFmpegFrameGrabber getFFmpegFrameGrabber(String path) throws Exception {
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
}
