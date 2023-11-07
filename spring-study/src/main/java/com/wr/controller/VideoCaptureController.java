package com.wr.controller;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;

/**
 * @author : WangRui
 * @date : 2023/11/3
 */

@RestController
public class VideoCaptureController {

//    static {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.load("/opt/homebrew/Cellar/opencv/4.8.1_2/share/java/opencv4/libopencv_java470.dylib");
//    }

    @RequestMapping("/startRecording")
    public String startRecording(){

        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()){
            return null;
        }
        CanvasFrame canvas = new CanvasFrame("实时监控", CanvasFrame.getDefaultGamma());
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Mat frame = new Mat();
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        while (true) {
            camera.read(frame);
            if (!frame.empty()) {
                Frame convertedFrame = converter.convert(frame);
                canvas.showImage(convertedFrame);
            } else {
                System.out.println("无法获取帧");
                break;
            }
        }

        // 释放资源
        camera.release();
        canvas.dispose();

        return "ok";

    }
}
