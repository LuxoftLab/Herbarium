package com.example.shand.herbarium.detector;

import android.util.Log;


import com.example.shand.herbarium.classification.Features;
import com.example.shand.herbarium.classification.LeafData;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

//detect whether leaf edges are smooth or ribbed
public class ContourDetector extends DebugDetector implements Detector {
    private static int idx;
    private Detector next;

    private final double k = 0.01, threshold = 0.9;

    public ContourDetector(Detector next){
        this.next = next;
    }

    public ContourDetector(){}

    @Override
    public void setIdx(int i) {
        idx = i;
    }

    @Override
    public int getIdx() {
        return idx;
    }

    @Override
    public Detector detect(LeafData leafData, Features features){
        startTimer();
        leafData.deleteScape();

        Mat rgba = leafData.getRgba().clone();
        Mat gray = leafData.getGray().clone();

        addDebugMat(gray.clone());

        MatOfPoint contour = leafData.getContour();
        if(contour == null) {
            features.setFeature(idx, ContourType.UNKNOWN.ordinal());
            return next;
        }

        MatOfPoint2f input = new MatOfPoint2f(contour.toArray());
        MatOfPoint2f output = new MatOfPoint2f();
        Imgproc.approxPolyDP(input, output, Imgproc.arcLength(input, false) * k, false);

        double k = (Imgproc.arcLength(output, true) / Imgproc.arcLength(input, true));
        if (k > threshold) {
            features.setFeature(idx, ContourType.SMOOTH.ordinal());
        } else {
            features.setFeature(idx, ContourType.RIBBED.ordinal());
        }

        stopTimer();

        try {
            Thread.sleep(2000);
        }catch (Exception e) {

        }

        if(debug) {
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            contours.add(contour);
            Imgproc.drawContours(rgba, contours, -1, new Scalar(255, 0, 0));

            MatOfPoint res = new MatOfPoint(output.toArray());
            ArrayList<MatOfPoint> arrayListM = new ArrayList<>();
            arrayListM.add(res);
            Imgproc.drawContours(rgba, arrayListM, -1, new Scalar(0, 255, 0));

            String text = "RESULT: " + ContourType.values()[features.getFeature(idx)];

            addDebugText(text);
            addDebugMat(rgba.clone());
        }

        Log.d("Classification", "RESULT: " + ContourType.values()[features.getFeature(idx)]);

        return next;
    }

    @Override
    public String toString() {
        return "Shape";
    }

    @Override
    public String getFeatureName(int i) {
        return ContourType.values()[i].toString();
    }

    private enum ContourType {
        UNKNOWN, SMOOTH, RIBBED
    }
}