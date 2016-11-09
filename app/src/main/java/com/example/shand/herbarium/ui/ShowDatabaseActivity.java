package com.example.shand.herbarium.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.shand.herbarium.R;
import com.example.shand.herbarium.classification.Plant;
import com.example.shand.herbarium.database.DatabaseHelper;

import java.util.ArrayList;

public class ShowDatabaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_database);

        //get all plant list
        DatabaseHelper dh = new DatabaseHelper(this);
        ArrayList<Plant> plants = dh.getPlants();

        //create text
        StringBuilder text = new StringBuilder();
        if(plants.size() == 0) {
            text.append("Database empty");
        } else {
            for (int i = 0; i < plants.size(); i++) {
                text.append(plants.get(i).getName() + "\n");
                text.append(dh.featuresToString(plants.get(i).getFeatures()) + "\n");
            }
        }

        //show text
        TextView textView = (TextView) findViewById(R.id.showDatabaseTextView);
        textView.setText(text.toString());
    }
}
