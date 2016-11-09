package com.example.shand.herbarium.ui.classify;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shand.herbarium.R;
import com.example.shand.herbarium.ui.ImageSender;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

public class ShowClassificationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.showResultLinearLayout);

        //show rgba mat
        try {
            Mat rgba = new ImageSender().getMat(this, "rgba");
            Mat mat = rgba;
            ImageView imageView = new ImageView(this);
            Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
            imageView.setImageBitmap(bitmap);
            linearLayout.addView(imageView);
        } catch (IOException e) {
            Toast.makeText(this, "Can not receive image", Toast.LENGTH_LONG).show();
        }

        //show result text
        String resultText = getIntent().getStringExtra("result");
        TextView textView = new TextView(this);
        textView.setText(resultText);
        linearLayout.addView(textView);
    }
}