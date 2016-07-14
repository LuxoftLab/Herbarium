package com.example.shand.herbarium;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FormSingleton {
    private static FormSingleton formSingleton;
    Mat img;
    Mat hierarchy;
    List<MatOfPoint> contours;
    Mat gray;
    Mat rgba;

    private FormSingleton() {
        /*hierarchy = new Mat();
        contours = new ArrayList<>();
        gray = new Mat();
        rgba = new Mat();*/
        hierarchy = null;
        contours = null;
        gray = null;
        rgba = null;
    }

    public Mat getRGBA() {
        return rgba;
    }

    public Mat getGray() {
        return gray;
    }

    public static FormSingleton getInstance() {
        if(formSingleton == null) {
            formSingleton = new FormSingleton();
        }
        return formSingleton;
    }
    public Mat findContoursAndHull(Mat srcRGBA, Mat srcGray) {
        Mat rgba1 = srcRGBA.clone();
        Mat gray1 = srcGray.clone();
        contours = new ArrayList<>();
        hierarchy = new Mat();
        rgba = new Mat();

        //Imgproc.GaussianBlur(gray1, gray1, new Size(5, 5), 0.3 * (3 / 2 - 1) + 0.8, 0.0, 4);
        Imgproc.threshold(gray1, gray1, 0.0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        //Imgproc.cvtColor(rgba1, gray1, Imgproc.COLOR_RGB2GRAY);

        //Imgproc.Canny(gray1, gray1, 80, 100);

        Imgproc.findContours(gray1, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        int imax = 0;
        double areamax = -1;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            if (Imgproc.contourArea(contours.get(contourIdx)) > areamax) {
                imax = contourIdx;
                areamax = Imgproc.contourArea(contours.get(contourIdx));
            }
        }


        hierarchy.release();

        if (areamax != -1 && contours.size() != 0) {
            Imgproc.drawContours(rgba1, contours, imax, new Scalar(0, 0, 255));
        }

        MatOfPoint2f in = new MatOfPoint2f(contours.get(imax).toArray());
        MatOfPoint2f out = new MatOfPoint2f();

        Imgproc.approxPolyDP(in, out, Imgproc.boundingRect(contours.get(imax)).height * 0.000015, true);

        List<MatOfPoint> outList = new ArrayList<>();
        outList.add(new MatOfPoint(out.toArray()));
        //outList.add(contours.get(imax));
        MatOfInt mHull = new MatOfInt();
        MatOfInt4 matOfInt4 = new MatOfInt4();
        Imgproc.convexHull(contours.get(imax), mHull);
        Imgproc.convexityDefects(contours.get(imax), mHull, matOfInt4);
        MatOfPoint mopOut = hull2Points(mHull, contours.get(imax));



        ArrayList<MatOfPoint> mout = new ArrayList<>();
        mout.add(mopOut);

        //List<Integer> moi4L = matOfInt4.toList();


        Double contourArea = Imgproc.contourArea(contours.get(imax));
        //System.out.println("Contour: " + contourArea.doubleValue());
        //  Draw Contour
        Imgproc.drawContours(rgba1, outList, -1, new Scalar(0, 0, 0), 4);


        //  Draw Convex
        Double hullArea = Imgproc.contourArea(mopOut);
       // System.out.println("Hull: " + hullArea.doubleValue());

        Imgproc.drawContours(rgba1, mout, -1, new Scalar(200, 200, 200), 3);


        Double div = (contourArea / hullArea) * 100.0;
        //System.out.println(String.format("Div %.5s", div.toString()));

        //rgba = rgba1.clone();

        if(div < 90.0) {
            Imgproc.putText(rgba1, "Disjoined", new Point(rgba1.width() / 8, rgba1.height() / 8), Core.FONT_ITALIC, 2, new Scalar(0,0,0), 3);
        } else {
            Imgproc.putText(rgba1, "Solid", new Point(rgba1.width() / 8, rgba1.height() / 8), Core.FONT_ITALIC, 2, new Scalar(0,0,0), 3);
        }

        rgba = rgba1.clone();

        return rgba;
    }

    MatOfPoint hull2Points(MatOfInt hull, MatOfPoint contour) {
        List<Integer> indexes = hull.toList();
        List<Point> points = new ArrayList<>();
        MatOfPoint point= new MatOfPoint();
        for(Integer index : indexes) {
            points.add(contour.toList().get(index));
        }
        point.fromList(points);
        return point;
    }
}
