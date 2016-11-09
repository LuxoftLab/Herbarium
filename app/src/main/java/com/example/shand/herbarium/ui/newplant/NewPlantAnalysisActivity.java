package com.example.shand.herbarium.ui.newplant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shand.herbarium.R;
import com.example.shand.herbarium.classification.Classifier;
import com.example.shand.herbarium.classification.Features;
import com.example.shand.herbarium.database.DatabaseHelper;
import com.example.shand.herbarium.ui.ImageSender;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;

public class NewPlantAnalysisActivity extends Activity {
    private ArrayList<int[]> features;
    private Features result;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_new_plant_analysis);

        DatabaseHelper dh = new DatabaseHelper(this);

        //get features
        features = (ArrayList<int[]>) this.getIntent().getSerializableExtra("features");

        //get rgba mat
        Mat rgba;
        try {
            rgba = new ImageSender().getMat(this, "image");
        } catch (IOException e) {
            Toast.makeText(this, "Image is not received", Toast.LENGTH_LONG).show();
            returnToCameraActivity();
            return;
        }

        //classify leaf
        Classifier classifier = new Classifier(dh.getFeaturesNumber(), rgba);
        result = classifier.classify();

        //create result text
        String resultText = "";
        resultText += dh.featuresToString(result);

        //show image
        rgba = classifier.getLeafData().getRgba();
        ImageView imageView = (ImageView) findViewById(R.id.newPlantImageView);
        Bitmap bitmap = Bitmap.createBitmap(rgba.width(), rgba.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgba, bitmap);
        imageView.setImageBitmap(bitmap);

        //show result text
        TextView textView = (TextView) findViewById(R.id.newPlantResultTextView);
        textView.setText(resultText + "\n\n");

        //delete result button
        Button button = (Button) findViewById(R.id.deleteNewPlantButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToCameraActivity();
            }
        });

        //save result button
        button = (Button) findViewById(R.id.saveNewPlantButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                features.add(result.getFeatures());
                returnToCameraActivity();
            }
        });
    }

    //delete result
    @Override
    public void onBackPressed() {
        returnToCameraActivity();
    }

    //start camera activity and send features there
    private void returnToCameraActivity() {
        Intent intent = new Intent(NewPlantAnalysisActivity.this, NewPlantCameraActivity.class);
        intent.putExtra("features", features);
        startActivity(intent);
        finish();
    }
}
