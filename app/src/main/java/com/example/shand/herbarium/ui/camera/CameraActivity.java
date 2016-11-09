package com.example.shand.herbarium.ui.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.example.shand.herbarium.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

//base class for all activities, that use camera
//touch screen to capture image
public abstract class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    protected AutofocusJavaCameraView cameraView;
    protected boolean touched; //whether user has touched screen or not
    protected boolean cameraFrameCaptured; //whether image is saves in rgba mat or not
    protected Mat rgba; //captured rgba image

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(CameraActivity.class.getName(), "OpenCV loaded successfully");
                    cameraView.enableView();
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
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (!OpenCVLoader.initDebug()) {
            Log.e(CameraActivity.class.getName(), "Cannot connect to OpenCV Manager");
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContent();

        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this);

        cameraView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent ev) {
                if(!touched) {
                    touched = true;
                }

                return true;
            }
        });
    }

    /*template method for setting content view and finding camera view
        by default:
        - content view - activity_camera
        - camera view - myJavaCameraView*/
    protected void setContent() {
        this.setContentView(R.layout.activity_camera);
        cameraView = (AutofocusJavaCameraView) findViewById(R.id.myJavaCameraView);
    }

    //template method for processing image, saved in rgba mat
    protected abstract void captureImage();

    @Override
    public void onPause() {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(CameraActivity.class.getName(), "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(CameraActivity.class.getName(), "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (cameraView != null)
            cameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        cameraView.setFocusMode(getApplicationContext(), Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mat = inputFrame.rgba();

        if(touched && !cameraFrameCaptured) {
            if(mat != null) {
                rgba = mat.clone();
                cameraFrameCaptured = true;
                captureImage();
                finish();
            }
        }

        return mat;
    }
}