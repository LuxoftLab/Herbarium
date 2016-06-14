package com.example.shand.herbarium;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ShowComplicatedLeafActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_complicated_leaf);

        ImageView imageView = (ImageView) findViewById(R.id.showComplicatedLeafImageView);

        ComplicatedLeafDetector complicatedLeafDetector = new ComplicatedLeafDetector();
        complicatedLeafDetector.detect(LeafData.getLeafData().getRgba(), LeafData.getLeafData().getGray());

        Mat rgba = LeafData.getLeafData().getRgba();
        Bitmap bitmap = Bitmap.createBitmap(rgba.width(), rgba.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgba, bitmap);
        imageView.setImageBitmap(bitmap);
    }
}