package com.example.shand.herbarium.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageSender {
    //save image in mat to file, put filename to intent with key
    public void sendMat(Activity activity, Mat mat, Intent intent, String filename, String key) throws IOException {
        //convert mat to bitmap
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        //save image to file
        FileOutputStream stream = activity.openFileOutput(filename, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        stream.close();
        bitmap.recycle();

        intent.putExtra(key, filename);
    }

    //get filename using key, read mat from file
    public Mat getMat(Activity activity, String key) throws IOException {
        Bitmap bmp;
        Mat mat;

        //get file name using key
        String filename = activity.getIntent().getStringExtra(key);

        //read image from file
        FileInputStream is = activity.openFileInput(filename);
        bmp = BitmapFactory.decodeStream(is);
        is.close();

        //convert bitmap to mat
        mat = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp, mat);

        //delete file
        File dir = activity.getFilesDir();
        File file = new File(dir, filename);
        file.delete();

        return mat;
    }
}