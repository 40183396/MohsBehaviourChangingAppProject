package com.napier.mohs.behaviourchangeapp.Diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.BottomNavigationViewExSettings;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterSectionsPager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 24/03/2018.
 */

public class ActivityAddDiary extends AppCompatActivity {
    private static final String TAG = "ActivityAddDiary";

    private Context mContext = ActivityAddDiary.this;

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;

    private static final int ACTIVITY_NUMBER = 3;

    // widgets
    @BindView(R.id.containerViewPager)
    ViewPager mViewPager;
    @BindView(R.id.containerAddDiary)
    FrameLayout mFrameLayout;
    @BindView(R.id.relLayoutParentAddDiary)
    RelativeLayout mRelativeLayout;

    String dateIntent; // String retrieving date from diary
    FragmentAddWeights fragment = new FragmentAddWeights();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_add);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started diary activity");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();

        setupFirebaseAuth();

        // receiving date intent from FragmentDiary
        try {
            Intent intent = getIntent();
            dateIntent = intent.getStringExtra("date");
            Log.d(TAG, "onCreate: dateIntent " + dateIntent);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Exception " + e.getMessage() );
        }


        bottomNavbarSetup();
        setupViewPagerTabs();
    }

    // method to pass date to add to diary fragments
    public void passDateintent(String passDate){
        Log.d(TAG, "passDateintent: date passed " + passDate);

        // bundles the date
        Bundle bundle = new Bundle();
        bundle.putString("date", dateIntent);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack("fragment_add_weightsdiary");
        transaction.commit();
    }

    /*
    * Responsible for tabs weights and cardio
    * */
    private void setupViewPagerTabs(){
        passDateintent(dateIntent);
        AdapterSectionsPager adapter = new AdapterSectionsPager(getSupportFragmentManager());
        adapter.addFragment(fragment); // index 0
        // cardio taken out temporarily
       // adapter.addFragment(new FragmentAddCardio()); // index 1
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_weights);
       // tabLayout.getTabAt(1).setIcon(R.drawable.ic_cardio);
    }

    // setuup bottom navbar
    private void bottomNavbarSetup(){
        Log.d(TAG, "bottomNavbarSetup: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewExSettings.bottomNavigationViewExSetup(bottomNavigationViewEx);
        BottomNavigationViewExSettings.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER );
        menuItem.setChecked(true);
    }





    //------------------------FIREBASE STUFF------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: Firebase auth is being setup");
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


