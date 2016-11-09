package com.example.shand.herbarium.classification;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class LeafData {

    private MatOfPoint contour;
    private Mat grayMat;
    private Mat rgbaMat;

    private Point basePoint;

    private boolean scapeDeleted = false;

    public LeafData() {
        contour = null;
        grayMat = null;
        rgbaMat = null;
    }

    public void setMat(Mat m) {
        rgbaMat = m;
        grayMat = new Mat(rgbaMat.rows(), rgbaMat.cols(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGBA2GRAY);
    }

    public Point getBasePoint() {
        return basePoint;
    }

    private void findContour() {
        Mat gray = grayMat.clone();
        Imgproc.threshold(gray, gray, 160, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        contour = findLargestContour(gray);
    }

    //delete scape from gray mat and contour and find base point
    //if leaf has no scape, no changes are saved in gray mat and contour
    public void deleteScape() {
        final int width = 30;
        final double minScapeArea = 314;

        //if scape is already deleted, method is not executed
        if(scapeDeleted) {
            return;
        }
        scapeDeleted = true;

        long startTime = System.currentTimeMillis();

        MatOfPoint mat;

        Log.d("Scape", "Time: " + (System.currentTimeMillis() - startTime));

        if (contour == null) {
            findContour();

            if(contour == null) {
                return;
            }
        }
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);

        int idx = 0;

        //draw mask
        Mat mask = new Mat(grayMat.rows(), grayMat.cols(), grayMat.type(), new Scalar(0));
        Imgproc.drawContours(mask, contours, idx, new Scalar(255), -1);

        Mat maskWithScape = mask.clone();

        //delete background from grayscale image
        Core.bitwise_and(grayMat, mask, grayMat);

        //delete scape from mask
        Imgproc.drawContours(mask, contours, idx, new Scalar(0), width);

        //find contour without scape
        ArrayList<MatOfPoint> contourWithoutScape = new ArrayList<>();
        mat = findLargestContour(mask.clone());

        Log.d("Scape", "Time: " + (System.currentTimeMillis() - startTime));

        if (mat == null) return;
        contourWithoutScape.add(mat);

        //draw leaf mask without scape
        Imgproc.drawContours(mask, contourWithoutScape, 0, new Scalar(255), -1);
        Imgproc.drawContours(mask, contourWithoutScape, 0, new Scalar(255), width + 10);
        Mat maskWithoutScape = mask.clone();

        //find scape contours
        Core.subtract(maskWithScape, mask, mask);

        //find scape
        ArrayList<MatOfPoint> scapeContours = new ArrayList<>();
        mat = findLargestContour(mask.clone());
        if (mat == null) return;
        scapeContours.add(mat);

        Mat scape = new Mat(mask.rows(), mask.cols(), mask.type(), new Scalar(0));

        Log.d("Scape", "Time: " + (System.currentTimeMillis() - startTime));

        if (Imgproc.contourArea(scapeContours.get(0)) < minScapeArea) {
            basePoint = null;
            return;
        }

        //find point of intersection scape and leaf
        Imgproc.drawContours(scape, scapeContours, 0, new Scalar(255), -1);
        Imgproc.drawContours(scape, scapeContours, 0, new Scalar(255), width + 60);
        Core.bitwise_and(scape, maskWithoutScape, maskWithoutScape);

        //delete scape from grayscale image
        Core.subtract(grayMat, scape, grayMat);
        Core.subtract(maskWithScape, scape, maskWithScape);

        //find base
        ArrayList<MatOfPoint> contours1 = new ArrayList<>();
        mat = findLargestContour(maskWithoutScape.clone());

        Log.d("Scape", "Time: " + (System.currentTimeMillis() - startTime));

        if (mat == null) return;
        contours1.add(mat);

        RotatedRect base = Imgproc.fitEllipse(new MatOfPoint2f(contours1.get(0).toArray()));
        basePoint = base.center.clone();

        mat = findLargestContour(maskWithScape);

        Log.d("Scape", "Time: " + (System.currentTimeMillis() - startTime));

        if (mat == null) {
            return;
        }
        contour = mat;
        return;
    }

    public static MatOfPoint findLargestContour(Mat mat) {
        ArrayList<MatOfPoint> allContours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat.clone(), allContours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        MatOfPoint contoursMat;
        double maxArea = -1;
        int maxi = -1;
        for (int i = 0; i < allContours.size(); i++) {
            contoursMat = allContours.get(i);
            if (allContours == null) continue;

            if (Imgproc.contourArea(contoursMat) > maxArea) {
                maxi = i;
                maxArea = Imgproc.contourArea(contoursMat);
            }
        }

        hierarchy.release();
        if (maxi == -1) {
            return null;
        }

        return allContours.get(maxi);
    }

    public static ArrayList<MatOfPoint> findLargestContourArrayList(Mat mat) {
        ArrayList<MatOfPoint> result = new ArrayList<>();
        MatOfPoint largestContour = findLargestContour(mat);
        if(largestContour != null) {
            result.add(largestContour);
        }

        return result;
    }

    public static Mat thresholdOTSU(Mat gray) {
        Imgproc.threshold(gray, gray, 160, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        return gray;
    }

    public MatOfPoint getContour() {
        if(contour == null) {
            findContour();
        }
        return contour;
    }

    public Mat getGray() {
        return grayMat;
    }

    public Mat getRgba() {
        return rgbaMat;
    }
}