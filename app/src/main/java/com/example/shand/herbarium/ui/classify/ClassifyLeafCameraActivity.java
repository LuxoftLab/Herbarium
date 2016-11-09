package com.example.shand.herbarium.ui.classify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.shand.herbarium.ui.ImageSender;
import com.example.shand.herbarium.ui.camera.CameraActivity;

import java.io.IOException;

public class ClassifyLeafCameraActivity extends CameraActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void captureImage() {
        Intent intent = new Intent(ClassifyLeafCameraActivity.this, ShowClassificationProgressActivity.class);

        try {
            new ImageSender().sendMat(this, rgba.clone(), intent, "classifyLeafActivityImage.png", "image");
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(this, "Can not send image", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
