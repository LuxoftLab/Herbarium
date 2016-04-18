package com.example.shand.herbarium;

import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class VenationDetector {
    private Mat morphology;
    private Mat contour;
    private Mat imageEnchancement;
    private Mat binarization;
    private Mat findLines;

    public Mat getMorphology(){
        return morphology;
    }

    public Mat getContour(){
        return contour;
    }

    public Mat getImageEnchancement(){
        return imageEnchancement;
    }

    public Mat getBinarization(){
        return binarization;
    }

    public Mat getFindLines(){
        return findLines;
    }

    public Mat detect(Mat gray, Mat rgba){
        /*ArrayList<MatOfPoint> contours = findContours(gray.clone());
        int index = findOuterContour(contours);
*/
        LeafData leafData = LeafData.getLeafData();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        contours.add(leafData.getContour());


        gray = morphologyProcessing(gray);

        gray = removeOuterContour(gray, contours, -1);

        gray = imageEnhancement(gray);
        gray = binarization(gray);

        findLines(gray, rgba);

        return rgba;
    }

    private Mat grayscale(Mat rgba){
        Mat hsv = new Mat(), gray = new Mat(rgba.rows(), rgba.cols(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV);

        for(int i = 0; i < gray.rows(); i++){
            for(int j = 0; j < gray.cols(); j++){
                double d[] = hsv.get(i, j);
                gray.put(i, j, (double)((((d[0]+90)/100)+1 - d[2])/2));//
            }
        }

        return gray;
    }

    private Mat morphologyProcessing(Mat src){
        Mat closing = new Mat(src.rows(), src.cols(), src.type());
        //closing operation
        Imgproc.dilate(src, closing, new Mat(), new Point(-1, -1), 2, 1, new Scalar(1));
        Imgproc.erode(closing, closing, new Mat());
        Core.subtract(closing, src, src);
        morphology = src.clone();
        Imgproc.putText(morphology, "morphology", new Point(0, 90), 0, 2, new Scalar(255));

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
        contour = mat.clone();
       // if(imax != -1) {
            Mat buffer = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));
            Mat mask = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));
            Imgproc.drawContours(mask, contours, imax, new Scalar(255), -1);

            contour = mat.clone();

            Imgproc.drawContours(mat, contours, imax, new Scalar(0), 20);
            Log.d(getClass().getName(), "outer contour was removed");

            //Core.bitwise_and(mat, mask, mat);
            mat.copyTo(buffer, mask);

            mat = buffer;


            // Imgproc.drawContours(contour, contours, imax, new Scalar(255), 20);

            //contour = mat.clone();
      //  }else {

      //      Log.d(getClass().getName(), "outer contour wasn't removed");
      //  }

        return mat;
    }

    private Mat imageEnhancement(Mat mat){
        Imgproc.equalizeHist(mat, mat);
        imageEnchancement = mat.clone();
        Imgproc.putText(imageEnchancement, "imageEnchancement", new Point(0, 90), 0, 2, new Scalar(255));
        return mat;
    }

    private Mat binarization(Mat mat){
        Imgproc.threshold(mat, mat, 215, 255, Imgproc.THRESH_BINARY);
        binarization = mat.clone();
        Imgproc.putText(binarization, "binarization", new Point(0, 90), 0, 2, new Scalar(255));
        return mat;
    }

    private void findLines(Mat mat, Mat dst){
        findLines = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));



        Mat lines = new Mat();
        Imgproc.HoughLinesP(mat, lines, 1, Math.PI / 180, 60, 70, 10);

        int index = -1;
        double max_res = -1;

        Log.d("Lines", "Lines " + lines.cols());

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
            Imgproc.line(findLines, start, end, new Scalar(255), 8);
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

        Imgproc.putText(findLines, "findLines", new Point(0, 90), 0, 2, new Scalar(255));

    }
}
