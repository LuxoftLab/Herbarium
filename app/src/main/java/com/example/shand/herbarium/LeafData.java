package com.example.shand.herbarium;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.Objdetect;

import java.util.ArrayList;

public class LeafData {
    private static LeafData leafData;
    private MatOfPoint contour;
    private Mat grayMat;
    private Mat rgbaMat;

    private Mat morphology;

    private LeafData() {
        contour = null;
        grayMat = null;
        rgbaMat = null;
    }

    public static LeafData getLeafData() {
        if (leafData == null) {
            leafData = new LeafData();
        }
        return leafData;
    }

    public boolean find(Mat mat, float x, float y, Mat rgba) {
        Mat gray1 = mat.clone(), rgba1 = rgba.clone();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.GaussianBlur(mat, mat, new Size(25, 25), 0);
        Imgproc.threshold(mat, mat, 160, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Imgproc.Canny(mat, mat, 80, 100);
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        int imax = -1, imax1 = -1;
        double areamax = -1, areamax1 = -1;

        MatOfPoint contoursMat;

        for (int contourIdx = 0; contourIdx < contours.size() && contours != null && hierarchy != null; contourIdx++) {
            contoursMat = contours.get(contourIdx);
            if (contoursMat == null) continue;
           // MatOfPoint2f input = new MatOfPoint2f(contoursMat.toArray());

            /*
            if (Imgproc.contourArea(contoursMat) > areamax
                    && Imgproc.pointPolygonTest(input, new Point(x, y), false) > 0
                    && hierarchy.get(contourIdx, 2) != null
                    && hierarchy.get(contourIdx, 2)[0] != -1) {
                imax = contourIdx;
                areamax = Imgproc.contourArea(contoursMat);
            }*/

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
