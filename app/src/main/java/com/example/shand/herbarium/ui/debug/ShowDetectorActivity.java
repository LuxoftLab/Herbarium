package com.example.shand.herbarium.ui.debug;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shand.herbarium.R;
import com.example.shand.herbarium.classification.Features;
import com.example.shand.herbarium.classification.LeafData;
import com.example.shand.herbarium.database.DatabaseHelper;
import com.example.shand.herbarium.detector.DebugDetector;
import com.example.shand.herbarium.ui.ImageSender;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;

public class ShowDetectorActivity extends Activity {
    protected DebugDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        LinearLayout layout = (LinearLayout) findViewById(R.id.showResultLinearLayout);

        //get rgba mat
        Mat rgba;
        try {
            rgba = new ImageSender().getMat(this, "image");
        } catch (IOException e) {
            Toast.makeText(this, "Can not receive image", Toast.LENGTH_LONG).show();
            return;
        }

        //create detector instance
        String detectorClassName = this.getIntent().getStringExtra("detector");
        detector = createDetector(detectorClassName);

        if(detector == null) {
            Toast.makeText(this, "Error creating instance of detector", Toast.LENGTH_LONG).show();
            return;
        }

        //use detector
        LeafData leafData = new LeafData();
        leafData.setMat(rgba);
        Features features = new Features(new DatabaseHelper(this).getFeaturesNumber());
        detector.debug();
        detector.detect(leafData, features);

        //show rgba mat
        showMat(leafData.getRgba(), layout);

        //show all debug images
        ArrayList<Mat> mat = detector.getDebugMat();
        for(int i = 0; i < mat.size(); i ++) {
            showMat(mat.get(i), layout);
        }

        //show all debug text
        TextView textView = new TextView(this);
        String text = "";
        ArrayList<String> debugText = detector.getDebugText();
        for(int i = 0; i < debugText.size(); i ++) {
            text += debugText.get(i) + "\n";
        }

        //show detector time
        long time = detector.getTime();
        if(time != -1) {
            text += "Time: " + time;
        }

        textView.setText(text);
        layout.addView(textView);
    }

    private DebugDetector createDetector(String detectorClassName) {
        try {
            Class detectorClass = Class.forName(detectorClassName);
            detector = (DebugDetector) detectorClass.newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            detector = null;
        } catch (InstantiationException e) {
            System.out.println(e.getMessage());
            detector = null;
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
            detector = null;
        }
        return detector;
    }

    protected void showMat(Mat mat, LinearLayout layout) {
        ImageView imageView = new ImageView(this);
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        imageView.setImageBitmap(bitmap);
        layout.addView(imageView);
    }
}
