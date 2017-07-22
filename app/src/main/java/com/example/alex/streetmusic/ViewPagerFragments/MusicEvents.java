package com.example.alex.streetmusic.ViewPagerFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.streetmusic.ActiveListDetailsActivity;
import com.example.alex.streetmusic.Map.MapsActivityFromDetails;
import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.model.EventList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * Created by Alex on 9/19/2016.
 */
public class MusicEvents extends Fragment {

    //View Holder that will be used in FirebaseRecyclerView
    public static class EventListViewHolder extends RecyclerView.ViewHolder {

        //Variable for the Click Listener
        private EventListViewHolder.ClickListener mClickListener;

        //Interface to send Callbacks...
        public interface ClickListener{
            public void onItemClick(View view, int position);
        }

        public void setOnClickListener(EventListViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }

        TextView textViewListName;
        TextView textViewCreatedByUser;
        TextView checkOnMap;
        ImageView listImage;

        public EventListViewHolder(View view) {
            super(view);

            textViewListName = (TextView) view.findViewById(R.id.text_view_list_name);
            textViewCreatedByUser = (TextView) view.findViewById(R.id.created_by);
            checkOnMap = (TextView) view.findViewById(R.id.check_on_map_adapter);
            listImage = (ImageView) view.findViewById(R.id.listImage);

            //listener that we set on the entire row
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    //Constants used when deciding on the Layout
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private DatabaseReference mDatabaseEventListReference;
    private FirebaseRecyclerAdapter<EventList, EventListViewHolder> mFirebaseAdapter;
    private String eventListId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Initialize UI elements
         */
        View rootView = inflater.inflate(R.layout.fragment_music_event_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycle_view_music_event_lists);

        //use the LinearLayout for RecyclerView
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //Setting this will make performance improvements if changes in the content will not change
        //the size of the layout
        mRecyclerView.setHasFixedSize(true);

        //Set the database refence to the root of the database
        mDatabaseEventListReference = FirebaseDatabase.getInstance().getReference();

        //Set up the adapter for reading the events in database and display them to user
        mFirebaseAdapter = new FirebaseRecyclerAdapter<EventList, EventListViewHolder>(
                EventList.class,
                R.layout.single_active_list,
                EventListViewHolder.class,
                mDatabaseEventListReference.child(Constants.FIREBASE_LOCATION_EVENT_LISTS)) {

            //This sheize we need so that we can implement the click listener
            @Override
            public EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                EventListViewHolder eventListViewHolder = super.onCreateViewHolder(parent, viewType);
                eventListViewHolder.setOnClickListener(new EventListViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        EventList selectedEventList = mFirebaseAdapter.getItem(position);
                        if (selectedEventList != null) {
                            Intent intent = new Intent(getActivity(), ActiveListDetailsActivity.class);
                            String listId = mFirebaseAdapter.getRef(position).getKey();
                            intent.putExtra(Constants.INTENT_KEY_ID, listId);
                            startActivity(intent);
                        }
                    }
                });

                return eventListViewHolder;
            }

            @Override
            protected void populateViewHolder(final EventListViewHolder viewHolder, EventList list, int position) {
                DatabaseReference locationReference = FirebaseDatabase.getInstance().getReference(
                        Constants.FIREBASE_LOCATION_EVENT_LISTS).child(list.getListId()).child("latitude");
                eventListId = list.getListId();

                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE);
                StorageReference ref = storageRef.child("images/" + list.getListId());
                long ONE_MEGABYTE = 1024 * 1024;


                /* Set the list name and owner */
                viewHolder.textViewListName.setText(list.getListName());
                viewHolder.textViewCreatedByUser.setText(list.getOwner());

                //get the image and populate it in the listview
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getActivity()).load(uri).into(viewHolder.listImage);
                    }
                });

                //place an onclicklistener which would pass on latitude/longitute to the MapsActivity,
                // which would show the location on the map
                viewHolder.checkOnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MapsActivityFromDetails.class);
                        intent.putExtra(Constants.INTENT_KEY_ID_FROM_DETAILED_ACTIVITY, eventListId);
                        startActivity(intent);
                    }
                });


            }
        };

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mFirebaseAdapter);

        return rootView;
    }



}

