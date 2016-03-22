package com.example.shand.herbarium;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class LeafClassifier {
    private double k, threshold;

    public LeafClassifier(double k1, double t){
        k = k1;
        threshold = t;
    }

    public boolean hasSmoothEdges(MatOfPoint contour){
        MatOfPoint2f input = new MatOfPoint2f(contour.toArray());
        MatOfPoint2f output = new MatOfPoint2f();
        Imgproc.approxPolyDP(input, output, Imgproc.arcLength(input, false) * k, false);

        double k = (Imgproc.arcLength(output, true) / Imgproc.arcLength(input, true));
        return k > threshold;
    }

    public boolean hasSmoothEdges(MatOfPoint contour, Mat mRgba){
        MatOfPoint2f input = new MatOfPoint2f(contour.toArray());
        MatOfPoint2f output = new MatOfPoint2f();
        Imgproc.approxPolyDP(input, output, Imgproc.arcLength(input, false) * k, false);

        MatOfPoint res = new MatOfPoint(output.toArray());

        ArrayList<MatOfPoint> arrayListM = new ArrayList<>();
        arrayListM.add(res);

        Imgproc.drawContours(mRgba, arrayListM, -1, new Scalar(0, 0, 255));

        double k = (Imgproc.arcLength(output, true) / Imgproc.arcLength(input, true));
        return k > threshold;
    }
}
