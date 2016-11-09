package com.example.shand.herbarium.classification;

import com.example.shand.herbarium.detector.CompoundLeafDetector;
import com.example.shand.herbarium.detector.ContourDetector;
import com.example.shand.herbarium.detector.Detector;
import com.example.shand.herbarium.detector.ShapeDetector;

import org.opencv.core.Mat;

public class Classifier {
    private LeafData leafData;
    private Features features;

    public Classifier(int featuresNumber, Mat rgba){
        leafData = new LeafData();
        leafData.setMat(rgba);
        features = new Features(featuresNumber);
    }

    public LeafData getLeafData(){
        return leafData;
    }

    //classify leaf
    public Features classify(){
        Detector detector = getDetectorHierarchy();

        while(detector != null) {
            detector = detector.detect(leafData, features);
        }

        return features;
    }

    //classify and handle detection start and end
    public Features classify(DetectionCallback detectionCallback){
        Detector detector = getDetectorHierarchy();

        while(detector != null) {
            detectionCallback.detectionStarted(detector.getClass().getSimpleName());
            detector = detector.detect(leafData, features);
            detectionCallback.detectionFinished(null);
        }

        return features;
    }

    private Detector getDetectorHierarchy() {
        return new CompoundLeafDetector(null, new ShapeDetector(new ContourDetector(null)), null);
    }

    public interface DetectionCallback {
        void detectionStarted(String message);
        void detectionFinished(String message);
    }
}
