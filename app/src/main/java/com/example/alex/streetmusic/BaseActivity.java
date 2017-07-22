package com.example.alex.streetmusic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alex.streetmusic.SignIn.CreateUser;
import com.example.alex.streetmusic.SignIn.LogIn;
import com.example.alex.streetmusic.Utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Alex on 9/24/2016.
 */
public class BaseActivity extends AppCompatActivity {

    protected String mUserKey;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting accesss to shared preferences to use it to get access to some User Identification key
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserKey = sp.getString(Constants.KEY_USER_UID, null);



        if (!((this instanceof LogIn) || (this instanceof CreateUser))) {
            mAuth = FirebaseAuth.getInstance();
            Log.d("if statement", "changed");
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth authData) {

                    FirebaseUser user = authData.getCurrentUser();
                    //The user has been loged out
                    if (user == null) {
                        //Clear out shared preferences
                        SharedPreferences.Editor spe = sp.edit();
                        spe.putString(Constants.KEY_USER_UID, null);

                        takeUserToLoginScreenOnUnAuth();
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
            Log.d("listener", "added");
        }
    }

    private void takeUserToLoginScreenOnUnAuth(){
        //move user to login activity, and remove backstack
        Intent intent = new Intent(BaseActivity.this, LogIn.class);
        //intent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String iamthebest = "parmezan";

        if (id == android.R.id.home) {
            //super.onBackPressed();
            return true;
        }

        if (id == R.id.action_logout){
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void logout(){
        mAuth.getInstance().signOut();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!((this instanceof LogIn) || (this instanceof CreateUser)))
            mAuth.removeAuthStateListener(mAuthListener);
    }



}
