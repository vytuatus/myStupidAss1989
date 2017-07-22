package com.example.alex.streetmusic;

import com.firebase.client.Firebase;

/**
 * Created by Alex on 10/7/2016.
 */
public class StreetMusicApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
    }

}
