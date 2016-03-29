package com.example.shand.herbarium;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class FindContoursActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "Time in FindContours";
    private static int count = 0;
    private static long sum=0;
    private Mat mRgba;
    private Mat mGray;
    private LeafClassifier leafClassifier;
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(FindContoursActivity.class.getName(), "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!OpenCVLoader.initDebug()) {
            Log.e(FindContoursActivity.class.getName(), "Cannot connect to OpenCV Manager");
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        leafClassifier = new LeafClassifier(0.01, 0.9);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(FindContoursActivity.class.getName(), "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(FindContoursActivity.class.getName(), "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        Log.d(TAG, "average: "+ ((double)sum/count));
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        long start = System.currentTimeMillis();
        Log.d(TAG, "start: " + start);
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.GaussianBlur(mGray, mGray, new Size(5, 5), 0.3 * (3 / 2 - 1) + 0.8, 0.0, 4);
        Imgproc.threshold(mGray, mGray, 160, 255, Imgproc.THRESH_BINARY_INV);

        Imgproc.Canny(mGray, mGray, 80, 100);
        Imgproc.findContours(mGray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

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
            Imgproc.drawContours(mRgba, contours, imax, new Scalar(0, 0, 255));
            Imgproc.putText(mRgba, (leafClassifier.hasSmoothEdges(contours.get(imax))? "smooth edges" : "ribbed edges"), new Point(0, 90), 0, 2, new Scalar(255, 0, 0));
        }
        long end = System.currentTimeMillis();
        Log.d(TAG, "end: " + end);
        long diff = end - start;
        sum+=diff;
        count++;
        Log.d(TAG, "difference: " + diff);

        return mRgba;
    }

}
