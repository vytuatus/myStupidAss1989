package com.example.alex.streetmusic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.streetmusic.Map.MapsActivityFromDetails;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.model.EventList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ActiveListDetailsActivity extends AppCompatActivity {
    private String mListId;
    private DatabaseReference mEventListReference;
    private ImageView eventImage;
    private TextView bandName;
    private TextView checkOnMap;
    private ValueEventListener mEventListener;
    private DatabaseReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_list_details);

        initializeScreen();

        //get the push id from MusicEvents fragment
        Intent intent = this.getIntent();
        mListId = intent.getStringExtra(Constants.INTENT_KEY_ID);
        Log.d("id is", mListId);
        if (mListId == null) {
            /* No point in continuing without a valid ID. */
            finish();
            return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE);
        StorageReference ref = storageRef.child("images/" + mListId);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).into(eventImage);
            }
        });


        eventRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_EVENT_LISTS).child(mListId);
        mEventListener = eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EventList eventList = dataSnapshot.getValue(EventList.class);
                bandName.setText(eventList.getOwner());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        checkOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivityFromDetails.class);
                intent.putExtra(Constants.INTENT_KEY_ID_FROM_DETAILED_ACTIVITY, mListId);
                startActivity(intent);
            }
        });

    }

    private void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        eventImage = (ImageView) findViewById(R.id.eventImage);
        bandName = (TextView) findViewById(R.id.bandName);
        checkOnMap = (TextView) findViewById(R.id.check_on_map);


    }

}
