package com.example.shand.herbarium;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class ComplicatedLeafDetector {
    public CompoundLeafType detect(Mat rgba, Mat gray){
        long startTime;
        LeafData leafData = LeafData.getLeafData();

        Imgproc.threshold(gray, gray, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        startTime = System.currentTimeMillis();
        Mat mat = gray.clone();
        Imgproc.resize(mat, mat, new Size(rgba.width()/5, rgba.height()/5));

        //delete scape from resized image
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5), new Point(-1, -1)); // kernel performing drode
        Imgproc.erode(mat, mat, element);
        Imgproc.dilate(mat, mat, element);

        //delete scape from initial image
        Imgproc.resize(mat, mat, new Size(rgba.width(), rgba.height()));
        Core.bitwise_and(gray, mat, gray);

        //find leaves
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Log.d("Time", "time: " + (System.currentTimeMillis() - startTime));
        Imgproc.drawContours(leafData.getRgba(), contours, -1, new Scalar(255, 0, 0), 2);

        //detect leaf type
        switch (contours.size()) {
            case 0:
                Imgproc.putText(leafData.getRgba(), "Leaf is not found", new Point(0, 90), 0, 2, new Scalar(255, 255, 255), 3);
                return CompoundLeafType.UNKNOWN;
            case 1:
                Imgproc.putText(leafData.getRgba(), "Simple", new Point(0, 90), 0, 2, new Scalar(255, 255, 255), 3);
                return CompoundLeafType.SIMPLE;
            default:
                Imgproc.putText(leafData.getRgba(), "Compound", new Point(0, 90), 0, 2, new Scalar(255, 255, 255), 3);
                return CompoundLeafType.COMPOUND;
        }
    }
}

enum CompoundLeafType {
    COMPOUND, SIMPLE, UNKNOWN
}