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
import com.napier.mohs.behaviourchangeapp.Utils.BottomNavigationViewHelper;
import com.napier.mohs.behaviourchangeapp.Utils.SectionsPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 15/03/2018.
 */

public class ActivityDiary extends AppCompatActivity{
    private static final String TAG = "ActivityDiary";
    private static final int ACTIVITY_NUM = 3;

    private Context mContext = ActivityDiary.this;

    @BindView(R.id.viewpagerContainer)
    ViewPager mViewPager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started diary activity");

        viewPagerSetup();
        setupBottomNavigationView();
    }

    // setting up fragment in diary activity
    private void viewPagerSetup(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentDiary());

        mViewPager.setAdapter(adapter);
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


}
