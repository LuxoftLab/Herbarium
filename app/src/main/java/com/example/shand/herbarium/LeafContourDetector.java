package com.example.shand.herbarium;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

public class LeafContourDetector {
    private CascadeClassifier cascadeClassifier;
    private MatOfRect objects;

    public LeafContourDetector(String cascadeFileName) {
        cascadeClassifier = new CascadeClassifier();
        cascadeClassifier.load(cascadeFileName);
        objects = new MatOfRect();
    }

    public Rect[] detect(Mat mat) {
        int size = (int) (mat.height()*0.15);
        cascadeClassifier.detectMultiScale(mat, objects, 1.2, 4, new Size(size, size), new Size());
        return objects.toArray();
    }
}
