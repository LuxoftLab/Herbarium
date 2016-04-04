package com.example.shand.herbarium;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class VenetionDetector {
    public Mat detect(Mat mat, Mat rgba, Mat mask_image){

        Imgproc.drawContours(mask_image, contours, ind, Scalar(255), CV_FILLED);
// copy only non-zero pixels from your image to original image
        your_image.copyTo(original_image, mask_image);
/*
        double d[] = new double[1];
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
         for(int x = 0; x < mat.rows(); x++) {
             for(int y = 0; y < mat.cols(); y++) {
                 d = mat.get(x, y);
                 mat.put(x, y, 255 - d[0]);
             }
         }
        *//*
        Mat invertcolormatrix= new Mat(mat.rows(),mat.cols(), mat.type(), new Scalar(255,255,255));

        Core.subtract(invertcolormatrix, mat, mat);
        //Mat output = Scalar.all(255).;
        //Imgproc.GaussianBlur(mat, res, new Size(5, 5), 0);//, 0.3 * (3 / 2 - 1) + 0.8, 0.0, 4);
        //Imgproc.adaptiveThreshold(res, res, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 7, 0);// | Imgproc.THRESH_OTSU );
        //Imgproc.threshold(mat, res, 160, 255, Imgproc.THRESH_BINARY_INV);
        Core.inRange(mat, new Scalar(100), new Scalar(255), mat);*/
        //Imgproc.Canny(res, res, 300, 600, 5, true);
        //Imgproc.Canny(res, res, 0.0, 300.0);
        //Imgproc.Canny(res, res, 80, 100);
    //    Imgproc.findContours(res, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

      //  Imgproc.drawContours(rgba, contours, -1, new Scalar(255, 0, 0, 0));


        return mat;
    }
}
