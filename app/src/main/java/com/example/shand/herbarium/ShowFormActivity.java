package com.example.shand.herbarium;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ShowFormActivity extends AppCompatActivity {
    private ImageView imageView;
    private FormSingleton formSingleton;
    private Mat rgbaRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_form);

        imageView = (ImageView) findViewById(R.id.showFormImageView);

        formSingleton = FormSingleton.getInstance();
        Mat rgba = FormSingleton.getInstance().getRGBA();
        Mat gray = rgba.clone();
        //Mat gray = FormSingleton.getInstance().getGray();
        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGB2GRAY);

        rgbaRes = formSingleton.findContoursAndHull(rgba, gray);

        Bitmap bitmap = Bitmap.createBitmap(rgbaRes.width(), rgbaRes.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgbaRes, bitmap);

        imageView.setImageBitmap(bitmap);


    }
}
