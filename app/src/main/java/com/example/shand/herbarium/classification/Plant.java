package com.example.shand.herbarium.classification;

public class Plant {
    private int id;
    private String name;
    private Features features;

    public Plant(int id, String name, Features features) {
        this.id = id;
        this.name = name;
        this.features = features;
    }

    public Plant(String name, Features features) {
        this.name = name;
        this.features = features;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }
}