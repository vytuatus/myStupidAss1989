package com.example.alex.streetmusic.ViewPagerFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alex.streetmusic.Adapters.MusicianProfileListAdapter;
import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.model.Band;
import com.example.alex.streetmusic.model.User;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Alex on 9/19/2016.
 */
public class MusicianProfile extends Fragment{

    private Query mMusicianProfileInformation;
    private SharedPreferences myPrefs;
    private String mUserKey;
    private ValueEventListener bandListEventListener;
    private static final String LOG_TAG = MusicianProfile.class.getSimpleName();
    private MusicianProfileListAdapter mMusicianProfileListAdapter;
    private ListView mListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_musician_profile, container, false);
        initializeVariables(rootView);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserKey = myPrefs.getString(Constants.KEY_USER_UID, "didNotFetchTheKey");
        mMusicianProfileInformation = FirebaseDatabase.getInstance().getReference().
                child(Constants.FIREBASE_LOCATION_USERS).child(mUserKey).
                child(Constants.FIREBASE_LOCATION_BAND_LIST);

        mMusicianProfileListAdapter = new MusicianProfileListAdapter(getActivity(), Band.class,
                R.layout.musician_profile_single_active_list, mMusicianProfileInformation);

        mListView.setAdapter(mMusicianProfileListAdapter);
        return rootView;

    }


    private void initializeVariables(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.musician_profile_list_view);
    }

}
