package com.example.shand.herbarium;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ShowVenation extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_venation);

        imageView = (ImageView) findViewById(R.id.showVenationImageView);

        /*long frameAddress = getIntent().getLongExtra("image_gray", 0);
        Mat gray = new Mat(frameAddress);

        frameAddress = getIntent().getLongExtra("image_rgba", 0);
        Mat rgba = new Mat(frameAddress);*/

        Mat rgba = LeafData.getLeafData().getRgba();
        Mat gray = LeafData.getLeafData().getGray();

        VenationDetector venationDetector = new VenationDetector();
        rgba = venationDetector.detect(gray, rgba);

        Bitmap bitmap = Bitmap.createBitmap(rgba.width(), rgba.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgba, bitmap);

        imageView.setImageBitmap(bitmap);

        LinearLayout scrollView = (LinearLayout) findViewById(R.id.showVenationLayout);

        imageView = new ImageView(this);
        Mat morphology = venationDetector.getMorphology();
        bitmap = Bitmap.createBitmap(morphology.width(), morphology.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(morphology, bitmap);
        imageView.setImageBitmap(bitmap);
        scrollView.addView(imageView);

        imageView = new ImageView(this);
        morphology = venationDetector.getContour();
        bitmap = Bitmap.createBitmap(morphology.width(), morphology.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(morphology, bitmap);
        imageView.setImageBitmap(bitmap);
        scrollView.addView(imageView);

        imageView = new ImageView(this);
        morphology = venationDetector.getImageEnchancement();
        bitmap = Bitmap.createBitmap(morphology.width(), morphology.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(morphology, bitmap);
        imageView.setImageBitmap(bitmap);
        scrollView.addView(imageView);

        imageView = new ImageView(this);
        morphology = venationDetector.getBinarization();
        bitmap = Bitmap.createBitmap(morphology.width(), morphology.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(morphology, bitmap);
        imageView.setImageBitmap(bitmap);
        scrollView.addView(imageView);

        imageView = new ImageView(this);
        morphology = venationDetector.getFindLines();
        bitmap = Bitmap.createBitmap(morphology.width(), morphology.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(morphology, bitmap);
        imageView.setImageBitmap(bitmap);
        scrollView.addView(imageView);


       /* imageView = new ImageView(this);
        morphology = venationDetector.getFindLines2();
        bitmap = Bitmap.createBitmap(morphology.width(), morphology.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(morphology, bitmap);
        imageView.setImageBitmap(bitmap);
        scrollView.addView(imageView);*/
    }
}
