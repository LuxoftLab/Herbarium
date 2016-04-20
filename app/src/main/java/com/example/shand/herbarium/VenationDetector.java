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
import java.util.Arrays;

public class VenationDetector {
    private Mat morphology;
    private Mat contour;
    private Mat imageEnchancement;
    private Mat binarization;
    private Mat findLines;

    public Mat getMorphology() {
        return morphology;
    }

    public Mat getContour() {
        return contour;
    }

    public Mat getImageEnchancement() {
        return imageEnchancement;
    }

    public Mat getBinarization() {
        return binarization;
    }

    public Mat getFindLines() {
        return findLines;
    }

    public Mat detect(Mat gray, Mat rgba) {
        float minLineLength;

        LeafData leafData = LeafData.getLeafData();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        contours.add(leafData.getContour());


        gray = morphologyProcessing(gray);

        gray = removeOuterContour(gray, contours, -1);

        gray = imageEnhancement(gray);
        gray = binarization(gray);

        minLineLength = getMinLineLength(rgba, leafData.getContour());

        Mat lines = findLines(gray, rgba, minLineLength / 3);

        int k = deleteCloseLines(lines, rgba);
        Log.d("Lines", "Unique lines " + k);

        Imgproc.putText(rgba, classify(k), new Point(0, 90), 0, 2, new Scalar(255, 255, 255), 3);

        return rgba;
    }

    private Mat morphologyProcessing(Mat src) {
        Mat closing = new Mat(src.rows(), src.cols(), src.type());
        //closing operation
        Imgproc.dilate(src, closing, new Mat(), new Point(-1, -1), 2, 1, new Scalar(1));
        Imgproc.erode(closing, closing, new Mat());
        Core.subtract(closing, src, src);
        morphology = src.clone();
        Imgproc.putText(morphology, "morphology", new Point(0, 90), 0, 2, new Scalar(255));

        return src;
    }

    private ArrayList<MatOfPoint> findContours(Mat mat) {
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.GaussianBlur(mat, mat, new Size(5, 5), 0.3 * (3 / 2 - 1) + 0.8, 0.0, 4);
        Imgproc.threshold(mat, mat, 160, 255, Imgproc.THRESH_BINARY_INV);

        Imgproc.Canny(mat, mat, 80, 100);
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        hierarchy.release();

        return contours;
    }

