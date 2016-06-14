package com.example.shand.herbarium;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity {
    Button buttonMoveToCatchLeaves;
    Button buttonFindContour;
    Button buttonFindVenation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buttonMoveToCatchLeaves = (Button) findViewById(R.id.buttonCatchLeaves);

        buttonMoveToCatchLeaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Menu.this, CameraActivity.class);
                startActivity(mIntent);
            }
        });

        buttonFindContour = (Button) findViewById(R.id.contourButton);

        buttonFindContour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Menu.this, ComplicatedLeafActivity.class);
                startActivity(mIntent);
            }
        });

        buttonFindVenation = (Button) findViewById(R.id.buttonFindVenation);
        buttonFindVenation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Menu.this, VenationActivity.class);
                startActivity(mIntent);
            }
        });


    }


}

