package com.example.shand.herbarium.ui.newplant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shand.herbarium.R;
import com.example.shand.herbarium.classification.Features;
import com.example.shand.herbarium.classification.Plant;
import com.example.shand.herbarium.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class FinishNewPlantActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_finish_new_plant);
        final DatabaseHelper dh = new DatabaseHelper(this);

        //get features
        ArrayList<int[]> features = (ArrayList<int[]>) this.getIntent().getSerializableExtra("features");

        //create all features text
        String s = "";
        for(int i = 0; i < features.size(); i ++) {
            s += "" + i + "\n";
            s += dh.featuresToString(features.get(i));
            s += "\n";
        }

        //add result text
        s += "\nRESULT: \n";
        final Features res = createResult(features);
        s += dh.featuresToString(res);

        //show text
        TextView textView = (TextView) findViewById(R.id.finishNewPlantTextView);
        textView.setText(s);

        //save to database and close
        Button button = (Button) findViewById(R.id.saveNewPlantTextView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEditText = (EditText) findViewById(R.id.finishNewPlantNameEditText);
                String name = nameEditText.getText().toString();

                if(name.equals("")) {
                    Toast.makeText(FinishNewPlantActivity.this, "Fill plant name text field", Toast.LENGTH_LONG).show();
                    return;
                }

                Plant newPlant = new Plant(name, res);
                dh.addPlant(newPlant);
                Toast.makeText(FinishNewPlantActivity.this, "New plant " + name + " is saved", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private Features createResult(ArrayList<int[]> results) {
        ArrayList<HashMap<Integer, Integer>> map = new ArrayList<>();

        //create HashMat for every feature;
        for(int i = 0; i < results.get(0).length; i ++) {
            map.add(new HashMap<Integer, Integer>());
        }

        //calculate frequency for every feature value
        for(int i = 0; i < results.size(); i ++) {
            int[] r = results.get(i);

            for(int j = 0; j < r.length; j ++) {
                if(!map.get(j).containsKey(r[j])) {
                    map.get(j).put(r[j], 1);
                } else {
                    int n = map.get(j).get(r[j]);
                    map.get(j).put(r[j], n + 1);
                }
            }
        }

        //find max frequency for every feature
        Features answ = new Features(map.size());
        for(int i = 0; i < map.size(); i ++) {
            int max = -1;
            int nmax = 1;
            int imax = -1;

            Set<Integer> keys = map.get(i).keySet();
            for(Integer k : keys) {
                if (map.get(i).get(k) > max) {
                    max = map.get(i).get(k);
                    nmax = 1;
                    imax = k;
                } else if (map.get(i).get(k) == max) {
                    nmax++;
                }
            }

            //if several feature values has max frequency, ambiguity is obtained
            if(nmax > 1) {
                answ.setFeature(i, 0); //set unknown value
            } else {
                //else save feature value
                answ.setFeature(i, imax);
            }
        }

        return answ;
    }
}
