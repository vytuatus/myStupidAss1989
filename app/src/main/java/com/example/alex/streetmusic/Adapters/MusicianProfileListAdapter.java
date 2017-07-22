package com.example.alex.streetmusic.Adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.model.Band;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * Created by vytuatus on 12/28/16.
 */

public class MusicianProfileListAdapter extends FirebaseListAdapter<Band> {

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public MusicianProfileListAdapter(Activity activity, Class<Band> modelClass, int modelLayout,
                             Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }



    /**
     * Protected method that populates the view attached to the adapter (list_view_active_lists)
     * with items inflated from single_active_list.xml
     * populateView also handles data changes and updates the listView accordingly
     */
    @Override
    protected void populateView(View view, Band band, int position) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE);
        StorageReference ref = storageRef.child("images/" + band.getBandName());
        long ONE_MEGABYTE = 1024 * 1024;
        /**
         * Grab the needed Textivews and strings
         */
        TextView bandDescription = (TextView) view.findViewById(R.id.band_description_text_view_music_prof);
        TextView bandName = (TextView) view.findViewById(R.id.band_name_text_view_music_prof);
        final ImageView bandImage = (ImageView) view.findViewById(R.id.band_image_musician_profile_list);

        /* Set the list name and owner */
        bandDescription.setText(band.getBandDescription());
        bandName.setText(band.getBandName());


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(mActivity).load(uri).into(bandImage);
            }
        });



    }

}
