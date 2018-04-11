package com.napier.mohs.behaviourchangeapp.Diary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.napier.mohs.behaviourchangeapp.Goals.ActivityGoals;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityExerciseList extends Activity {

    private static final String TAG = "ActivityExerciseList";

    AdapterExpandableListView adapter;
    ExpandableListView mExpandableListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private Context mContext = ActivityExerciseList.this;
    String dateIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started exercise list activity");

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListViewExerciseList);

        setupExerciseListData();

        adapter = new AdapterExpandableListView(this, listDataHeader, listDataChild);

        mExpandableListView.setAdapter(adapter);

        // Listview on child click listener
        mExpandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // Button which opens add diary
                Log.d(TAG, "onClick: clicked diary button");
                Log.d(TAG, "onClick: navigating to add diary");
                // receiving date intent from FragmentDiary
                try {
                    Intent intentReceive = getIntent();
                    if(intentReceive !=null)
                        dateIntent = intentReceive.getStringExtra("date");
                } catch (Exception e) {
                    Log.e(TAG, "onCreate: Exception " + e.getMessage());
                }
                    Log.d(TAG, "onCreate: dateIntent " + dateIntent);

                    // The exercise clicked
                    String exercise = listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);

                    Intent intent = new Intent(mContext, ActivityAddDiary.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("date", dateIntent);
                    mBundle.putString("exercise", exercise);
                    intent.putExtras(mBundle);

                    Log.d(TAG, "onCreate: " + exercise + " " + dateIntent);
                    startActivity(intent);


                return false;
            }
        });
    }

    @OnClick(R.id.imageExercisesListClose)
    public void closeActivity(){
        finish();
    }

    /*
     * the data for the exercise list
     */
    private void setupExerciseListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Shoulders");
        listDataHeader.add("Triceps");
        listDataHeader.add("Biceps");
        listDataHeader.add("Chest");
        listDataHeader.add("Back");
        listDataHeader.add("Legs");
        listDataHeader.add("Abs");

        // Adding child data
        List<String> shoulders = new ArrayList<String>();
        shoulders.add("Arnold Dumbbell Press");
        shoulders.add("Behind The Neck Barbell Press");
        shoulders.add("Cable Face Pull");
        shoulders.add("Front Dumbbell Raise");
        shoulders.add("Hammer Strength Shoulder Press");
        shoulders.add("Lateral Dumbbell Raise");
        shoulders.add("Lateral Machine Raise");
        shoulders.add("Log Press");
        shoulders.add("One-Arm Standing Dumbbell Press");
        shoulders.add("Overhead Press");
        shoulders.add("Push Press");
        shoulders.add("Rear Delt Dumbbell Raise");
        shoulders.add("Rear Delt Machine Fly");
        shoulders.add("Seated Dumbbell Lateral Raise");
        shoulders.add("Seated Dumbbell Press");
        shoulders.add("Smith Machine Overhead Press");

        List<String> triceps = new ArrayList<String>();
        triceps.add("Cable Overhead Triceps Extension");
        triceps.add("Close Grip Barbell Bench Press");
        triceps.add("Dumbbell Overhead Triceps Extension");
        triceps.add("EZ-Bar Skullcrusher");
        triceps.add("Lying Triceps Extension");
        triceps.add("Parallel Bar Triceps Dip");
        triceps.add("Ring Dip");
        triceps.add("Rope Push Down");
        triceps.add("Smith Machine Close Grip Bench Press");
        triceps.add("V-Bar Push Down");

        List<String> biceps = new ArrayList<String>();
        biceps.add("Barbell Curl");
        biceps.add("Cable Curl");
        biceps.add("Dumbbell Concentration Curl");
        biceps.add("Dumbbell Curl");
        biceps.add("Dumbbell Hammer Curl");
        biceps.add("Dumbbell Preacher Curl");
        biceps.add("EZ-Bar Curl");
        biceps.add("EZ-Bar Preacher Curl");
        biceps.add("Seated Incline Dumbbell Curl");
        biceps.add("Seated Machine Curl");

        List<String> chest = new ArrayList<String>();
        chest.add("Cable Crossover");
        chest.add("Decline Barbell Bench Press");
        chest.add("Decline Hammer Strength Press");
        chest.add("Flat Barbell Bench Press");
        chest.add("Flat Dumbbell bench Press");
        chest.add("Flat Dumbbell Fly");
        chest.add("Incline Barbell Bench Press");
        chest.add("Incline Dumbbell Bench Press");
        chest.add("Incline Dumbbell Fly");
        chest.add("Incline Hammer Strength Chest Press");
        chest.add("Seated Machine Fly");

        List<String> back = new ArrayList<String>();
        back.add("Barbell Row");
        back.add("Barbell Shrug");
        back.add("Chin Up");
        back.add("Deadlift");
        back.add("Dumbbell Row");
        back.add("Good Morning");
        back.add("Hammer Strength Row");
        back.add("Lat Pulldown");
        back.add("Machine Shrug");
        back.add("Neutral Chin Up");
        back.add("Pendaly Row");
        back.add("Pull Up");
        back.add("Rack Pull");
        back.add("Seated Cable Row");
        back.add("Straight-Arm Cable Pushdown");
        back.add("T-Bar Row");

        List<String> legs = new ArrayList<String>();
        legs.add("Barbell Calf Raise");
        legs.add("Barbell Front Squat");
        legs.add("Barbell Glute Bridge");
        legs.add("Barbell Squat");
        legs.add("Donkey Calf Raise");
        legs.add("Glute-Ham Raise");
        legs.add("Leg Extension Machine");
        legs.add("Leg Press");
        legs.add("Leg Leg Curl Machine");
        legs.add("Romanian Deadlift");
        legs.add("Seated Calf Raise Machine");
        legs.add("Seated Leg Curl Machine");
        legs.add("Standing Calf Raise Machine");
        legs.add("Stiff Legged Deadlift");
        legs.add("Sumo Deadlift");

        List<String> abs = new ArrayList<String>();
        abs.add("Ab-Wheel Rollout");
        abs.add("Cable Crunch");
        abs.add("Crunch");
        abs.add("Crunch Machine");
        abs.add("Decline Crunch");
        abs.add("Dragon Flag");
        abs.add("Hanging Knee Raise");
        abs.add("Hanging Leg Raise");
        abs.add("Plank");
        abs.add("Side Plank");

        listDataChild.put(listDataHeader.get(0), shoulders); // Header, Child data
        listDataChild.put(listDataHeader.get(1), triceps);
        listDataChild.put(listDataHeader.get(2), biceps);
        listDataChild.put(listDataHeader.get(3), chest);
        listDataChild.put(listDataHeader.get(4), back);
        listDataChild.put(listDataHeader.get(5), legs);
        listDataChild.put(listDataHeader.get(6), abs);
    }
}