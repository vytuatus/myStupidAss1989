package com.example.alex.streetmusic.SignIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alex.streetmusic.BaseActivity;
import com.example.alex.streetmusic.MainActivity;
import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Alex on 9/24/2016.
 */
public class CreateUser extends BaseActivity {
    private static final String LOG_TAG = CreateUser.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextUserCreate, mEditTextEmailCreate, mEditTextPasswordCreate;
    private String mUserName, mUserEmail, mPassword;
    private DatabaseReference mFirebaseDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        initializeScreen();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    String Uid = user.getUid();
                    final DatabaseReference userLocation = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_USERS)
                            .child(Uid);
                    //Take to the main Activity
                    Intent intent = new Intent(CreateUser.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    /**
                     * See if there is already a user (for example, if they already logged in with an associated
                     * Google account.
                     */
                    userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                        //if there is no user, make one
                            if (dataSnapshot.getValue() == null){
                                HashMap<String, Object> timestampJoined = new HashMap<String, Object>();
                                timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                                User newUser = new User(mUserName, mUserEmail,timestampJoined);
                                userLocation.setValue(newUser);

                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor spe = sp.edit();
                                spe.putBoolean(Constants.KEY_HAS_BAND, false).apply();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOG_TAG, databaseError.getMessage());
                        }
                    });
                }else{
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


    }

    public void initializeScreen() {
        mEditTextUserCreate = (EditText) findViewById(R.id.edit_text_username_create);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);
        mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_password_create);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setCancelable(false);
    }
    //Open logIn activity when user taps on "Sign In" Textview
    public void onSignInPressed (View view){
        Intent intent = new Intent(CreateUser.this, LogIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onCreateAccountPressed(View view) {

        mUserName = mEditTextUserCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString();
        mPassword = mEditTextPasswordCreate.getText().toString();

        boolean validUserName = isUserNameValid(mUserName);
        boolean validEmail =  isEmailValid(mUserEmail);
        boolean validPassword = isPasswordValid(mPassword);
        if (!validEmail || !validUserName || !validPassword) return;

        mAuthProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(mUserEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {

                        } else {
                            Log.d(LOG_TAG, "createUserWithEmail:Failed:" + task.getException());
                            Toast.makeText(CreateUser.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        mAuthProgressDialog.dismiss();
                    }
                });
    }

    private boolean isEmailValid(String email){
        boolean emailIsGood = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!emailIsGood){
            mEditTextEmailCreate.setError(String.format(getString(R.string.error_invalid_email_not_valid), email));
            return false;
        }
        return emailIsGood;
    }

    private boolean isUserNameValid(String userName){
        if (userName.equals("")){
            mEditTextUserCreate.setError(getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password){
        if (password.length() < 8){
            mEditTextPasswordCreate.setError(getString(R.string.error_password_too_short));
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

}
