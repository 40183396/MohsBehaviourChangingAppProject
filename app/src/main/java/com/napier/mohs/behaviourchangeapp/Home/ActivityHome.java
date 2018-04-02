package com.napier.mohs.behaviourchangeapp.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.behaviourchangeapp.Login.ActivityLogin;
import com.napier.mohs.behaviourchangeapp.Models.Photo;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.BottomNavigationViewHelper;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterMainFeedList;
import com.napier.mohs.behaviourchangeapp.Utils.SectionsPagerAdapter;
import com.napier.mohs.behaviourchangeapp.Utils.UniversalImageLoader;
import com.napier.mohs.behaviourchangeapp.Utils.FragmentViewComments;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityHome extends AppCompatActivity implements AdapterMainFeedList.OnItemsLoadMoreListener{


    // interface methods
    @Override
    public void onItemsLoadMore() {
        Log.d(TAG, "onItemsLoadMore: more photos being displayed");
        // FragmentHome is set up through view pager there is a different way to assign tag
        FragmentHome homeFragment = (FragmentHome)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpagerContainer
                        + ":" + mViewPager.getCurrentItem()); // references its tag
        if(homeFragment != null){
            homeFragment.displayMorePhotos(); // fragment will display more photos
        }
    }

    private static final String TAG = "ActivityHome";
    private static final int ACTIVITY_NUM = 0;
    private static final int FRAGMENT_HOME = 1; // 1 because it is middle tab
    private Context mContext = ActivityHome.this;

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private StorageReference mStorageRefFirebase;

    // widgets
    @BindView(R.id.viewpagerContainer) ViewPager mViewPager;
    @BindView(R.id.container) FrameLayout mFrameLayout;
    @BindView(R.id.relLayoutParent) RelativeLayout mRelativeLayout;

    // Strings
    @BindString(R.string.home_activity) String home_activity;
    @BindString(R.string.photo) String photo_extra;
    @BindString(R.string.fragment_viewcomments) String viewcomments_fragment;
    private String a = "activities";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: starting.");
        setupFirebaseAuth();

        // TODO Remove this
        /*DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.push().setValue(1);

        mDatabase.child("numbers").push().setValue(1);
        mDatabase.child("numbers").push().setValue(53);
        mDatabase.child("numbers").push().setValue(42);*/

        // make sure to initiliases image loader first
        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();
    }

    public void layoutHide(){
        Log.d(TAG, "layoutHide: hiding relative layout in activity home");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE); // FrameLayout is where comments fragments is going to be inserted into
    }

    public void layoutShow(){
        Log.d(TAG, "layoutShow: hiding frame layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE); // FrameLayout is where comments fragments is going to be inserted into
    }

    // overriding on back press method when we are navigating away from comments thread we hide frame layout for comments
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            layoutShow();
        }
    }

    // method to take home activity to comment thread
    public void onSelectedCommentThread(Photo photo, String callingActivity){
        Log.d(TAG, "onSelectedCommentThread: comment thread was selected");

        // bundles the user account settings and photo
        FragmentViewComments viewCommentsFragment = new FragmentViewComments();
        Bundle bundle = new Bundle();
        bundle.putParcelable(photo_extra, photo);
        bundle.putString(home_activity, home_activity);
        viewCommentsFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, viewCommentsFragment); // replace home container with this fragment
        transaction.addToBackStack(viewcomments_fragment);
        transaction.commit();
    }

    // initialises image loader here to be able to use in all other activities
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig()); // retrieves configuration
    }


    /*
    * Responsible for adding 3 tabs: Camera, Home, Messages
    * */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentCamera()); // index 0
        adapter.addFragment(new FragmentHome()); // index 1
        adapter.addFragment(new FragmentMessages()); // index 2
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
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
    private void currentUserCheck(FirebaseUser user){
        Log.d(TAG, "currentUserCheck:  check if a user has signed in");

        if(user == null){
            Intent intent = new Intent(mContext, ActivityLogin.class);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firebase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // checks for user signed in
                currentUserCheck(user);

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
        FirebaseUser user = mAuth.getCurrentUser();
        mViewPager.setCurrentItem(FRAGMENT_HOME); // displays home as default first view
        currentUserCheck(user);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
