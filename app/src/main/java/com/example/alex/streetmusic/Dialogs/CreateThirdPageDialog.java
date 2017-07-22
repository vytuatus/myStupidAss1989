package com.example.alex.streetmusic.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.ViewPagerFragments.MusicianProfile;
import com.example.alex.streetmusic.ViewPagerFragments.ViewPagerHandler;
import com.example.alex.streetmusic.model.Band;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by vytuatus on 12/1/16.
 */

public class CreateThirdPageDialog extends DialogFragment {

    private View rootView;
    private TextView next;
    private TextView previous;
    private EditText bandDescription;
    private Uri imageUri;
    private SharedPreferences myPrefs;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String LOG_TAG = CreateThirdPageDialog.class.getSimpleName();
    private ArrayList<String> musicStyleList;

    public static CreateThirdPageDialog newInstance(Uri imageUri) {
        CreateThirdPageDialog createThirdPageDialog = new CreateThirdPageDialog();
        Bundle bundle = new Bundle();

        bundle.putParcelable(Constants.KEY_IMAGE_URI, imageUri);
        createThirdPageDialog.setArguments(bundle);
        return createThirdPageDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUri = getArguments().getParcelable(Constants.KEY_IMAGE_URI);
        myPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        musicStyleList = new ArrayList<String>(myPrefs.getStringSet(Constants.KEY_MUSIC_STYLE, null));

    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.create_third_page_dialogfragment, null);
        initializeVariables(rootView);


        bandDescription.setText(myPrefs.getString(Constants.KEY_SAVE_BAND_DESCRIPTION, "default"));

        final SharedPreferences.Editor myPrefsEditor = myPrefs.edit();

        bandDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){
                    myPrefsEditor.putString(Constants.KEY_SAVE_BAND_DESCRIPTION,
                            bandDescription.getText().toString()).apply();
                }
                return false;
            }
        });

        //
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = CreateSecondPageDialog.newInstance(imageUri);
                dialog.show(CreateThirdPageDialog.this.getFragmentManager(), "CreateFirstPageDialog");
                dismiss();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Not sure where to go next", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ViewPagerHandler.class);

                //Extract needed info from Prefs to store it in the Band object
                String userId = myPrefs.getString(Constants.KEY_USER_UID, null);
                String bandName = myPrefs.getString(Constants.KEY_WRITTEN_BAND_NAME, null);
                String bandDescription = myPrefs.getString(Constants.KEY_SAVE_BAND_DESCRIPTION, null);



                //Save band info. band description and band name
                DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference(
                        Constants.FIREBASE_LOCATION_USERS).child(userId).child(Constants.FIREBASE_LOCATION_BAND_LIST).
                        child(bandName);

                //make a hashmap to store the time when the event was added
                HashMap<String, Object> timestampCreated = new HashMap<>();
                timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                Band band = new Band(bandName, bandDescription, musicStyleList, timestampCreated);

                firebaseReference.setValue(band);

                //Save Band picture
                StorageReference storageRef = storage.getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE);
                StorageReference imageRef = storageRef.child("images/").child(bandName);
                UploadTask uploadTask = imageRef.putFile(imageUri);
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



                startActivity(intent);
                dismiss();

            }
        });


        Dialog d = new Dialog(getActivity(), R.style.FirstPageDialog);
        d.setContentView(rootView);


        d.show();


        return d;

    }

    private void initializeVariables(View rootView){
        next = (TextView) rootView.findViewById(R.id.third_page_next);
        previous = (TextView) rootView.findViewById(R.id.third_page_previous);
        bandDescription = (EditText) rootView.findViewById(R.id.editText_thirdpage_add_band_description);


    }


}
