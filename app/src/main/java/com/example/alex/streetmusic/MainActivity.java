package com.example.alex.streetmusic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.alex.streetmusic.Dialogs.AddEventDialogFragment;
import com.example.alex.streetmusic.Dialogs.CreateFirstPageDialog;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.ViewPagerFragments.ViewPagerHandler;

public class MainActivity extends BaseActivity {

    private Button goToMusicEvents;
    private Button goToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeScreen();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializeScreen(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        goToMusicEvents = (Button) findViewById(R.id.go_to_music_events);
        goToProfile = (Button) findViewById(R.id.go_to_profile);
    }

    public void goToMusicEvents (View view){
        Intent intent = new Intent(this, MusicEventHolder.class);
        startActivity(intent);
    }

    public void goToProfile (View view){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean hasBand = sp.getBoolean(Constants.KEY_HAS_BAND, false);

        if (hasBand){

            Intent intent = new Intent(this, ViewPagerHandler.class);
            startActivity(intent);

        }else{
            Uri Peter = null;
            DialogFragment dialog = CreateFirstPageDialog.newInstance(Peter);
            dialog.show(this.getSupportFragmentManager(), "CreateFirstPageDialog");

        }

    }

}
