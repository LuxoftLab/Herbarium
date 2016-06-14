package com.example.shand.herbarium;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class LeafData {
    private static LeafData leafData;

    private MatOfPoint contour;
    private Mat grayMat;
    private Mat rgbaMat;

    private Point basePoint;
    private ArrayList<MatOfPoint> leavesContoursWithoutScape;

    private LeafData() {
        contour = null;
        grayMat = null;
        rgbaMat = null;
    }

    public void setGrayMat(Mat m){
        grayMat = m;
    }

    public void setRgbaMat(Mat m){
        rgbaMat = m;
    }

    public void setContour(MatOfPoint m){
        contour = m;
    }

    public static LeafData getLeafData() {
        if (leafData == null) {
            leafData = new LeafData();
        }
        return leafData;
    }

    public Point getBasePoint(){
        return basePoint;
    }

    public boolean find(Mat mat, float x, float y, Mat rgba) {
        Mat gray1 = mat.clone(), rgba1 = rgba.clone();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.GaussianBlur(mat, mat, new Size(19, 19), 0);
        Imgproc.threshold(mat, mat, 160, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Imgproc.Canny(mat, mat, 80, 100);
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        int imax = -1, imax1 = -1;
        double areamax = -1, areamax1 = -1;

        MatOfPoint contoursMat;

        for (int contourIdx = 0; contourIdx < contours.size() && contours != null && hierarchy != null; contourIdx++) {
            contoursMat = contours.get(contourIdx);
            if (contoursMat == null) continue;

            if (Imgproc.contourArea(contoursMat) > areamax1) {
                imax1 = contourIdx;
                areamax1 = Imgproc.contourArea(contoursMat);
            }
        }

        if (imax1 != -1) {
            Imgproc.drawContours(rgba, contours, imax1, new Scalar(0, 255, 255), 2);

            contoursMat = contours.get(imax1);
            if (contoursMat != null) {
                MatOfPoint2f input = new MatOfPoint2f(contoursMat.toArray());

                if (Imgproc.pointPolygonTest(input, new Point(x, y), false) <= 0) {
                    return false;
                }

                contour = contours.get(imax1);
                rgbaMat = rgba1;
                grayMat = gray1;

                return true;
            }
        }

        return false;
    }

    public boolean deleteScape(){
        final int width = 30;
        final double minScapeArea = 314;

        MatOfPoint mat;

        if(contour == null){
            return false;
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
        if(mat == null) return false;
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
        if(mat == null) return false;
        scapeContours.add(mat);

        Mat scape = new Mat(mask.rows(), mask.cols(), mask.type(), new Scalar(0));

        if(Imgproc.contourArea(scapeContours.get(0)) < minScapeArea){
            basePoint = null;
            return false;
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
        if(mat == null) return false;
        contours1.add(mat);

        RotatedRect base = Imgproc.fitEllipse(new MatOfPoint2f(contours1.get(0).toArray()));
        basePoint = base.center.clone();

        Imgproc.circle(rgbaMat, basePoint, 20, new Scalar(255, 0, 0));
        mat = findLargestContour(maskWithScape);
        if(mat == null) {
            return false;
        }
        contour = mat;
        return true;
    }

    public ArrayList<MatOfPoint> getLeavesContoursWithoutScape(){
        return leavesContoursWithoutScape;
    }

    public MatOfPoint findLargestContour(Mat mat){
        ArrayList<MatOfPoint> allContours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat.clone(), allContours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        MatOfPoint contoursMat;
        double maxArea = -1;
        int maxi = -1;
        for (int i = 0; i < allContours.size(); i++) {
            contoursMat = allContours.get(i);
            if (allContours == null) continue;

            if(Imgproc.contourArea(contoursMat) > maxArea) {
                maxi = i;
                maxArea = Imgproc.contourArea(contoursMat);
            }
        }

        hierarchy.release();
        if(maxi == -1) return null;

        return allContours.get(maxi);
    }

    public MatOfPoint getContour() {
        return contour;
    }

    public Mat getGray() {
        return grayMat;
    }

    public Mat getRgba() {
        return rgbaMat;
    }
}