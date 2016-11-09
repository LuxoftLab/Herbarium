package com.example.shand.herbarium.ui.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import org.opencv.android.JavaCameraView;
import java.util.List;

//camera view, that uses autofocus
public class AutofocusJavaCameraView extends JavaCameraView {

    public AutofocusJavaCameraView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void setFocusMode(Context context, String type) {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            List<String> FocusModes = params.getSupportedFocusModes();

            if (FocusModes.contains(type)) {
                params.setFocusMode(type);
                Log.d(AutofocusJavaCameraView.class.getName(), "Focus mode is set");
            } else {
                Toast.makeText(context, type + " mode is not supported", Toast.LENGTH_SHORT).show();
            }

            mCamera.setParameters(params);
        } else {
            Log.e(AutofocusJavaCameraView.class.getName(), "Camera is not available");
        }
    }
}