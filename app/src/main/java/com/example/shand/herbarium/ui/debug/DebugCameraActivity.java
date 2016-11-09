package com.example.shand.herbarium.ui.debug;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.shand.herbarium.ui.ImageSender;
import com.example.shand.herbarium.ui.camera.CameraActivity;

import java.io.IOException;

public class DebugCameraActivity extends CameraActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected void captureImage() {
        Intent intent = new Intent(DebugCameraActivity.this, ShowDetectorActivity.class);
        String detector = this.getIntent().getStringExtra("detector");
        intent.putExtra("detector", detector);

        try {
            new ImageSender().sendMat(DebugCameraActivity.this, rgba.clone(), intent, "imageHerbarium.png", "image");
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(this, "Can not send image", Toast.LENGTH_LONG).show();
        }
    }
}
