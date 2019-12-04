package com.haowen;


import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
public class FFMPEGDemo {

	static String path="rtsp://admin:2284424q@192.168.1.68:5100/MPEG-4/ch1/main/av_stream";
    //static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    
    
	static JFrame jFrame=null;
	static int width=200;
	static int height=150;
	public static void main(String[] args) throws Exception, InterruptedException {
 
		FFmpegFrameGrabber mFrameGrabber  = FFmpegFrameGrabber.createDefault(path);
		
		//上面的代码就是创建FFmpegFrameGrabber的方式，path就是要拉取流的地址。

		//mFrameGrabber.setPixelFormat(avutil.AV_PIX_FMT_ARGB);
		//设置帧收集时的像素格式，这块设置AV_PIX_FMT_RGBA的原因主要是，我们展示画面的时候是转换为Bitmap格式的。

		mFrameGrabber.setOption("fflags", "nobuffer");
		mFrameGrabber.setOption("rtsp_transport", "tcp");
		mFrameGrabber.setOption("hwaccel", "cuvid");
		mFrameGrabber.setOption("hwaccel_device", "0");
		mFrameGrabber.setOption("c:v", "h264_cuvid");

		 
		//上面的代码表示我们可以像ijkplayer一样，设置一些参数，这些参数格式我们可以参考ijkplayer也可以去ffmpeg命令行的一些设置参数文档里面去查找，这里就不多赘述了。

		mFrameGrabber.start();
		CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
		canvas.setBounds(0, 0, width, height);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(false);
        new Thread(){
        	public void run() {
        		try {
					Thread.sleep( 5*60*1000);
					System.out.println("exit.....");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		System.exit(0);
        	};
        }.start();
        while(true){
            if(!canvas.isDisplayable()){//窗口是否关闭
            	mFrameGrabber.stop();//停止抓取
                System.exit(2);//退出
                break;
            }
             Frame frame = mFrameGrabber.grab();
            if ((frame==null)){
            	System.out.println("stop....");
            	//重连
            }
            canvas.showImage(frame);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
            Thread.sleep(200);//50毫秒刷新一次图像
        }
      //  Mat mat = converter.convertToMat(mFrameGrabber.grabFrame());
       // opencv_imgcodecs.imwrite("d:\\img\\" + System.currentTimeMillis() + ".png", mat);
        
	}
}
