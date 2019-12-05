package com.haowen;

import org.bytedeco.javacpp.Loader;

import java.io.IOException;
 
public class JavaCVProcess {
 
    public static void main(String[] args) throws IOException, InterruptedException {
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        String from = "rtsp://*************";
        String to = "rtmp://********************";
        ProcessBuilder pb = new ProcessBuilder(ffmpeg,
            "-i", from, "-codec", "copy", "-f", "flv", "-y", to);
        Process process = pb.inheritIO().start();
        process.waitFor();
    }
}
//		mFrameGrabber.setVideoCodecName(HW_CODE);// h264_qsv
