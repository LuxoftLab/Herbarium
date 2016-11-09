package com.example.shand.herbarium.classification;

import java.util.ArrayList;

public class Analyzer {
    private ArrayList<Plant> plants;
    public double[] probabilities;

    public Analyzer(ArrayList<Plant> plants){
        this.plants = plants;
        probabilities = new double[plants.size()];
    }

    public ArrayList<Result> analyse(Features features) {
        for(int i = 0; i < plants.size(); i ++) {
            probabilities[i] = Features.compare(features, plants.get(i).getFeatures());
        }

        ArrayList<Integer> mostProbablePlantIndexes = new ArrayList<>();
        double maxProbability = 0;
        for(int i = 0; i < plants.size(); i ++) {
            if(maxProbability < probabilities[i]) {
                maxProbability = probabilities[i];
                mostProbablePlantIndexes = new ArrayList<>();
                mostProbablePlantIndexes.add(i);
            }
            else if(maxProbability == probabilities[i]) {
                mostProbablePlantIndexes.add(i);
            }
        }

        if(maxProbability == 0) return new ArrayList<>();

        ArrayList<Result> results = new ArrayList<>();
        for(int i = 0; i < mostProbablePlantIndexes.size(); i ++) {
            int idx = mostProbablePlantIndexes.get(i);
            results.add(new Result(plants.get(idx).getName(), probabilities[idx]));
        }

        return results;
    }
}