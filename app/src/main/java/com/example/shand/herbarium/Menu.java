package com.example.shand.herbarium;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity {

Button buttonMoveToCatchLeaves;

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

    }


}

