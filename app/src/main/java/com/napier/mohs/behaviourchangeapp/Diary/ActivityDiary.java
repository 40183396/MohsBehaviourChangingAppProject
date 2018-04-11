package com.napier.mohs.behaviourchangeapp.Diary;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.SettingsBottomNavigationViewEx;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterSectionsPager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 15/03/2018.
 */

public class ActivityDiary extends AppCompatActivity{
    private static final String TAG = "ActivityDiary";
    private static final int ACTIVITY_NUMBER = 3;

    private Context mContext = ActivityDiary.this;

    @BindView(R.id.containerViewPager)
    ViewPager mViewPager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started diary activity");

        setupViewPagerFragments();
        bottomNavbarSetup();
    }

    // setting up fragment in diary activity
    private void setupViewPagerFragments(){
        AdapterSectionsPager adapter = new AdapterSectionsPager(getSupportFragmentManager());
        adapter.addFragment(new FragmentDiary());

        mViewPager.setAdapter(adapter);
    }


    // setup for bottom navbar
    private void bottomNavbarSetup(){
        Log.d(TAG, "bottomNavbarSetup: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        SettingsBottomNavigationViewEx.bottomNavigationViewExSetup(bottomNavigationViewEx);
        SettingsBottomNavigationViewEx.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER );
        menuItem.setChecked(true);
    }


}
