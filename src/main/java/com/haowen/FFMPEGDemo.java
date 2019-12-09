package com.haowen;

import java.util.Date;

import javax.swing.JFrame;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class FFMPEGDemo {
	// rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov
	// rtsp://admin:84424q@192.168.1.68:5100/h265/ch1/main/av_stream
	static String path = "rtsp://admin:2284424q@192.168.1.68:5100/h265/ch1/main/av_stream";
	static String path2 = "rtsp://admin:2284424q@192.168.1.68:5101/h265/ch1/main/av_stream";
	static String path3 = "rtsp://admin:2284424q@192.168.1.68:5102/h265/ch1/main/av_stream";
	static String path4 = "rtsp://admin:2284424q@192.168.1.68:5104/h265/ch1/main/av_stream";
	static String path5 = "rtsp://admin:2284424q@192.168.1.68:5103/h265/ch1/main/av_stream";

	// static OpenCVFrameConverter.ToIplImage converter = new
	// OpenCVFrameConverter.ToIplImage();
	// ffmpeg -hwaccel qsv -c:v h264_qsv -i 1.MP4 -c:v h264_qsv -y 00.mp4
	// .\ffmpeg.exe -rtsp_transport tcp
	// rtsp://admin:84424q@192.168.1.68:5100/MPEG-4/ch1/main/av_stream
	// ffplay -hwaccel qsv tcp -c:v h264_qsv -rtsp_transport tcp
	// rtsp://admin:84424q@192.168.1.68:5100/MPEG-4/ch1/main/av_stream

	// BufferedImage bi = (new Java2DFrameConverter()).getBufferedImage(f);
	static int width = 200;
	static int height = 150;

	static String HW_CODE = "h264_qsv";
	// static String HW_CODE="hevc_qsv";
	static String HWACCEL = "qsv";

	public static FFmpegFrameGrabber getFFmpegFrameGrabber(String path) throws Exception {
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
		// mFrameGrabber.start();
		return mFrameGrabber;

	}
	// avcodec.AV_CODEC_ID_H264

	public static void main(String[] args) throws Exception, InterruptedException {

		openPath(path);
		openPath(path2);
		openPath(path3);
		openPath(path4);
		openPath(path5);

	}

	public static void openPath(final String path) throws Exception, InterruptedException {
		new Thread() {
			public void run() {
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
						System.out.println(Thread.currentThread().getName()+"-"+new Date( frame.timestamp));

						// Thread.sleep(800);//50毫秒刷新一次图像
					}
					// Mat mat =
					// converter.convertToMat(mFrameGrabber.grabFrame());
					// opencv_imgcodecs.imwrite("d:\\img\\" +
					// System.currentTimeMillis() + ".png", mat);

				} catch (Exception e) {

				}
			};
		}.start();

	}

	// DEV.LS h264 H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10 (decoders: h264
	// h264_qsv libopenh264 h264_cuvid ) (encoders: libx264 libx264rgb
	// libopenh264 h264_nvenc h264_qsv nvenc nvenc_h264 )
	// DEV.L. hevc H.265 / HEVC (High Efficiency Video Coding) (decoders: hevc
	// hevc_qsv hevc_cuvid ) (encoders: libx265 nvenc_hevc hevc_nvenc hevc_qsv )
	// DEVIL. mjpeg Motion JPEG (decoders: mjpeg mjpeg_cuvid ) (encoders: mjpeg
	// mjpeg_qsv )
	// DEV.L. mpeg2video MPEG-2 video (decoders: mpeg2video mpegvideo mpeg2_qsv
	// mpeg2_cuvid ) (encoders: mpeg2video mpeg2_qsv )
	// D.V.L. vc1 SMPTE VC-1 (decoders: vc1 vc1_qsv vc1_cuvid )
	// DEV.L. vp8 On2 VP8 (decoders: vp8 libvpx vp8_cuvid vp8_qsv ) (encoders:
	// libvpx )
}
