package com.example.alex.streetmusic.model;

import java.util.HashMap;

/**
 * Created by Alex on 9/27/2016.
 */
public class EventList {
    private String listName;
    private String owner;
    private double latitude;
    private double longitude;
    private HashMap<String, Object> timestampCreated;
    private String listId;


    /**
     * Required public constructor
     */
    public EventList() {
    }

    /**
     * Use this constructor to create new ShoppingLists.
     * Takes shopping list listName and owner. Set's the last
     * changed time to what is stored in ServerValue.TIMESTAMP
     *
     * @param listName
     * @param owner
     */

    public EventList(String listName, String owner, double latitude, double longitude, HashMap<String, Object> timestampCreated,
                     String listId) {
        this.listName = listName;
        this.owner = owner;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestampCreated = timestampCreated;
        this.listId = listId;
    }

    public String getListName() {return listName;}

    public String getOwner() {return owner;}

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public String getListId() {
        return listId;
    }
}
