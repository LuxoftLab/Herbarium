package com.example.shand.herbarium;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

public class LeafDetector {
    private CascadeClassifier cascadeClassifier;
    private MatOfRect objects;

    public LeafDetector(String cascadeFileName) {
        cascadeClassifier = new CascadeClassifier();
        cascadeClassifier.load(cascadeFileName);
        objects = new MatOfRect();
    }

    public Rect[] detect(Mat mat) {
          int size = (int) (mat.height()*0.19);
        cascadeClassifier.detectMultiScale(mat, objects, 1.3, 6, new Size(size, size), new Size());
        return objects.toArray();
    }
}
