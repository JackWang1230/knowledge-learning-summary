package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FaceRecognition {
    public static void main(String[] args) {
        String trainingDir = "training"; // 训练集目录
        List<String> names = new ArrayList<>(); // 人物名称
        List<Mat> images = new ArrayList<>(); // 人脸图像
        Mat testImage = null; // 待识别图像

        // 加载训练集
        File root = new File(trainingDir);
        FilenameFilter imgFilter = (dir, name) -> name.endsWith(".jpg");
        File[] imageFiles = root.listFiles(imgFilter);

        // 读取图像并将它们存储为Mat对象
        for (File imageFile : imageFiles) {
            Mat image = opencv_imgcodecs.imread(imageFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
            String name = imageFile.getName().split("\\.")[0];
            names.add(name);
            images.add(image);
        }

        // 将人脸图像转换为一维向量
        MatVector imagesVector = new MatVector(images.size());
        for (int i = 0; i < images.size(); i++) {
            imagesVector.put(i, images.get(i).reshape(1, 1));
        }

        // 训练Eigenfaces识别器
        FaceRecognizer recognizer = EigenFaceRecognizer.create();

        // recognizer.train(imagesVector);

        // 识别人脸
        Scanner input = new Scanner(System.in);
        System.out.println("请输入待识别图像的文件路径：");
        String imagePath = input.nextLine();
        testImage = opencv_imgcodecs.imread(imagePath, opencv_imgcodecs.IMREAD_GRAYSCALE);

        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        recognizer.predict(testImage.reshape(1, 1), label, confidence);

        System.out.println("识别结果：");
        System.out.println("标签：" + names.get(label.get()));
        System.out.println("置信度：" + confidence.get());
    }
}

