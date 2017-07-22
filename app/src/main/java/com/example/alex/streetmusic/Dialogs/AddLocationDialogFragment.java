package com.example.alex.streetmusic.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.alex.streetmusic.Map.MapsActivity;
import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.model.Locations;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by vytuatus on 1/5/17.
 */

public class AddLocationDialogFragment extends DialogFragment {

    private String mUserKey;
    public View rootView;
    private static final String LOG_TAG = AddLocationDialogFragment.class.getSimpleName();
    private Button selectLocation;
    private double[] latLongFromMapAct;
    private String resultAdress;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddLocationDialogFragment newInstance(String userKey) {
        AddLocationDialogFragment addLocationDialogFragment = new AddLocationDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putString(Constants.KEY_USER_KEY, userKey);
        addLocationDialogFragment.setArguments(bundle);
        return addLocationDialogFragment;
    }
    //initialize variables
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserKey = getArguments().getString(Constants.KEY_USER_KEY);


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_add_location, null);
        initializeVariables(rootView);

        //button to open maps and select the location on the map.
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(i, 0);
                String time = "530pm";
                SimpleDateFormat dateFormat = new SimpleDateFormat("hmmaa");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa");
                try {
                    Date date = dateFormat.parse(time);

                    String out = dateFormat2.format(date);
                    Log.d("Time", out);
                } catch (ParseException e) {
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        builder.setView(rootView).setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Postive button cliecked", Toast.LENGTH_SHORT).show();
                addLocation();
                Log.d(LOG_TAG, "positveButonTriger");
            }
        });

        return builder.create();

    }

    private void addLocation() {
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newListRef = firebaseReference.push();

        //craete a uniqui push id so that each location is unique in Firebase
        final String listId = newListRef.getKey();

        //Create a reference where we can save the location with needed info
        DatabaseReference location = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_LOCATIONS).
                child(listId);

        //store time when the location was added. maybe it will be needed
        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

         /* Build the new locations to be added to Firebase */
        Locations newLocations = new Locations(latLongFromMapAct[0], latLongFromMapAct[1],
                resultAdress, timestampCreated);

        location.setValue(newLocations);
    }

    private void initializeVariables(View rootView) {
        selectLocation = (Button) rootView.findViewById(R.id.select_location);
    }

    //get the lat/long/adress data from the maps activity and use it to in addLocation() method
    //to save that info in Firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    latLongFromMapAct = data.getDoubleArrayExtra(Constants.INTENT_KEY_LAT_LONG);
                    resultAdress = data.getStringExtra(Constants.INTENT_KEY_RESULT_ADRESS);
                    Log.d("longitude is", String.valueOf(latLongFromMapAct[1]));
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
