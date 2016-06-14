package com.example.shand.herbarium;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class ComplicatedLeafActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private AutofocusJavaCameraView mOpenCvCameraView;
    private Mat frameMat, rgba;

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
        mOpenCvCameraView = (AutofocusJavaCameraView) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mOpenCvCameraView.setOnTouchListener(new View.OnTouchListener() {
            private boolean onTouch = false;
            public boolean onTouch(View v, MotionEvent ev) {
                if(!onTouch) {
                    Intent intent = new Intent(ComplicatedLeafActivity.this, ShowComplicatedLeafActivity.class);
                    startActivity(intent);
                    onTouch = true;
                    finish();
                }

                return true;
            }
        });
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
        rgba = new Mat();
        frameMat = new Mat();
        mOpenCvCameraView.setFocusMode(getApplicationContext(), Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    public void onCameraViewStopped() {
        rgba.release();
        frameMat.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat gray = inputFrame.gray();
        Mat rgba = inputFrame.rgba();

        LeafData leafData = LeafData.getLeafData();
        leafData.setGrayMat(gray.clone());
        leafData.setRgbaMat(rgba.clone());

        return rgba;
    }
}