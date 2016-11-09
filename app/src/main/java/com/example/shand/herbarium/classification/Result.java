package com.example.shand.herbarium.classification;

public class Result {
    private String plantName;
    private double probability;

    public Result(String plantName, double probability) {
        this.plantName = plantName;
        this.probability = probability;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
