package com.example.shand.herbarium;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CameraActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "Time in FindContours";
    private static int count = 0;
    private static long sum=0;

    private String cascadeFrontalFileName = "lbp_maidenhair_cascade.xml";
    //private String cascadeFrontalFileName = "lbp_basil_cascade.xml";
    private CameraBridgeViewBase mOpenCvCameraView;
    private LeafDetector leafDetector;
    private Mat frameMat;

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

        try {
            InputStream is = getAssets().open(cascadeFrontalFileName);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, cascadeFrontalFileName);
            FileOutputStream fos = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) > 0) {
                fos.write(buffer, 0, bytesRead);
            }
            is.close();
            fos.close();

            leafDetector = new LeafDetector(cascadeFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(FindContoursActivity.class.getName(), e.getMessage(), e);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


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
    }

    public void onCameraViewStopped() {
        Log.d(TAG, "average: "+ ((double)sum/count));
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        frameMat = inputFrame.gray();
        //frameMat = inputFrame.rgba();
        long start = System.currentTimeMillis();
        Log.d(TAG, "start: " + start);

        Rect[] rect = leafDetector.detect(inputFrame.gray());
        long end = System.currentTimeMillis();
        Log.d(TAG, "end: " + end);
        long diff = end - start;
        sum+=diff;
        count++;

        for (Rect r : rect) {
            Imgproc.rectangle(frameMat, r.tl(), r.br(), new Scalar(255, 255, 255, 255), 3);
        }
        Log.d(TAG, "difference: " + diff);

        return frameMat;

    }
}
