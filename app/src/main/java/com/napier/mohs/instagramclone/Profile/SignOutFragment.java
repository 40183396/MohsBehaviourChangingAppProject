package com.napier.mohs.instagramclone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.napier.mohs.instagramclone.Login.LoginActivity;
import com.napier.mohs.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by User on 6/4/2017.
 */

public class SignOutFragment extends Fragment {

    private static final String TAG = "SignOutFragment";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @BindView(R.id.progressbarSignOutConfirm) ProgressBar mProgressBar;
    @BindView(R.id.textviewSignOutConfirm) TextView mTextViewSignOut;
    @BindView(R.id.textviewSigningOut) TextView mTextViewSigningOut;
    @BindView(R.id.buttonSignOutConfirm)Button mSignOutButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);
        ButterKnife.bind(this, view);

        mProgressBar.setVisibility(View.GONE);
        mTextViewSigningOut.setVisibility(View.GONE);

        setupFirebaseAuth();

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting sign out");
                mProgressBar.setVisibility(View.VISIBLE);
                mTextViewSigningOut.setVisibility(View.VISIBLE);
                mAuth.signOut();
                // ends activity after logging out
                getActivity().finish();
            }
        });
        return view;
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
                    // signs out and goes back to log in activity
                    Log.d(TAG, "onAuthStateChanged: navigating to log in screen");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    // closes stack so when user logs out the user can no longer access home page
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


