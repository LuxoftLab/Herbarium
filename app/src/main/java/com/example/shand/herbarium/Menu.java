package com.example.shand.herbarium;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button buttonMoveToCatchLeaves = (Button) findViewById(R.id.buttonCatchLeaves);

        buttonMoveToCatchLeaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Menu.this, CameraActivity.class);
                startActivity(mIntent);
            }
        });

        buttonMoveToCatchLeaves = (Button) findViewById(R.id.contourButton);

        buttonMoveToCatchLeaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Menu.this, FindContoursActivity.class);
                startActivity(mIntent);
            }
        });

    }


}

