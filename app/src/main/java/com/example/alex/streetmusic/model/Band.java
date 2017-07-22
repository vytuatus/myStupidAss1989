package com.example.alex.streetmusic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vytuatus on 12/19/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Band {

    private String bandName;
    private String bandDescription;
    private HashMap<String, Object> timestampCreated;
    private ArrayList<String> musicStyles;



    /**
     * Required public constructor
     */
    public Band() {
    }

    /**
     * Use this constructor to create new ShoppingLists.
     * Takes shopping list listName and owner. Set's the last
     * changed time to what is stored in ServerValue.TIMESTAMP
     *
     * @param bandName
     * @param bandDescription
     */

    public Band(String bandName, String bandDescription, ArrayList<String> musicStyles, HashMap<String, Object> timestampCreated) {
        this.bandName = bandName;
        this.bandDescription = bandDescription;
        this.timestampCreated = timestampCreated;
        this.musicStyles = musicStyles;
    }

    public String getBandName() {
        return bandName;
    }

    public String getBandDescription() {
        return bandDescription;

    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public ArrayList<String> getMusicStyles(){
        return musicStyles;
    }


}
