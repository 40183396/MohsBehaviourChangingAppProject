package com.napier.mohs.behaviourchangeapp.Login;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.napier.mohs.behaviourchangeapp.Home.ActivityHome;
import com.napier.mohs.behaviourchangeapp.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * Created by Mohs on 17/03/2018.
 */

public class ActivityLogin extends AppCompatActivity {
    private static final String TAG = "ActivityLogin";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @BindView(R.id.edittextLoginEmail)
    EditText mEmail;
    @BindView(R.id.edittextLoginPassword)
    EditText mPassword;
    @BindView(R.id.textviewLoginSigningIn)
    TextView mSigningIn;
    @BindView(R.id.buttonLoginRegister)
    Button signUpLink;
    @BindView(R.id.progressbarLogin)
    ProgressBar mProgressBar;
    @BindView(R.id.buttonLogin)
    Button loginButton;

    private Context mContext;

    private String email, password, username;

    // Strings
    @BindString(R.string.error_invalid_password)
    String invalid_password;
    @BindString(R.string.error_invalid_email)
    String invalid_email;
    @BindString(R.string.error_field_required)
    String field_required;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this); // bind butterknife after

        Log.d(TAG, "onCreate: started login");

        mContext = ActivityLogin.this;

        // TODO REMOVE THIS LOGIN STUFF
        // quick log in
       // mEmail.setText("test1@test.com");
       // mPassword.setText("11111111");


        // This is invisible till user signs in
        mProgressBar.setVisibility(View.GONE);
        mSigningIn.setVisibility(View.GONE);
        setupFirebaseAuth();
        initialiseLoggingIn();
    }




    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private boolean isEmailValid(String email) {
        // TODO: Add More email checks here
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+");
    }

    //------------------------FIREBASE STUFF------------


    // Button which initialises logging in
    private void initialiseLoggingIn() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to log in");

                // Reset errors displayed in the form.
                mEmail.setError(null);
                mPassword.setError(null);

                email = mEmail.getText().toString();
                password = mPassword.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
                    mPassword.setError(invalid_password);
                    focusView = mPassword;
                    cancel = true;
                }

                // Check email field is filled or email is valid
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError(field_required);

                    focusView = mEmail;
                    cancel = true;
                } else if (!isEmailValid(email)) {
                    mEmail.setError(invalid_email);
                    focusView = mEmail;
                    cancel = true;
                }

                if (cancel) {
                    // error does not attempt log in
                    Toasty.error(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                    focusView.requestFocus();
                } else {
                    // If Fields are not empty, attempt a log in and progress bar shows
                    mProgressBar.setVisibility(View.VISIBLE);
                    mSigningIn.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                                        Toasty.error(ActivityLogin.this, getString(R.string.failed_auth),
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        mSigningIn.setVisibility(View.GONE);
                                    } else {
                                        Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish(); // ending the stack so can't go back to login from home
                                        Toasty.success(mContext, "Success!", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        mSigningIn.setVisibility(View.GONE);
                                    }

                                }
                            });
                }

            }
        });


        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going to register activity");
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(intent);
            }
        });
        // navigates to home activity if user is logged in
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
            startActivity(intent);
            // closes login activity
            finish();
        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
