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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;

/**
 * Created by Mohs on 17/03/2018.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods fbMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;

    private Context mContext;
    private String email, password, username;
    private EditText mEmail, mPassword, mUsername;
    private TextView mSigningIn;
    private Button mRegisterButton;
    private ProgressBar mProgressBar;

    private String append = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;

        fbMethods = new FirebaseMethods(mContext);
        Log.d(TAG, "onCreate: started register");

        mProgressBar = (ProgressBar) findViewById(R.id.progressbarRegister);
        mSigningIn = (TextView) findViewById(R.id.textviewRegisterLoggingIn);
        mEmail = (EditText) findViewById((R.id.edittextRegisterEmail));
        mPassword = (EditText) findViewById((R.id.edittextRegisterPassword));
        mUsername = (EditText) findViewById((R.id.edittextRegisterUsername));
        mRegisterButton = (Button) findViewById(R.id.buttonRegister);

        // Invisible till user attempts to register
        mProgressBar.setVisibility(View.GONE);
        mSigningIn.setVisibility(View.GONE);

        setupFirebaseAuth();
        initialiseRegisterUser();
    }

    // Button which initialises resgistering
    private void initialiseRegisterUser(){
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to register user ");

                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                username = mUsername.getText().toString();

                if(checkIfStringNull(email) || checkIfStringNull(password) || checkIfStringNull(username)){
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    // If Fields are not empty, attempt registration and progress bar shows
                    mProgressBar.setVisibility(View.VISIBLE);
                    mSigningIn.setVisibility(View.VISIBLE);

                    fbMethods.newEmailRegister(email, password, username);

                }

            }
        });

    }



    // method to check if input fields are not null
    private boolean checkIfStringNull(String input){
        Log.d(TAG, "checkIfStringNull: checking if fields are null");

        if(input.equals("")){
            Log.d(TAG, "checkIfStringNull: fields are null");
            return true;
        } else {
            Log.d(TAG, "checkIfStringNull: fields are filled");
            return false;
        }
    }

    //------------------------FIRESBASE STUFF------------

    // checks if username is in db
    // using query means cant return anything (e.g. boolean if usename already exists)
    // Forced to run this and execute methods inside the override
    private void checkUsernameExist(final String username) {
        Log.d(TAG, "checkUsernameExist: Checking if this username exists already: " + username);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        // looks for node that contains object that is being lookexd for then gets field in that object
        Query qry = ref
                .child(getString(R.string.db_name_users))
                .orderByChild(getString(R.string.username_field))
                .equalTo(username);
        qry.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //loops through results
                // single snapshot as only one item from db is being returned
                for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                    if(singleDataSnapshot.exists()){
                        Log.d(TAG, "onDataChange: username already exists in db: " + singleDataSnapshot.getValue(User.class).getUsername());
                        // if username exists appends a random substring to username and adds to db
                        append = myDBRefFirebase.push().getKey().substring(3,10);
                        Log.d(TAG, "onDataChange: username already exists, appended user is: " + append);
                    }
                }

                String mUsername = "";

                mUsername = username + append; // if username is appended

                // add new user to db & user_account_settings to db
                fbMethods.addNewUser(email, mUsername, "", "", "");
                Log.d(TAG, "onDataChange: email: " + email + ", username = " + mUsername );

                Toast.makeText(mContext, "Successfully Signed Up, Email Verification Sent", Toast.LENGTH_SHORT).show();

                mAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: user signed in " + user.getUid());

                    // looks for single snapshot of fb db in its current state
                    myDBRefFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) { // shows when data is changed in db

                            checkUsernameExist(username);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { // shows when error

                        }
                    });
                    // closes activity
                    finish();
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
