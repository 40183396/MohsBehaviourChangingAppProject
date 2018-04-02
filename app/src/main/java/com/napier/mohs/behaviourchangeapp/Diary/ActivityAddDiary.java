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
import com.napier.mohs.behaviourchangeapp.Utils.BottomNavigationViewHelper;
import com.napier.mohs.behaviourchangeapp.Utils.FirebaseMethods;
import com.napier.mohs.behaviourchangeapp.Utils.SectionsPagerAdapter;

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
    private FirebaseMethods mFirebaseMethods;

    private static final int FRAGMENT_ADD = 1;
    private static final int ACTIVITY_NUM = 3;

    // widgets
    @BindView(R.id.viewpagerContainer)
    ViewPager mViewPager;
    @BindView(R.id.containerAddDiary)
    FrameLayout mFrameLayout;
    @BindView(R.id.relLayoutParentAddDiary)
    RelativeLayout mRelativeLayout;

    String dateIntent;
    FragmentAddWeights fragment = new FragmentAddWeights();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started diary activity");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(mContext);
        ///addEntryToDB();
        setupFirebaseAuth();

        // receiving date intent from FragmentDiary
        try {
            Intent intent = getIntent();
            dateIntent = intent.getStringExtra("date");
            Log.d(TAG, "onCreate: dateIntent " + dateIntent);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Exception " + e.getMessage() );
        }


        setupBottomNavigationView();
        setupViewPager();
    }

    // method to take home activity to comment thread
    public void passDateintent(String passDate){
        Log.d(TAG, "passDateintent: date passed " + passDate);

        // bundles the user account settings and photo

        Bundle bundle = new Bundle();
        bundle.putString("date", dateIntent);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       // transaction.replace(R.id.containerAddDiary, fragment); // replace home container with this fragment
        transaction.addToBackStack("fragment_add_weightsdiary");
        transaction.commit();
    }

    /*
    * Responsible for adding 3 tabs: Camera, Home, Messages
    * */
    private void setupViewPager(){
        passDateintent(dateIntent);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragment); // index 0
        adapter.addFragment(new FragmentAddCardio()); // index 1
        adapter.addFragment(new FragmentAddGoals()); // index 2
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_weights);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_cardio);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_goals);
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM );
        menuItem.setChecked(true);
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


