package com.napier.mohs.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;
import com.napier.mohs.instagramclone.Utils.GridImageAdapter;
import com.napier.mohs.instagramclone.Utils.UniversalImageLoader;


import java.util.ArrayList;

/**
 * Created by Mohs on 15/03/2018.
 */

public class ProfileActivity extends AppCompatActivity{
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_COLS_GRID = 3;


    private Context mContext = ProfileActivity.this;
    private ProgressBar mProgressBar;

    private ImageView mProfilePhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started");

        // inflates fragments for profile
        initialiseProfileFragment();

//        setupBottomNavigationView();
//        setupToolbar();
//        setupActivityWidgets();
//        setProfileImage();
//
//        // temporary method to see if grid works
//        setupGridImagesTemp();
    }

    private void initialiseProfileFragment(){
        Log.d(TAG, "initialiseProfileFragment: inflating " + R.string.profile_fragment);

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profileContainer, profileFragment); // replacing activity container with fragment
        // fragments have different stacks to activities, have to manually track stacks with fragments
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }
//    // temporary method for gridview images
//    private void setupGridImagesTemp(){
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("http://cdn.newsapi.com.au/image/v1/9fdbf585d17c95f7a31ccacdb6466af9");
//        imgURLs.add("https://i.redd.it/v95w1pywpfi01.jpg");
//        imgURLs.add("https://i.redd.it/nm3kc8kzwki01.jpg");
//        imgURLs.add("https://i.redd.it/rut7sosqxi101.jpg");
//        imgURLs.add("https://i.redd.it/cybj5uxyn5m01.jpg");
//        imgURLs.add("https://www.flickr.com/photos/codispotia/40093117854/sizes/l/");
//        imgURLs.add("https://i.redd.it/616dc2aks7m01.jpg");
//        imgURLs.add("https://i.redd.it/0fq9we4wg5m01.jpg");
//
//        setupGridImages(imgURLs);
//    }
//
//    // sets up grid view, takes in array list of image urls
//    private void setupGridImages(ArrayList<String> imgURLs) {
//        GridView gridView = (GridView) findViewById(R.id.gridView);
//
//        // makes sure images are distributed equally acording to phone size
//        int widthGrid = getResources().getDisplayMetrics().widthPixels;
//        int widthImage = widthGrid/NUM_COLS_GRID;
//        gridView.setColumnWidth(widthImage);
//
//        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, imgURLs, "");
//        gridView.setAdapter(adapter);
//    }
//
//    private void setProfileImage(){
//        Log.d(TAG, "setProfileImage: profile image is being set.");
//        String imgURL = "http://cdn.newsapi.com.au/image/v1/9fdbf585d17c95f7a31ccacdb6466af9";
//        UniversalImageLoader.setImage(imgURL, mProfilePhoto, mProgressBar, ""); // static image so this being used
//    }
//
//    // method for seeing ap the widgets (e.g. progress bar)
//    private void setupActivityWidgets(){
//        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//        mProfilePhoto = (ImageView) findViewById(R.id.profile_image);
//    }
//
//    // Sets up toolbar
//    private void setupToolbar(){
//        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolbar) ;
//        setSupportActionBar(toolbar);
//
//        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
//
//        profileMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: navigating to acc settings");
//                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    /**
//     * BottomNavigationView setup
//     */
//    private void setupBottomNavigationView(){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
//        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
//        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM );
//        menuItem.setChecked(true);
//    }

}
