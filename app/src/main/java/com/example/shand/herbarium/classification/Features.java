package com.example.shand.herbarium.classification;

//contains array with features values of leaf
public class Features {
    private int[] features; //index is delector index; value is detector feature type value; value 0 for unknown feature type

    public Features(int num) {
        features = new int[num];
    }

    public void setFeature(int idx, int res) {
        features[idx] = res;
    }

    public int getFeature(int idx) {
        return features[idx];
    }

    public int getFeatureCount() {
        return features.length;
    }

    public Features(int res[]) {
        features = res;
    }

    public int[] getFeatures() {
        return features;
    }

    //compare arrays of f1 and f2
    //return value is probability of equality plant types of f1 and f2
    public static double compare(Features f1, Features f2) {
        if (f1.features.length == 0 || f2.features.length == 0 || f1.features.length != f2.features.length)
            return 0;
        double probability = 0;

        for (int i = 0; i < f1.features.length; i++) {
            if (f1.features[i] == f2.features[i]) {
                probability++;
            }
        }

        probability /= f1.features.length;
        return probability;
    }
}
