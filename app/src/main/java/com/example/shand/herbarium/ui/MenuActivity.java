package com.example.shand.herbarium.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.shand.herbarium.R;
import com.example.shand.herbarium.ui.classify.ClassifyLeafCameraActivity;
import com.example.shand.herbarium.ui.debug.DebugMenuActivity;
import com.example.shand.herbarium.ui.newplant.NewPlantCameraActivity;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button button = (Button) findViewById(R.id.classifyButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MenuActivity.this, ClassifyLeafCameraActivity.class);
                startActivity(mIntent);
            }
        });

        button = (Button) findViewById(R.id.showDatabaseButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MenuActivity.this, ShowDatabaseActivity.class);
                startActivity(mIntent);
            }
        });

        button = (Button) findViewById(R.id.addNewPlantMenuButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MenuActivity.this, NewPlantCameraActivity.class);
                startActivity(mIntent);
            }
        });

        button = (Button) findViewById(R.id.debugButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MenuActivity.this, DebugMenuActivity.class);
                startActivity(mIntent);
            }
        });
    }
}

