package com.napier.mohs.instagramclone.Diary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Share.GalleryFragment;
import com.napier.mohs.instagramclone.Share.PhotoFragment;
import com.napier.mohs.instagramclone.Share.ShareActivity;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;
import com.napier.mohs.instagramclone.Utils.SectionsPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 15/03/2018.
 */

public class DiaryActivity extends AppCompatActivity{
    private static final String TAG = "DiaryActivity";
    private static final int ACTIVITY_NUM = 3;

    private Context mContext = DiaryActivity.this;
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

    // returns the number of the current tab
    // 0 is gallery fragment
    // 1 is photo fragment
    public int getTabCurrentNumber(){
        return mViewPager.getCurrentItem();
    }

    // setting up tabs in diary activity
    private void viewPagerSetup(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());
        adapter.addFragment(new DiaryFragment());

        mViewPager.setAdapter(adapter);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.bottomTabs);
//        tabLayout.setupWithViewPager(mViewPager);
//
//        // set text for each of tabs
//        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
//        tabLayout.getTabAt(1).setText(getString(R.string.photo));
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