    private int findOuterContour(ArrayList<MatOfPoint> contours) {
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

    private Mat removeOuterContour(Mat mat, ArrayList<MatOfPoint> contours, int imax) {
        contour = mat.clone();

        Mat buffer = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));
        Mat mask = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));
        Imgproc.drawContours(mask, contours, imax, new Scalar(255), -1);

        contour = mat.clone();

        Imgproc.drawContours(mat, contours, imax, new Scalar(0), 20);
        Log.d(getClass().getName(), "outer contour was removed");

        mat.copyTo(buffer, mask);

        mat = buffer;

        return mat;
    }

    private Mat imageEnhancement(Mat mat) {
        Imgproc.equalizeHist(mat, mat);
        imageEnchancement = mat.clone();
        Imgproc.putText(imageEnchancement, "imageEnchancement", new Point(0, 90), 0, 2, new Scalar(255));
        return mat;
    }

    private Mat binarization(Mat mat) {
        Imgproc.threshold(mat, mat, 215, 255, Imgproc.THRESH_BINARY);
        binarization = mat.clone();
        Imgproc.putText(binarization, "binarization", new Point(0, 90), 0, 2, new Scalar(255));
        return mat;
    }

    private boolean areLinesEqual(double vec1[], double vec2[]) {
        double length1 = Math.sqrt((vec1[0] - vec1[2]) * (vec1[0] - vec1[2]) + (vec1[1] - vec1[3]) * (vec1[1] - vec1[3]));//sqrtf((l1[2] - l1[0])*(l1[2] - l1[0]) + (l1[3] - l1[1])*(l1[3] - l1[1]));
        double length2 = Math.sqrt((vec2[0] - vec2[2]) * (vec2[0] - vec2[2]) + (vec2[1] - vec2[3]) * (vec2[1] - vec2[3]));//sqrtf((l1[2] - l1[0])*(l1[2] - l1[0]) + (l1[3] - l1[1])*(l1[3] - l1[1]));

        double product = (vec1[2] - vec1[0]) * (vec2[2] - vec2[0]) + (vec1[3] - vec1[1]) * (vec2[3] - vec2[1]);


        Log.d("lines", "angle = " + Math.acos(Math.abs(product / (length1 * length2))) * 180 / Math.PI);

        if (Math.abs(product / (length1 * length2)) < Math.cos(5 * Math.PI / 180))
            return false;

        Log.d("lines", "cos = " + Math.abs(product / (length1 * length2)) + " cos5 = " + Math.cos(5 * Math.PI / 180));

        double y1 = (vec1[3] - vec1[1]);
        double x1 = (vec1[2] - vec1[0]);

        double y2 = (vec2[3] - vec2[1]);
        double x2 = (vec2[2] - vec2[0]);

        double k1 = y1 / x1;
        double k2 = y2 / x2;

        double b1 = -(vec1[0] * y1 + vec1[1] * x1) / x1;
        double b2 = -(vec2[0] * y2 + vec2[1] * x2) / x2;

        double s1 = x1 * Math.sqrt(x1 * x1 + y1 * y1);
        double s2 = x2 * Math.sqrt(x2 * x2 + y2 * y2);

        double s = (s1 + s2) / 2;

        //double m = (k1 + k2) / 2;

        double dist = Math.abs(b2 - b1) / s;

        Log.d("Lines", "distance " + dist);
        Log.d("Lines", "max dist " + Math.max(length1, length2) * 0.1);

        if (dist > Math.max(length1, length2) * 0.1)
            return false;

        return true;
    }

    private double calculateLineLength(double vec1[]) {
        return Math.sqrt((vec1[0] - vec1[2]) * (vec1[0] - vec1[2]) + (vec1[1] - vec1[3]) * (vec1[1] - vec1[3]));
    }

    private int deleteCloseLines(Mat lines, Mat rgba) {
        int output[] = new int[lines.rows()];

        Arrays.fill(output, 0);

        boolean flag;

        for (int i = 0; i < lines.rows(); i++) {
            flag = true;
            for (int j = i + 1; j < lines.rows() && flag; j++) {
                double[] vec1 = lines.get(i, 0);
                double[] vec2 = lines.get(j, 0);

                if (output[j] != -1 && areLinesEqual(vec1, vec2)) {
                    if (calculateLineLength(vec1) > calculateLineLength(vec2)) {
                        output[j] = -1;
                    } else {
                        output[i] = -1;
                        flag = false;
                    }
                }
            }
        }

        int k = 0;
        for (int i = 0; i < lines.rows(); i++) {
            if (output[i] != -1) {
                double[] vec = lines.get(i, 0);
                double x1 = vec[0],
                        y1 = vec[1],
                        x2 = vec[2],
                        y2 = vec[3];
                Point start = new Point(x1, y1);
                Point end = new Point(x2, y2);

                k++;
                Imgproc.line(rgba, start, end, new Scalar(255, 0, 0), 3);
            }
        }

        return k;
    }

    private Mat findLines(Mat mat, Mat dst, float minLineLength) {
        findLines = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));

        Mat lines = new Mat();
        Imgproc.HoughLinesP(mat, lines, 1, Math.PI / 180, (int) minLineLength - (int) (minLineLength * 0.05), minLineLength, minLineLength * 0.05);

        int index = -1;
        double max_res = -1;

        Log.d("Lines", "Lines " + lines.rows());

        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            double res = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

            if (res > max_res) {
                max_res = res;
                index = x;
            }

            //Imgproc.line(dst, start, end, new Scalar(255, 0, 0), 8);
            Imgproc.line(findLines, start, end, new Scalar(255), 1);
        }

        if (lines.rows() == 0) {
            return lines;
        }

        int x = index;
        double[] vec = lines.get(x, 0);

        double x1 = vec[0],
                y1 = vec[1],
                x2 = vec[2],
                y2 = vec[3];
        Point start = new Point(x1, y1);
        Point end = new Point(x2, y2);

        //draw main vein
        //Imgproc.line(dst, start, end, new Scalar(255, 0, 255), 8);

        Imgproc.putText(findLines, "findLines", new Point(0, 90), 0, 2, new Scalar(255));

        return lines;
    }

    public float getMinLineLength(Mat rgbs, MatOfPoint contour) {
        float f[] = new float[1];
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contour.toArray());

        RotatedRect box = Imgproc.fitEllipse(matOfPoint2f);
        Log.d("Lines", "Radius: " + f[0]);

        //Imgproc.ellipse(rgbs, box, new Scalar(255, 255, 0));
        return (float) Math.min(box.size.width, box.size.height);
    }

    private String classify(int numLines) {
        if(numLines == 0){
            return "0 veins found";
        }
        if (numLines > 1) {
            return "> 1 main veins";
        }
        return "1 main vein";
    }
}
