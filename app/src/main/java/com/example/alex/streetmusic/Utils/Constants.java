package com.example.alex.streetmusic.Utils;

/**
 * Created by Alex on 9/24/2016.
 */
public class Constants {

    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where active lists are stored (ie "activeLists")
     */
    public static final String FIREBASE_LOCATION_EVENT_LISTS = "eventLists";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_LOCATIONS = "locations";
    public static final String FIREBASE_LOCATION_BAND_LIST = "bandList";

    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = "https://streetmusic-a3c02.firebaseio.com/";
    public static final String FIREBASE_URL_EVENT_LISTS = FIREBASE_URL + "/" + FIREBASE_LOCATION_EVENT_LISTS;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_STORAGE = "gs://streetmusic-a3c02.appspot.com";

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_EVENT_NAME = "eventName";
    public static final String FIREBASE_PROPERTY_EVENT_TIME = "eventTime";
    public static final String FIREBASE_PROPERTY_MUSIC_STYLE = "musicStyle";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timeStamp";
    public static final String FIREBASE_PROPERTY_LATITUDE = "latitude";
    public static final String FIREBASE_PROPERTY_LONGITUDE = "longitude";

    /* Constants for Firebase login
    */
    public static final String PASSWORD_PROVIDER = "password";

    /**
     * Constants for shared preferences keys
     */
    public static final String KEY_USER_UID = "USER_UID";
    public static final String KEY_HAS_BAND = "HAS_BAND";
    public static final String KEY_SAVE_PICTURE_FIRST_PAGE = "SAVE_PICTURE_FIRST_PAGE";
    public static final String KEY_SAVE_BAND_DESCRIPTION = "SAVE_BAND_DESCRIPTION";
    public static final String KEY_MUSIC_STYLE = "MUSIC_STYLE";
    public static final String KEY_WRITTEN_BAND_NAME = "WRITTEN_BAND_NAME";


    /**
     * Constants for bundles
     */

    public static final String KEY_USER_KEY = "USER_KEY";
    public static final String KEY_IMAGE_URI = "IMAGE_URI";

    /**
     * Constants for intents
     */
    public static final String INTENT_KEY_PREVIOUS_LOCATION = "previousLocation";
    public static final String INTENT_KEY_RESULT_ADRESS = "resultAdress";
    public static final String INTENT_KEY_LAT_LONG = "latLong";
    public static final String INTENT_KEY_ID = "keyId";
    public static final String INTENT_KEY_ID_FROM_DETAILED_ACTIVITY = "keyIdFromDetailedActivity";
    public static final String INTENT_KEY_LOCATION_PUSH_ID = "keyLocationPushId";

    /**
     * Constants for Maps
     */

    public static final double LAT_VILNIUS = 54.687157;
    public static final double LNG_VILNIUS = 25.279652;


}
