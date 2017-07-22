package com.example.alex.streetmusic.Map;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class MapsAddNewEvent extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private Geocoder geocoder;
    private DatabaseReference locationRef;
    private ValueEventListener mLocationListener;
    private double longitudeFromSelectedLocation;
    private double latitudeFromSelectedLocation;
    private String adressFromSelectedLocation;
    private static final String LOG_TAG = MapsAddNewEvent.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_add_new_event);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());

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

        //Zoom in to the Vilnius Location
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.LAT_VILNIUS,
                Constants.LNG_VILNIUS), 10));

        mLocationListener = locationRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Locations location = postSnapshot.getValue(Locations.class);
                    Log.d(LOG_TAG, postSnapshot.getKey());
                    LatLng latLngFromDatabase = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLngFromDatabase).title(location.getAdress())
                            .snippet(postSnapshot.getKey())).setTag(location);
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
        //When the user clicks the info tab of a marker, save the selectedlocation lat/ling and adress
        //so that we can later pass info back to the calling AddEventDialogFragment. Then this info will
        //be saved in Firebase as part of the event information
        Locations location = (Locations) marker.getTag();
        latitudeFromSelectedLocation = location.getLatitude();
        longitudeFromSelectedLocation = location.getLongitude();
        adressFromSelectedLocation = location.getAdress();

        Intent i = new Intent(this, SelectEventTimeActivity.class);
        i.putExtra(Constants.INTENT_KEY_LOCATION_PUSH_ID, marker.getSnippet());
        startActivityForResult(i, 0);
    }

    public void onEventLocationPicked (View v){
        double[] latLong = new double[]{latitudeFromSelectedLocation, longitudeFromSelectedLocation};
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.INTENT_KEY_LAT_LONG, latLong);
        returnIntent.putExtra(Constants.INTENT_KEY_RESULT_ADRESS, adressFromSelectedLocation);
        Log.d("latitude is", String.valueOf(latitudeFromSelectedLocation));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    //Received time from SelectEventTimeActivity. We should save it in a variable
                    // and pass this back via return intent to AddEventDialogFragment

                }
                break;

            default:
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;
        }
    }
}
