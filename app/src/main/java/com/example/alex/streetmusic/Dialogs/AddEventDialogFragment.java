package com.example.alex.streetmusic.Dialogs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.streetmusic.Map.MapsActivity;
import com.example.alex.streetmusic.Map.MapsAddNewEvent;
import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.model.EventList;
import com.example.alex.streetmusic.model.Locations;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

;

/**
 * Created by Alex on 9/26/2016.
 */
public class AddEventDialogFragment extends DialogFragment{
    EditText mEditTextListName;
    private String mUserKey;
    static Button s;
    private double[] latLongFromMapAct;
    private Button selectEventLocation;
    public View rootView;
    public Button mSelectTime;
    public static TextView timePicked;
    private static String resultAdress;
    //private String resultAdressSavedInstance;
    private TextView adressFromMapsActivity;
    private Button takePicture;
    private Button selectPictureFromGallery;
    private ImageView selectedPicture;
    private static String textFromTimePicker;
    private Uri selectedImage;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ValueEventListener bandNameListener;
    private DatabaseReference bandNameReference;
    private ArrayList<String> existingBands;
    private Spinner spinnerPickBand;
    private int getCount;
    private String getBandNameFromSpinner;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddEventDialogFragment newInstance(String userKey) {
        AddEventDialogFragment addEventDialogFragment = new AddEventDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putString(Constants.KEY_USER_KEY, userKey);
        addEventDialogFragment.setArguments(bundle);
        return addEventDialogFragment;
    }
    //initialize variables
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserKey = getArguments().getString(Constants.KEY_USER_KEY);


    }

    /**
     * Open the keyboard automatically when the dialog fragment is opened
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //probably orientation change
            textFromTimePicker = savedInstanceState.getString("TextFromTimePicker");
            timePicked.setText(textFromTimePicker);

            if (adressFromMapsActivity != null)
                adressFromMapsActivity.setText(savedInstanceState.getString("resultAdress"));

            latLongFromMapAct = savedInstanceState.getDoubleArray("latLong");

            selectedImage = savedInstanceState.getParcelable("image");
            Log.d("Code", "did get here");
            if (selectedImage != null) {
                Picasso.with(getActivity()).load(selectedImage).into(selectedPicture);
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_add_event, null);
        initializeVariables(rootView);
        Log.d("onCreateDialog", "here");

        existingBands = new ArrayList<String>();

        bandNameReference = FirebaseDatabase.getInstance().getReference(
                Constants.FIREBASE_LOCATION_USERS).child(mUserKey).child(Constants.FIREBASE_LOCATION_BAND_LIST);
        bandNameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot bandNames : dataSnapshot.getChildren()) {

                    existingBands.add(bandNames.getKey());
                    Log.d("bandNameIs", bandNames.getKey());

                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        bandNameReference.addValueEventListener(bandNameListener);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, existingBands);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPickBand.setAdapter(adapter);



        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 1);//zero can be replaced with any action code
            }
        });

        selectPictureFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 2);//one can be replaced with any action code
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        builder.setView(rootView).setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
                addEventList();
                Log.d("addeventlist", "trigered");
            }
        });


        selectEventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MapsAddNewEvent.class);
                startActivityForResult(i, 0);
            }
        });
        //Maybe usefull later
//        mEditTextListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                    addEventList();
//                }
//                return true;
//            }
//        });


        return builder.create();

    }

    /**
     * Add new event to Firebase
     */
    public void addEventList() {

        getBandNameFromSpinner = spinnerPickBand.getSelectedItem().toString();
        Log.d("igotheresomehow", getBandNameFromSpinner);
        if (!getBandNameFromSpinner.equals("")){
            DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference newListRef = firebaseReference.push();

            final String listId = newListRef.getKey();

            DatabaseReference eventList = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_EVENT_LISTS).
                    child(listId);
            DatabaseReference location = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_LOCATIONS).
                    child(listId);
            DatabaseReference eventListInUser = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_USERS).
                    child(mUserKey).child(Constants.FIREBASE_LOCATION_BAND_LIST).child(getBandNameFromSpinner).
                    child(Constants.FIREBASE_LOCATION_EVENT_LISTS).child(listId);

            //make a hashmap to store the time when the event was added
            HashMap<String, Object> timestampCreated = new HashMap<>();
            timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);


            /* Build the new event list to be added to Firebase */
            EventList newEventList = new EventList(getBandNameFromSpinner, mUserKey, latLongFromMapAct[0], latLongFromMapAct[1],
                    timestampCreated, listId);


             /* Build the new locations to be added to Firebase */
            //Locations newLocations = new Locations(latLongFromMapAct[0], latLongFromMapAct[1], resultAdress);


            /* Add the event list to Firebase database */
            eventList.setValue(newEventList);
            //location.setValue(newLocations);
            eventListInUser.setValue(newEventList);

            StorageReference storageRef = storage.getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE);
            StorageReference imageRef = storageRef.child("images/").child(listId);
            UploadTask uploadTask = imageRef.putFile(selectedImage);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("Uploaded", "Successfully");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("Uploaded", "NotSuccessfully");
                }
            });


            /* Close the dialog fragment */
            AddEventDialogFragment.this.getDialog().cancel();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    latLongFromMapAct = data.getDoubleArrayExtra(Constants.INTENT_KEY_LAT_LONG);
                    resultAdress = data.getStringExtra(Constants.INTENT_KEY_RESULT_ADRESS);
                    Log.d("longitude is", String.valueOf(latLongFromMapAct[1]));
                    adressFromMapsActivity.setText(resultAdress);


                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    selectedImage = data.getData();
                    Picasso.with(getActivity()).load(selectedImage).into(selectedPicture);
                }
                break;
            case 2:
                if(resultCode == Activity.RESULT_OK){
                    selectedImage = data.getData();
                    Picasso.with(getActivity()).load(selectedImage).into(selectedPicture);
                }
                break;
            default:
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        // Saving variables
        savedInstanceState.putString("TextFromTimePicker", textFromTimePicker);
        savedInstanceState.putString("resultAdress", resultAdress);
        savedInstanceState.putDoubleArray("latLong", latLongFromMapAct);

        savedInstanceState.putParcelable("image", selectedImage);
        if (resultAdress != null)
            Log.d("resultAdress", resultAdress);

        // Call at the end
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initializeVariables(View rootView){

        timePicked = (TextView) rootView.findViewById(R.id.timePicked);
        adressFromMapsActivity = (TextView) rootView.findViewById(R.id.adress);
        selectEventLocation = (Button) rootView.findViewById(R.id.select_place);
        selectedPicture = (ImageView) rootView.findViewById(R.id.selected_iamge);
        takePicture = (Button) rootView.findViewById(R.id.take_picture);
        selectPictureFromGallery = (Button) rootView.findViewById(R.id.select_from_gallery);
        spinnerPickBand = (Spinner) rootView.findViewById(R.id.spinner_pick_band);
    }

}
