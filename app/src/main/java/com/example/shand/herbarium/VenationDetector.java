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

public class VenationDetector {

    public Mat detect(Mat gray, Mat rgba){
        ArrayList<MatOfPoint> contours = findContours(gray.clone());
        int index = findOuterContour(contours);

        morphologyProcessing(gray);

        removeOuterContour(gray, contours, index);

        imageEnhancement(gray);
        binarization(gray);

        findLines(gray, rgba);

        return rgba;
    }

    private Mat morphologyProcessing(Mat src){
        Mat closing = new Mat(src.rows(), src.cols(), src.type());
        //closing operation
        Imgproc.dilate(src, closing, new Mat(), new Point(-1, -1), 2, 1, new Scalar(1));
        Imgproc.erode(closing, closing, new Mat());
        Core.subtract(closing, src, src);
        return src;
    }

    private ArrayList<MatOfPoint> findContours(Mat mat){
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.GaussianBlur(mat, mat, new Size(5, 5), 0.3 * (3 / 2 - 1) + 0.8, 0.0, 4);
        Imgproc.threshold(mat, mat, 160, 255, Imgproc.THRESH_BINARY_INV);

        Imgproc.Canny(mat, mat, 80, 100);
         Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        hierarchy.release();

        return contours;
    }

    private int findOuterContour(ArrayList<MatOfPoint> contours){
        int imax = -1;
        double areamax = -1;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            if (Imgproc.contourArea(contours.get(contourIdx)) > areamax) {
                imax = contourIdx;
                areamax = Imgproc.contourArea(contours.get(contourIdx));
            }
        }
        return imax;
    }

    private Mat removeOuterContour(Mat mat, ArrayList<MatOfPoint> contours, int imax){
        if(imax != -1) {
            Mat mask = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));
            Imgproc.drawContours(mask, contours, imax, new Scalar(255), -1);

            Imgproc.drawContours(mat, contours, imax, new Scalar(0), 20);
            Log.d(getClass().getName(), "outer contour was removed");

            Core.bitwise_and(mat, mask, mat);
        }else {
            Log.d(getClass().getName(), "outer contour wasn't removed");
        }

        return mat;
    }

    private Mat imageEnhancement(Mat mat){
        Imgproc.equalizeHist(mat, mat);
        return mat;
    }

    private Mat binarization(Mat mat){
        Imgproc.threshold(mat, mat, 215, 255, Imgproc.THRESH_BINARY);
        return mat;
    }

    private void findLines(Mat mat, Mat dst){
        Mat lines = new Mat();
        Imgproc.HoughLinesP(mat, lines, 1, Math.PI / 180, 60, 70, 10);

        int index = -1;
        double max_res = -1;

        for (int x = 0; x < lines.cols(); x++) {
            double[] vec = lines.get(0, x);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            double res = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));

            if (res > max_res){
                max_res = res;
                index = x;
            }

            Imgproc.line(dst, start, end, new Scalar(255,0,0), 8);

        }

        if (lines.cols() == 0){
            return;
        }

        int x = index;
        double[] vec = lines.get(0, x);
        double x1 = vec[0],
                y1 = vec[1],
                x2 = vec[2],
                y2 = vec[3];
        Point start = new Point(x1, y1);
        Point end = new Point(x2, y2);

        //draw main vein
        Imgproc.line(dst, start, end, new Scalar(255, 0, 255), 8);
    }
}
