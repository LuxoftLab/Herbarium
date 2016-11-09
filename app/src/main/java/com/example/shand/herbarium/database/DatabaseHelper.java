package com.example.shand.herbarium.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.shand.herbarium.classification.Features;
import com.example.shand.herbarium.classification.Plant;
import com.example.shand.herbarium.detector.CompoundLeafDetector;
import com.example.shand.herbarium.detector.ContourDetector;
import com.example.shand.herbarium.detector.Detector;
import com.example.shand.herbarium.detector.ShapeDetector;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TREES_TABLE = "trees";
    private static final String TREES_ID_COLUMN = "tree_id";
    private static final String TREES_NAME_COLUMN = "tree_name";

    private static final Detector[] detectorsList = {new CompoundLeafDetector(), new ShapeDetector(), new ContourDetector()};
    static {
        for(int i = 0; i < detectorsList.length; i ++) {
            detectorsList[i].setIdx(i);
        }
    }

    private String CREATE_TREES_TABLE = "create table " + TREES_TABLE +
            " (" + TREES_ID_COLUMN + " integer PRIMARY KEY AUTOINCREMENT, " +
            TREES_NAME_COLUMN + " varchar(200)";

    public DatabaseHelper(Context context) {
        super(context, "herbarium", null, 4);
    }

    public String getFeatureName(int i) {
        return detectorsList[i].getClass().getSimpleName();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(int i = 0; i < detectorsList.length; i++) {
            CREATE_TREES_TABLE += ", " + getFeatureName(i);
        }
        CREATE_TREES_TABLE += ");";

        db.execSQL(CREATE_TREES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TREES_TABLE);
        onCreate(db);
    }

    public String featuresToString(Features features) {
        String text = "";
        for(Detector detector : detectorsList) {
            text += detector + ": " + detector.getFeatureName(features.getFeature(detector.getIdx())) + "\n";
        }
        return text;
    }
    public String featuresToString(int[] features) {
        String text = "";
        for(Detector detector : detectorsList) {
            text += detector + ": " + detector.getFeatureName(features[detector.getIdx()]) + "\n";
        }
        return text;
    }

    public ArrayList<Plant> getPlants() {
        ArrayList<Plant> plants = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor c = database.query(DatabaseHelper.TREES_TABLE, null, null, null, null, null, null);

        if(c.moveToFirst()) {
            int idIdx = c.getColumnIndex(this.TREES_ID_COLUMN);
            int nameIdx = c.getColumnIndex(this.TREES_NAME_COLUMN);
            int featuresIdx[] = new int[this.detectorsList.length];
            for(int i = 0; i < featuresIdx.length; i ++) {
                featuresIdx[i] = c.getColumnIndex(getFeatureName(i));
            }

            do {
                Features features = new Features(this.detectorsList.length);
                for(int i = 0; i < this.detectorsList.length; i ++) {
                    features.setFeature(i, c.getInt(featuresIdx[i]));
                }

                Plant plant = new Plant(c.getInt(idIdx), c.getString(nameIdx), features);

                plants.add(plant);
            } while(c.moveToNext());
        }
        database.close();
        return plants;
    }

    public void addPlant(Plant plant) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(this.TREES_NAME_COLUMN, plant.getName());
        Features res = plant.getFeatures();
        for(int i = 0; i < this.detectorsList.length; i ++) {
            values.put(getFeatureName(i), res.getFeature(i));
        }

        database.insert(this.TREES_TABLE, null, values);

        database.close();
    }

    public int getFeaturesNumber() {
        return this.detectorsList.length;
    }
}