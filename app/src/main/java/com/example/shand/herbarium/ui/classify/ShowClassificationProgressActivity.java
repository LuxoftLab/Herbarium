package com.example.shand.herbarium.ui.classify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.shand.herbarium.classification.Analyzer;
import com.example.shand.herbarium.classification.Classifier;
import com.example.shand.herbarium.classification.Features;
import com.example.shand.herbarium.classification.LeafData;
import com.example.shand.herbarium.classification.Plant;
import com.example.shand.herbarium.classification.Result;
import com.example.shand.herbarium.database.DatabaseHelper;
import com.example.shand.herbarium.ui.ImageSender;

import org.opencv.core.Mat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShowClassificationProgressActivity extends Activity {
    private Features features;
    private LeafData leafData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ClassificationTask(this, new DatabaseHelper(this)).execute();
    }

    private class ClassificationTask extends AsyncTask<String, String, String> {
        private Activity context;
        private ProgressDialog progressDialog;
        private Handler handler; //sets new message in progress dialog
        private String resultText;
        private DatabaseHelper databaseHelper;

        public ClassificationTask(Activity context, DatabaseHelper databaseHelper) {
            this.context = context;
            this.databaseHelper = databaseHelper;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Classifying leaf...");
            progressDialog.setMessage("Start classification");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String messageText = (String) msg.obj;
                    progressDialog.setMessage(messageText);
                }
            };
        }

        @Override
        protected String doInBackground(String... params) {
            final int featuresNumber = databaseHelper.getFeaturesNumber();
            //get rgba mat
            Mat rgba;

            try {
                rgba = new ImageSender().getMat(context, "image");
            } catch (IOException e) {
                resultText = "Can not receive image";
                return "";
            }

            //classify rgba
            final Classifier classifier = new Classifier(featuresNumber, rgba);
            features = classifier.classify(new Classifier.DetectionCallback() {
                private int currentProgress = 0;

                @Override
                //set new message in progress dialog when detector started
                public void detectionStarted(String message) {
                    Message msg = new Message();
                    msg.obj = message;

                    handler.sendMessage(msg);
                }

                @Override
                //update progress when detector finished
                public void detectionFinished(String message) {
                    currentProgress += 100 / (featuresNumber + 1);
                    progressDialog.setProgress(currentProgress);
                }
            });
            leafData = classifier.getLeafData();

            //update progress dialog : analysing result
            progressDialog.setProgress(100 * featuresNumber / (featuresNumber + 1));
            Message message = new Message();
            message.obj = "Analysing result";
            handler.sendMessage(message);

            //create result text
            resultText = "Features: ";
            resultText += databaseHelper.featuresToString(features);

            ArrayList<Plant> plants = databaseHelper.getPlants();

            //check whether database is empty
            if(plants.isEmpty()) {
                resultText += "\n\nDatabase is empty";
                return "";
            }

            //analyse features, if database is not empty
            Analyzer analyzer = new Analyzer(plants);
            ArrayList<Result> results = analyzer.analyse(features);

            if(results.isEmpty()) {
                resultText += "\n\nNo matches found for this plant in database";
                return "";
            }

            //create text with plant names and probabilities
            resultText += "\n\nresult: ";
            for (int i = 0; i < results.size(); i++) {
                resultText += "\n\t" + results.get(i).getPlantName() + " with " + new DecimalFormat("##.##").format((results.get(i).getProbability() * 100)) + "% matches";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(context, ShowClassificationActivity.class);

            if(leafData != null) {
                try {
                    new ImageSender().sendMat(context, leafData.getRgba().clone(), intent, "rgba", "rgba");
                } catch (IOException e) {
                    Toast.makeText(context, "Can not send image", Toast.LENGTH_LONG).show();
                }
            }

            intent.putExtra("result", resultText);
            context.startActivity(intent);

            progressDialog.dismiss();

            context.finish();
        }
    }
}