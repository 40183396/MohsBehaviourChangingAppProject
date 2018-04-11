package com.napier.mohs.behaviourchangeapp.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.SettingsBottomNavigationViewEx;
import com.napier.mohs.behaviourchangeapp.Utils.Permissions;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterSectionsPager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 15/03/2018.
 */

public class ActivityShare extends AppCompatActivity{
    private static final String TAG = "ActivityShare";
    private static final int PERMISSIONS_VERIFY_REQUEST = 1;
    private static final int ACTIVITY_NUMBER = 2;

    @BindView(R.id.containerViewPager) ViewPager mViewPager;

    private Context mContext = ActivityShare.this;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);

        Log.d(TAG, "onCreate: started share activity");

        if(permissionsCheckArray(Permissions.PERMISSIONS)){
            setupViewPagerTabs();
        } else {
            permissionsVerify(Permissions.PERMISSIONS);

        }

        setupBottomNavbar();
    }

    // returns the number of the current tab
    // 0 is gallery fragment
    // 1 is photo fragment
    public int retrieveCurrentNumberTab(){
        return mViewPager.getCurrentItem();
    }

    private void setupViewPagerTabs(){
        AdapterSectionsPager adapter = new AdapterSectionsPager(getSupportFragmentManager());
        adapter.addFragment(new FragmentGallery());
        adapter.addFragment(new FragmentPhoto());

        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.bottomTabs);
        tabLayout.setupWithViewPager(mViewPager);

        // set text for each of tabs
        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));
    }

    public int taskGet(){
        Log.d(TAG, String.format("taskGet: %d", getIntent().getFlags()));
        return getIntent().getFlags(); // returns int of flag
    }

    // checks an array of permissions verified
    public boolean permissionsCheckArray(String[] permissions){
        Log.d(TAG, "permissionsCheckArray: permissions array is being checked ");

        // loops through permissions in array
        for(int i = 0; i < permissions.length; i++){
            String check = permissions[i];
            if(!permissionsCheck(check)){
                return false;
            }
        }
        return true;
    }

    // method to check if a single permission is verified
    public boolean permissionsCheck(String permission){
        Log.d(TAG, "permissionsCheck: permission being checked: " + permission);

        int permissionsRequest = ActivityCompat.checkSelfPermission(ActivityShare.this, permission);

        if(permissionsRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "permissionsCheck: permission has not been granted for : " + permission);
            return false;
        }
        else {
            Log.d(TAG, "permissionsCheck: permssion granted for: " + permission);
            return true;
        }
    }

    // verifies all permissions have passed
    public void permissionsVerify(String[] permissions){
        Log.d(TAG, "permissionsVerify: permissions being verified");

        ActivityCompat.requestPermissions(ActivityShare.this, permissions, PERMISSIONS_VERIFY_REQUEST);
    }

    // setup for bottom navbar
    private void setupBottomNavbar(){
        Log.d(TAG, "setupBottomNavbar: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        SettingsBottomNavigationViewEx.bottomNavigationViewExSetup(bottomNavigationViewEx);
        SettingsBottomNavigationViewEx.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER );
        menuItem.setChecked(true);
    }
}
