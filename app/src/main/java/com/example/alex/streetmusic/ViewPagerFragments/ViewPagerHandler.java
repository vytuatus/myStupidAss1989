package com.example.alex.streetmusic.ViewPagerFragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.alex.streetmusic.Dialogs.AddEventDialogFragment;
import com.example.alex.streetmusic.Dialogs.AddLocationDialogFragment;
import com.example.alex.streetmusic.Dialogs.TimePickerFragment;
import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;

public class ViewPagerHandler extends AppCompatActivity {

    private String mUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_handler);
        initializeScreen();
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserKey = sp.getString(Constants.KEY_USER_UID, null);

    }

    //Opens a dialog in which you can select a music event's one of the predefined locations, time, ect.
    public void showAddEventDialog(View view){
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = AddEventDialogFragment.newInstance(mUserKey);
        dialog.show(ViewPagerHandler.this.getSupportFragmentManager(), "AddEventDialogFragment");
    }

    //Opens a dialog where a developer can set a new location if needed for the user to pick from
    public void showAddLocationDialog(View view){
        /*Create an instance of the dialog fragment and show it */
        DialogFragment dialog = AddLocationDialogFragment.newInstance(mUserKey);
        dialog.show(ViewPagerHandler.this.getSupportFragmentManager(), "AddLocationDialogFragment");

    }

//    public void showTimePickerDialog (View v){
//        DialogFragment timePickerDialog = TimePickerFragment.newInstance();
//        timePickerDialog.show(ViewPagerHandler.this.getSupportFragmentManager(), "timePicker");
//    }


    public void initializeScreen(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_view_pager_toolbar);
        setSupportActionBar(toolbar);

        //creating the sectionPagerAdapter which is responsible for controlling how pages are displayed and how many
        SectionPagerAdapte adapter = new SectionPagerAdapte(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        /**
         * Setup the mTabLayout with view pager
         */
        tabLayout.setupWithViewPager(viewPager);
    }

    public class SectionPagerAdapte extends FragmentStatePagerAdapter {

        public SectionPagerAdapte(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            //set fragment to different fragments depending on the position in ViewPager
            /**
             * Set fragment to different fragments depending on position in ViewPager
             */
            switch (position) {
                case 0:
                    MusicianProfile fragment1 = new MusicianProfile();
                    return fragment1;
                case 1:
                    MusicEvents fragment2 = new MusicEvents();
                    return fragment2;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }
        /**
         * Set string resources as titles for each fragment by it's position
         *
         * @param position
         */
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0:
                    return getString(R.string.musician_profile);
                case 1:
                default:
                    return getString(R.string.music_events);
            }
        }
    }

}
