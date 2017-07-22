package com.example.alex.streetmusic.Adapters;

import android.app.Activity;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.model.Locations;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by vytuatus on 1/4/17.
 */

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mContentsView;
    private Activity mActivity;

    public MyInfoWindowAdapter(Activity activity){
        this.mActivity = activity;
        LayoutInflater inflater = mActivity.getLayoutInflater();
        mContentsView = inflater.inflate(R.layout.custom_info_window_layout, null);
    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView tvTitle = ((TextView) mContentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle());
        TextView tvSnippet = ((TextView) mContentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet());
        Locations location = (Locations) marker.getTag();
        Toast.makeText(mActivity, String.valueOf(location.getAdress()), Toast.LENGTH_SHORT).show();

        return mContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }


}
