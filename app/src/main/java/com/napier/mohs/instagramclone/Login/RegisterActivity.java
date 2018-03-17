package com.napier.mohs.instagramclone.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.napier.mohs.instagramclone.R;

/**
 * Created by Mohs on 17/03/2018.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private String email;
    private String password;
    private String username;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mUsername;
    private TextView mSigningIn;
    private Button mRegisterButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: started register");
        mProgressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        mSigningIn = (TextView) findViewById(R.id.signingInText);
        mEmail = (EditText) findViewById((R.id.input_email));
        mPassword = (EditText) findViewById((R.id.input_password));
        mUsername = (EditText) findViewById((R.id.input_username));
        mContext = RegisterActivity.this;

        // Invisible till user attempts to register
        mProgressBar.setVisibility(View.GONE);
        mSigningIn.setVisibility(View.GONE);
    }


    // method to check if input fields are not null
    private boolean checkIfStringNull(String input){
        Log.d(TAG, "checkIfStringNull: checking if fields are null");

        if(input.equals("")){
            Log.d(TAG, "checkIfStringNull: fields are filled");
            return true;
        } else {
            Log.d(TAG, "checkIfStringNull: error fields are null");
            return false;
        }
    }

    //------------------------FIRESBASE STUFF------------
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: user signed in " + user);
                } else {
                    Log.d(TAG, "onAuthStateChanged: user signed out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
