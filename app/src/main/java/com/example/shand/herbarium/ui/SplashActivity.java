package com.example.shand.herbarium.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.shand.herbarium.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();

    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
