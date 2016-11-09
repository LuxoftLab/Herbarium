package com.example.shand.herbarium.ui.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.shand.herbarium.R;
import com.example.shand.herbarium.detector.CompoundLeafDetector;
import com.example.shand.herbarium.detector.ContourDetector;
import com.example.shand.herbarium.detector.ShapeDetector;

public class DebugMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_menu);

        addButton(R.id.debugCompoundLeafDetectorButton, CompoundLeafDetector.class.getCanonicalName());
        addButton(R.id.debugShapeDetectorButton, ShapeDetector.class.getCanonicalName());
        addButton(R.id.debugContourDetectorButton, ContourDetector.class.getCanonicalName());
    }

    public void addButton(int id, final String detectorClassName) {
        Button button = (Button) findViewById(id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DebugMenuActivity.this, DebugCameraActivity.class);
                intent.putExtra("detector", detectorClassName);
                startActivity(intent);
            }
        });
    }
}
