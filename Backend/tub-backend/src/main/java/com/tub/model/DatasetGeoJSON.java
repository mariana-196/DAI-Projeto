package com.tub.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatasetGeoJSON {
    
    private String type = "FeatureCollection";
    private List<Map<String, Object>> features = new ArrayList<>();

    public DatasetGeoJSON() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<Map<String, Object>> getFeatures() { return features; }
    public void setFeatures(List<Map<String, Object>> features) { this.features = features; }
}