package com.napier.mohs.behaviourchangeapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.behaviourchangeapp.Diary.ActivityDiary;
import com.napier.mohs.behaviourchangeapp.Goals.ActivityGoals;
import com.napier.mohs.behaviourchangeapp.Home.ActivityHome;
import com.napier.mohs.behaviourchangeapp.Profile.ActivityProfile;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Share.ActivityShare;

/**
 * Created by Mohs on 15/03/2018.
 */

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        //bottomNavigationViewEx.enableAnimation(true);
       // bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final Activity activityCalling, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    // uses context as we are in an object not activity
                    case R.id.ic_house:
                        Intent intent1 = new Intent(context, ActivityHome.class);//ACTIVITY_NUM = 0;
                        context.startActivity(intent1);
                        //references animations to change page transitions from bottom bar
                        activityCalling.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.ic_goals:
                        Intent intent2 = new Intent(context, ActivityGoals.class); //ACTIVITY_NUM = 1;
                        context.startActivity(intent2);
                        activityCalling.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.ic_circle:
                        Intent intent3 = new Intent(context, ActivityShare.class); //ACTIVITY_NUM = 2;
                        context.startActivity(intent3);
                        activityCalling.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.ic_weights:
                        Intent intent4 = new Intent(context, ActivityDiary.class); //ACTIVITY_NUM = 3;
                        context.startActivity(intent4);
                        activityCalling.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.ic_android:
                        Intent intent5 = new Intent(context, ActivityProfile.class); //ACTIVITY_NUM = 4;
                        context.startActivity(intent5);
                        activityCalling.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                }

                return false;
            }
        });
    }
}
