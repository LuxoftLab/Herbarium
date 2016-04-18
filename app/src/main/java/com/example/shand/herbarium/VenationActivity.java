package com.example.shand.herbarium;

import android.app.Activity;
import android.content.Intent;
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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class VenationActivity extends Activity implements CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat frameMat, rgba;
    private VenationDetector venationDetector;
    private LeafData leafData;

    private float xTouch, yTouch;
    private boolean touch = false;

    private boolean isContourFound = false;

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

        venationDetector = new VenationDetector();

        if (!OpenCVLoader.initDebug()) {
            Log.e(FindContoursActivity.class.getName(), "Cannot connect to OpenCV Manager");
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mOpenCvCameraView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent ev) {
                //int action = ev.getAction();
                        xTouch = ev.getX();
                        yTouch = ev.getY();
                touch = true;

                return true;
            }
        });

        mOpenCvCameraView.setFocusable(true);

        leafData = LeafData.getLeafData();
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

    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {


        if(!isContourFound) {
            frameMat = inputFrame.gray();
            rgba = inputFrame.rgba();

            if (touch) {
                isContourFound = leafData.find(frameMat, xTouch, yTouch, rgba);
                if (isContourFound) {
                   // Log.d(this.getClass().getName().toString(), "CONTOUR FOUND");
                   // ArrayList<MatOfPoint> contours = new ArrayList<>();
                   // contours.add(leafData.getContour());
                    //Imgproc.drawContours(rgba, contours, -1, new Scalar(255, 0, 255), 8);
//                    Toast.makeText(this, "Contour found", Toast.LENGTH_SHORT).show();

                    /*long addrGray = frameMat.getNativeObjAddr();
                    long addrRgba = rgba.getNativeObjAddr();
*/


                    Intent intent = new Intent(this, ShowVenation.class);
  /*                  intent.putExtra( "image_gray", addrGray );
                    intent.putExtra( "image_rgba", addrRgba );
    */                startActivity( intent );
                }

                Imgproc.circle(rgba, new Point(xTouch, yTouch), 20, new Scalar(255, 0, 0));
            }

            if(mOpenCvCameraView.isFocused()){
                Imgproc.circle(rgba, new Point(20, 20), 20, new Scalar(0, 0, 0), -1);
            }

            return rgba;
        }

        if(mOpenCvCameraView.isFocused()){
            Imgproc.circle(rgba, new Point(20, 20), 20, new Scalar(0, 0, 0), -1);
        }

        return rgba;

        //venationDetector.detect(frameMat, inputFrame.rgba());
    }
}
