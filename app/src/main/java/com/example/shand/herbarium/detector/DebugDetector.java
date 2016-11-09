package com.example.shand.herbarium.detector;

import org.opencv.core.Mat;
import java.util.ArrayList;

//must have public default constructor
//extend for saving detector debug information
public abstract class DebugDetector implements Detector {
    protected boolean debug;
    private ArrayList<Mat> debugMat;
    private ArrayList<String> debugText;
    private long time = -1;

    public void debug() {
        debug = true;
        debugMat = new ArrayList<>();
        debugText = new ArrayList<>();
    }

    public ArrayList<Mat> getDebugMat() {
        return debugMat;
    }

    public void addDebugMat(Mat mat) {
        if(debug) {
            debugMat.add(mat.clone());
        }
    }

    public void startTimer() {
        if(debug) {
            time = System.currentTimeMillis();
        }
    }

    public void stopTimer() {
        if(debug) {
            time = System.currentTimeMillis() - time;
        }
    }

    public long getTime() {
        return time;
    }

    public void addDebugText(String text) {
        if(debug) {
            debugText.add(text);
        }
    }

    public ArrayList<String> getDebugText() {
        return debugText;
    }
}
