package com.example.shand.herbarium;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class LeafData {
    private static LeafData leafData;
    private MatOfPoint contour;
    private Mat grayMat;
    private Mat rgbaMat;

    private Mat morphology;

    private LeafData(){
        contour = null;
        grayMat = null;
        rgbaMat = null;
    }

    public static LeafData getLeafData(){
        if(leafData == null){
            leafData = new LeafData();
        }
        return leafData;
    }

    public boolean find(Mat mat, float x, float y, Mat rgba){
        Mat gray1 = mat.clone(), rgba1 = rgba.clone();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.GaussianBlur(mat, mat, new Size(15, 15), 0 );//, 0.3 * (3 / 2 - 1) + 0.8, 0.0, 4);
        Imgproc.threshold(mat, mat, 160, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Imgproc.Canny(mat, mat, 80, 100);
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        int imax = -1, imax1 = -1;
        double areamax = -1, areamax1 = -1;

        MatOfPoint contoursMat;

        for (int contourIdx = 0; contourIdx < contours.size() && contours != null && hierarchy != null; contourIdx++) {
            contoursMat = contours.get(contourIdx);
            if(contoursMat == null) continue;
            MatOfPoint2f input = new MatOfPoint2f(contoursMat.toArray());

            if ( Imgproc.contourArea(contoursMat) > areamax
                    && Imgproc.pointPolygonTest(input, new Point(x, y), false) > 0
                    && hierarchy.get(contourIdx, 2) != null
                    && hierarchy.get(contourIdx, 2)[0] != -1) {
                imax = contourIdx;
                areamax = Imgproc.contourArea(contoursMat);
            }

            if ( Imgproc.contourArea(contoursMat) > areamax1) {
                imax1 = contourIdx;
                areamax1 = Imgproc.contourArea(contoursMat);
            }



        }

        if(imax1 != -1){
            Imgproc.drawContours(rgba, contours, imax1, new Scalar(0, 255, 255), 8);
        }

        if(imax != -1){

            contour = contours.get(imax);
            rgbaMat = rgba1;
            grayMat = gray1;

            return true;
        }
        else {
            return false;
        }
    }

    public MatOfPoint getContour(){
        return contour;
    }

    public Mat getGray(){
        return grayMat;
    }

    public Mat getRgba(){
        return rgbaMat;
    }

    public Bitmap getMorphology(){
        Bitmap bitmap = Bitmap.createBitmap(morphology.width(), morphology.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(morphology, bitmap);
        return bitmap;
    }

    public void setMorphology(Mat m){
        morphology = m;
    }
}
