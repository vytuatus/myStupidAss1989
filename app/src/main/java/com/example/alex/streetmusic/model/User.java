package com.example.alex.streetmusic.model;

import java.util.HashMap;

/**
 * Created by Alex on 9/24/2016.
 */
public class User {

    private String name;
    private String email;
    private HashMap<String, Object> timestampJoined;

    //Required public constructor
    public User(){

    }

    public User(String email, String name, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.timestampJoined = timestampJoined;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

}
