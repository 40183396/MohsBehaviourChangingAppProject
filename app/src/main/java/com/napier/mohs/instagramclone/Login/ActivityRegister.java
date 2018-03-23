package com.napier.mohs.instagramclone.Login;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * Created by Mohs on 17/03/2018.
 */

public class ActivityRegister extends AppCompatActivity {
    private static final String TAG = "ActivityRegister";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods fbMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;

    private Context mContext;
    private String email, password, username;
    private String append = "";

    @BindView(R.id.textviewRegisterLoggingIn) TextView mSigningIn;
    @BindView(R.id.edittextRegisterEmail) EditText mEmail;
    @BindView(R.id.edittextRegisterPassword) EditText mPassword;
    @BindView(R.id.edittextRegisterUsername) EditText mUsername;
    @BindView(R.id.progressbarRegister) ProgressBar mProgressBar;
    @BindView(R.id.buttonRegister) Button mRegisterButton;

    // Strings
    @BindString(R.string.error_invalid_password) String invalid_password;
    @BindString(R.string.error_invalid_email) String invalid_email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mContext = ActivityRegister.this;

        fbMethods = new FirebaseMethods(mContext);
        Log.d(TAG, "onCreate: started register");

        // Invisible till user attempts to register
        mProgressBar.setVisibility(View.GONE);
        mSigningIn.setVisibility(View.GONE);

        setupFirebaseAuth();
        initialiseRegisterUser();
    }

    // Button which initialises registering
    private void initialiseRegisterUser(){
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to register user ");
                // Reset errors displayed in the form.
                mEmail.setError(null);
                mPassword.setError(null);

                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                username = mUsername.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
                    mPassword.setError(getString(R.string.error_invalid_password));
                    focusView = mPassword;
                    cancel = true;
                }

                // Check email field is filled or email is valid
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError(getString(R.string.error_field_required));
                    focusView = mEmail;
                    cancel = true;
                } else if (!isEmailValid(email)) {
                    mEmail.setError(getString(R.string.error_invalid_email));
                    focusView = mEmail;
                    cancel = true;
                }

                // check if username field is filled
                if (TextUtils.isEmpty(username)) {
                    mUsername.setError(getString(R.string.error_field_required));
                    focusView = mUsername;
                    cancel = true;
                }

                if(cancel){
                    // error does not attempt log in
                    Toasty.error(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                    focusView.requestFocus();
                } else {
                    // If Fields are not empty, attempt registration and progress bar shows
                    //TODO Figure out how to get progress bar to disappear
                    //mProgressBar.setVisibility(View.VISIBLE);
                    //mSigningIn.setVisibility(View.VISIBLE);
                    new StyleableToast
                            .Builder(mContext)
                            .text("Registering...")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.BLUE)
                            .show();
                    fbMethods.newEmailRegister(email, password, username);

                }

            }
        });

    }

    private boolean isPasswordValid(String password) {
        //TODO: Add another confirm password field
        //String confirmPassword = mConfirmPasswordView.getText().toString();

        return password.length() > 6;
    }

    private boolean isEmailValid(String email) {
        // TODO: Add More email checks here
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+");
    }


    //------------------------FIREBASE STUFF------------

    // checks if username is in db
    // using query means cant return anything (e.g. boolean if usename already exists)
    // Forced to run this and execute methods inside the override
    private void checkUsernameExist(final String username) {
        Log.d(TAG, "checkUsernameExist: Checking if this username exists already: " + username);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        // looks for node that contains object that is being looked for then gets field in that object
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

                // signs user out after registering
                mAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firebase auth is being setup");
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
