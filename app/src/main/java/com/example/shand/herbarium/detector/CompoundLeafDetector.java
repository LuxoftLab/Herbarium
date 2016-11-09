package com.example.shand.herbarium.detector;

import android.util.Log;

import com.example.shand.herbarium.classification.LeafData;
import com.example.shand.herbarium.classification.Features;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

//detect whether leaf is compound or simple
public class CompoundLeafDetector extends DebugDetector implements Detector {
    private static int idx;
    private Detector compoundNextDetector, simpleNextDetector, unknownNextDetector;

    public CompoundLeafDetector(){}

    public CompoundLeafDetector(Detector compound, Detector simple, Detector unknown){
        compoundNextDetector = compound;
        simpleNextDetector = simple;
        unknownNextDetector = unknown;
    }

    @Override
    public void setIdx(int i) {
        idx = i;
    }

    @Override
    public int getIdx() {
        return idx;
    }

    @Override
    public Detector detect(LeafData leafData, Features features) {
        startTimer();

        Mat gray = leafData.getGray().clone();
        Mat rgba = leafData.getRgba().clone();

        //threshold
        gray = LeafData.thresholdOTSU(gray);
        addDebugMat(gray);

        //fill leaf contour
        ArrayList<MatOfPoint> contours = LeafData.findLargestContourArrayList(gray);
        gray = new Mat(gray.rows(), gray.cols(), gray.type(), new Scalar(0));
        Imgproc.drawContours(gray, contours, -1, new Scalar(255), 5);
        Imgproc.drawContours(gray, contours, -1, new Scalar(255), -1);
        addDebugMat(gray);

        //downscale gray mat
        Mat mat = gray;
        Imgproc.resize(mat, mat, new Size(rgba.width() / 5, rgba.height() / 5));

        //delete scape from resized image
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7), new Point(-1, -1));
        Imgproc.erode(mat, mat, element);
        Imgproc.dilate(mat, mat, element);
        addDebugMat(mat);

        //delete scape from initial image
        Imgproc.resize(mat, mat, new Size(rgba.width(), rgba.height()));
        Core.bitwise_and(gray, mat, gray);
        addDebugMat(gray);

        //find leaves
        contours = new ArrayList<>();
        Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        stopTimer();

        if(debug) {
            //show contours on rgba mat
            Imgproc.drawContours(rgba, contours, -1, new Scalar(255, 0, 0), 2);
            addDebugMat(rgba);
        }

        Imgproc.drawContours(leafData.getRgba(), contours, -1, new Scalar(255), 3);

        //classify leaf type
        Detector next;
        switch (contours.size()) {
            case 0:
                features.setFeature(getIdx(), CompoundLeafType.UNKNOWN.ordinal());
                next = unknownNextDetector;
                break;
            case 1:
                features.setFeature(getIdx(), CompoundLeafType.SIMPLE.ordinal());
                next = simpleNextDetector;
                break;
            default:
                features.setFeature(getIdx(), CompoundLeafType.COMPOUND.ordinal());
                next = compoundNextDetector;
        }

        addDebugText("RESULT: " + CompoundLeafType.values()[features.getFeature(idx)]);
        Log.d("Classification", "RESULT: " + CompoundLeafType.values()[features.getFeature(idx)]);

        return next;
    }

    @Override
    public String toString() {
        return "Shape";
    }

    @Override
    public String getFeatureName(int i) {
        return CompoundLeafType.values()[i].toString();
    }

    private enum CompoundLeafType {
        UNKNOWN, SIMPLE, COMPOUND
    }
}