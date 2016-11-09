package com.example.shand.herbarium.detector;

import com.example.shand.herbarium.classification.Features;
import com.example.shand.herbarium.classification.LeafData;

public interface Detector {
    //detect feature and save result in features
    // return value is next detector in detectors hierarchy
    Detector detect(LeafData leafData, Features features);

    int getIdx(); //get index of feature
    void setIdx(int i); //set index of feature
    String getFeatureName(int i); //get feature name with value i
}
