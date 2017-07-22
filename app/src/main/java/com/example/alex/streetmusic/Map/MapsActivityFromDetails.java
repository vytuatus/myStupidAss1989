package com.example.alex.streetmusic.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.alex.streetmusic.Adapters.MyInfoWindowAdapter;
import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.model.Locations;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MapsActivityFromDetails extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private LatLng latLng;
    private Marker marker;
    Geocoder geocoder;
    private String mUserKey;
    private double longitude;
    private double latitude;
    private double[] latLongFromPreviousPick;
    private Marker previouslySelectedMarker;
    private ValueEventListener mLocationListener;
    private StringBuilder sb;
    private DatabaseReference locationRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity_from_details);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this, Locale.getDefault());
        mUserKey = getIntent().getStringExtra(Constants.INTENT_KEY_ID_FROM_DETAILED_ACTIVITY);

        locationRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_LOCATIONS);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationListener = locationRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Locations location = postSnapshot.getValue(Locations.class);
                    LatLng latLngFromDatabase = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLngFromDatabase).title(location.getAdress())
                            .snippet("Suck my dick")).setTag(location);
                    Log.e("GetData", String.valueOf(location.getLatitude()));
                    Log.d("Igothere", postSnapshot.getKey());
                    Log.d("IgothereToo", mUserKey);

                    if (postSnapshot.getKey().equals(mUserKey)){

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFromDatabase, 15));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.setOnInfoWindowClickListener(this);

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

}
