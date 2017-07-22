package com.example.alex.streetmusic.model;

import java.util.HashMap;

/**
 * Created by Alex on 10/18/2016.
 */
public class Locations {

    private double latitude;
    private double longitude;
    private String adress;
    private HashMap<String, Object> timestampCreated;


    /**
     * Required public constructor
     */
    public Locations() {
    }

    public Locations(double latitude, double longitude, String adress, HashMap<String, Object> timestampCreated) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
        this.timestampCreated = timestampCreated;

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAdress(){
        return adress;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

}
