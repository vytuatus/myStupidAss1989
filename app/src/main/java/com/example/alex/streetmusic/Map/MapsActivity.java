package com.example.alex.streetmusic.Map;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
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
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        mUserKey = getIntent().getStringExtra("key");
        Toast.makeText(MapsActivity.this, mUserKey, Toast.LENGTH_SHORT).show();
        latLongFromPreviousPick = getIntent().getDoubleArrayExtra(Constants.INTENT_KEY_PREVIOUS_LOCATION);

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
        // Add a marker in Sydney and move the camera
        if  (latLongFromPreviousPick != null) {
            LatLng previousPickedLocation = new LatLng(latLongFromPreviousPick[0], latLongFromPreviousPick[1]);

            previouslySelectedMarker = mMap.addMarker(new MarkerOptions().position(previousPickedLocation).title("Marker Bitch"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(previousPickedLocation));
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //save current location
                if (previouslySelectedMarker != null){previouslySelectedMarker.remove();}
                latLng = point;
                latitude = point.latitude;
                longitude = point.longitude;

                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                android.location.Address address = addresses.get(0);

                if (address != null) {
                    sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i) + "\n");
                    }
                    Toast.makeText(MapsActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                }

                //remove previously placed Marker
                if (marker != null) {
                    marker.remove();
                }

                //place marker where user just clicked
                marker = mMap.addMarker(new MarkerOptions().position(point).title(sb.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));

            }
        });

        mLocationListener = locationRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Locations location = postSnapshot.getValue(Locations.class);
                    LatLng latLngFromDatabase = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLngFromDatabase).title(location.getAdress())
                            .snippet("Suck my dick")).setTag(location);
                    Log.e("GetData", String.valueOf(location.getLatitude()));
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

    public void onLocationPicked (View v){
        double[] latLong = new double[]{latitude, longitude};
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.INTENT_KEY_LAT_LONG, latLong);
        returnIntent.putExtra(Constants.INTENT_KEY_RESULT_ADRESS, sb.toString());
        Log.d("latitude is", String.valueOf(latitude));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
