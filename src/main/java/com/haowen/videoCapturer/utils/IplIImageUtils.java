package com.haowen.videoCapturer.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;

public class IplIImageUtils {

	public static BufferedImage frameToImage(Frame image) {
		Java2DFrameConverter converter = new Java2DFrameConverter();
		return converter.getBufferedImage(image, (Java2DFrameConverter.getBufferedImageType(image) == 0) ? 1.0D : 0d,
				false, null);
	}

	/**
	 * 
	 * 功能说明:将javacv的IplImage图像转为java 2d自身的BufferedImage
	 * 
	 * @param iplImage
	 *            javacv图像
	 * @return BufferedImage java 2d图像
	 * @time:2016年8月3日下午12:03:05
	 * @author:linghushaoxia
	 * @exception:
	 *
	 */
	@Deprecated
	public static BufferedImage iplToBufferedImage(IplImage iplImage) {
		if (iplImage.height() > 0 && iplImage.width() > 0) {
			BytePointer imageData = iplImage.imageData();
			ByteBuffer byteBuffer = imageData.asByteBuffer();
			byteBuffer.clear();
			ByteArrayInputStream in = new ByteArrayInputStream(byteBuffer.array());
			BufferedImage image = null;
			try {
				image = ImageIO.read(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return image;
		}
		return null;
	}

	/**
	 * 
	 * 功能说明:将javacv的IplImage图像转为java 2d自身的BufferedImage
	 * 
	 * @param iplImage
	 *            javacv图像
	 * @return BufferedImage java 2d图像
	 * @time:2016年8月3日下午12:24:50
	 * @author:linghushaoxia
	 * @exception:
	 *
	 */
	@Deprecated

	public static BufferedImage iplToBufImgData(IplImage mat) {
		if (mat.height() > 0 && mat.width() > 0) {
			BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
			WritableRaster raster = image.getRaster();
			DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
			byte[] data = dataBuffer.getData();
			BytePointer bytePointer = new BytePointer(data);
			mat.imageData(bytePointer);
			return image;
		}
		return null;
	}
}
