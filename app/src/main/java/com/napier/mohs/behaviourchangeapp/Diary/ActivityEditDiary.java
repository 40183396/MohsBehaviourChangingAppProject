package com.napier.mohs.behaviourchangeapp.Diary;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.MethodsFirebase;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterSectionsPager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 04/04/2018.
 */

public class ActivityEditDiary extends AppCompatActivity {
    private static final String TAG = "ActivityEditDiary";

    private Context mContext = ActivityEditDiary.this;
    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private MethodsFirebase mMethodsFirebase;

    // widgets
    @BindView(R.id.containerViewPager)
    ViewPager mViewPager;
    @BindView(R.id.containerEditDiary)
    FrameLayout mFrameLayout;
    @BindView(R.id.relLayoutParentEditDiary)
    RelativeLayout mRelativeLayout;


    FragmentEditWeights fragment = new FragmentEditWeights();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_edit);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started diary activity");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mMethodsFirebase = new MethodsFirebase(mContext);

        setupFirebaseAuth();



        setupViewPagerTabs();
    }

    // method to receive bundle from diary
    public void retrieveBundle(){

        fragment.setArguments(getIntent().getExtras());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack("fragment_edit_weightsdiary");
        transaction.commit();
    }

    /*
    * Responsible for displaying edit weights fragment
    * */
    private void setupViewPagerTabs(){
        retrieveBundle();
        AdapterSectionsPager adapter = new AdapterSectionsPager(getSupportFragmentManager());
        adapter.addFragment(fragment); // index 0
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_weights);
    }



    //------------------------FIREBASE STUFF------------
    // Method to check if a user is signed in app

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

        // allows to get datasnapshot and allows to read or write to db
        myDBRefFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}




