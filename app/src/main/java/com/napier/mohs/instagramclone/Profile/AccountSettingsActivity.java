package com.napier.mohs.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;
import com.napier.mohs.instagramclone.Utils.SectionsStatePagerAdapter;

import java.util.ArrayList;


/**
 * Created by User on 6/4/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 4;

    private Context mContext;

    public SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        mContext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: started.");
        mViewPager = (ViewPager) findViewById(R.id.viewpagerContainer);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);

        setupSettingsList();
        setupBottomNavigationView();
        setupFragments();
        getIntentIncoming();

        //setup the backarrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) findViewById(R.id.imageAccountBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                finish();
            }
        });
    }

    private void getIntentIncoming() {
        Intent intent = getIntent();
        // checks if we have extra and only then proceeds
        if(intent.hasExtra(getString(R.string.image_selected))
                || intent.hasExtra(getString(R.string.bitmap_selected))){

            // if there is an image url attached as extra it means it was chose from the gallery or photo fragment
            Log.d(TAG, "getIntentIncoming: new image url recieved ");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString((R.string.fragment_edit_profile)))) {

                if(intent.hasExtra(getString(R.string.image_selected))){
                    // new profile picture is set
                    FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                    // as profile photo caption is null
                    // uploads profile photo to firebase storage
                    firebaseMethods.newPhotoUpload(getString(R.string.profile_photo), null,
                            0, intent.getStringExtra(getString(R.string.image_selected)), null);
                }
                else if(intent.hasExtra(getString(R.string.bitmap_selected))) {
                    // new profile picture is set
                    FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                    // as profile photo caption is null
                    // uploads profile photo to firebase storage
                    firebaseMethods.newPhotoUpload(getString(R.string.profile_photo), null,
                            0, null, (Bitmap) intent.getParcelableExtra(getString(R.string.bitmap_selected)));
                }

            }
        }

        // Checks if there is an incoming intent that has an extra
        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "getIntentIncoming: recieved intent from " + getString(R.string.profile_activity));
            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.fragment_edit_profile))); // setsViewPager to incoming intent
        }
    }

    private void setupFragments() {
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.fragment_edit_profile)); //fragment 0
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.fragment_sign_out)); //fragment 1
    }

    // method responsible for actually navigating to fragment
    public void setViewPager(int fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE); // sets visibility of relative layout to gone (Account settings goes away and only fragment is visible)
        Log.d(TAG, "setViewPager: nav to fragment number: ");
        mViewPager.setAdapter(pagerAdapter); // sets up adapter
        mViewPager.setCurrentItem(fragmentNumber); // navigates to fragment that I chose
    }

    private void setupSettingsList() {
        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list.");
        ListView listView = (ListView) findViewById(R.id.listviewAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.fragment_edit_profile)); //fragment 0
        options.add(getString(R.string.fragment_sign_out)); //fragement 1

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        // To set fragment depends on what list item we click here
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "onItemClick: nav to fragment number: " + position);
                setViewPager(position); // position depends on what order you added fragment so edit profile is pos 1, sign out is pos 2...
            }
        });

    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


}
















