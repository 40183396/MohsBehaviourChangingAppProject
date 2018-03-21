package com.napier.mohs.instagramclone.Home;

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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.instagramclone.Login.LoginActivity;
import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;
import com.napier.mohs.instagramclone.Utils.MainFeedListAdapter;
import com.napier.mohs.instagramclone.Utils.SectionsPagerAdapter;
import com.napier.mohs.instagramclone.Utils.UniversalImageLoader;
import com.napier.mohs.instagramclone.Utils.ViewCommentsFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity implements MainFeedListAdapter.OnItemsLoadMoreListener{


    // interface methods
    @Override
    public void onItemsLoadMore() {
        Log.d(TAG, "onItemsLoadMore: more photos being displayed");
        // HomeFragment is set up through view pager there is a different way to assign tag
        HomeFragment homeFragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpagerContainer + ":" + mViewPager.getCurrentItem()); // references its tag
        if(homeFragment != null){
            homeFragment.displayMorePhotos(); // fragment will display more photos
        }
    }

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int FRAGMENT_HOME = 1; // 1 because it is middle tab
    private Context mContext = HomeActivity.this;

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");
        mViewPager = (ViewPager) findViewById(R.id.viewpagerContainer);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);


        setupFirebaseAuth();
        // make sure to initiliases image loader first
        initImageLoader();



        setupBottomNavigationView();
        setupViewPager();
    }

    public void layoutHide(){
        Log.d(TAG, "layoutHide: hiding relative layout in activity home");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE); // FrameLayout is where comments fragments is going to be insereted into
    }

    public void layoutShow(){
        Log.d(TAG, "layoutShow: hiding frame layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE); // FrameLayout is where comments fragments is going to be insereted into
    }

    // overriding on back press method when we are navigating away from comments thread we hide fram layout
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            layoutShow();
        }
    }

    // method to take homae activity to comment thread
    public void onSelectedCommentThread(Photo photo, String callingActivity){
        Log.d(TAG, "onSelectedCommentThread: comment thread was selected");

        // bundles the user account settings and photo
        ViewCommentsFragment viewCommentsFragment = new ViewCommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.photo), photo);
        bundle.putString(getString(R.string.activity_number), getString(R.string.activity_number));
        viewCommentsFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, viewCommentsFragment); // replace home container with this fragment
        fragmentTransaction.addToBackStack(getString(R.string.fragment_viewcomments));
        fragmentTransaction.commit();
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
        adapter.addFragment(new CameraFragment()); // index 0
        adapter.addFragment(new HomeFragment()); // index 1
        adapter.addFragment(new MessagesFragment()); // index 2
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






    //------------------------FIRESBASE STUFF------------
    // Method to check if a user is signed in app
    private void currentUserCheck(FirebaseUser user){
        Log.d(TAG, "currentUserCheck:  check if a user has suigned in");

        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
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
