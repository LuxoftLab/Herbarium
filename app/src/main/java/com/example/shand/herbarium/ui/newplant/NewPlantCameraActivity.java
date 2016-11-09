package com.example.shand.herbarium.ui.newplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shand.herbarium.R;
import com.example.shand.herbarium.ui.ImageSender;
import com.example.shand.herbarium.ui.camera.AutofocusJavaCameraView;
import com.example.shand.herbarium.ui.camera.CameraActivity;

import org.opencv.android.CameraBridgeViewBase;

import java.io.IOException;
import java.util.ArrayList;

public class NewPlantCameraActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private ArrayList<ArrayList<Integer>> features;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //get features
        features = (ArrayList<ArrayList<Integer>>) this.getIntent().getSerializableExtra("features");
        if (features == null) {
            features = new ArrayList<>();
            Toast.makeText(this, "Start adding plant leaves", Toast.LENGTH_LONG).show();
        } else {
            String message = features.size() + (features.size() == 1 ? " leaf" : " leaves") + " saved";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        //cancel adding new plant button
        Button button = (Button) findViewById(R.id.cancelNewPlantButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //finish adding new plant button
        button = (Button) findViewById(R.id.finishNewPlantButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!features.isEmpty()) {
                    Intent intent = new Intent(NewPlantCameraActivity.this, FinishNewPlantActivity.class);
                    intent.putExtra("features", features);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(NewPlantCameraActivity.this, "Add plants to finish", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void setContent() {
        this.setContentView(R.layout.activity_new_plant_camera);
        cameraView = (AutofocusJavaCameraView) findViewById(R.id.tutorial1_activity_java_surface_view);
    }

    //start analysis for captured image
    @Override
    protected void captureImage() {
        Intent intent = new Intent(NewPlantCameraActivity.this, NewPlantAnalysisActivity.class);
        try {
            new ImageSender().sendMat(NewPlantCameraActivity.this, rgba.clone(), intent, "imageHerbarium.png", "image");
            intent.putExtra("features", features);
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(this, "Image can not be send", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}