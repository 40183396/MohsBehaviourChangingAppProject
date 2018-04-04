package com.napier.mohs.behaviourchangeapp.Diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.behaviourchangeapp.Goals.ActivityGoals;
import com.napier.mohs.behaviourchangeapp.Models.Exercise;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterExerciseList;
import com.napier.mohs.behaviourchangeapp.Utils.FirebaseMethods;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * Created by Mohs on 23/03/2018.
 */

public class FragmentDiary extends Fragment {
    private static final String TAG = "FragmentDiary";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;

    private Context mContext;

    private ArrayList<Exercise> mExerciseArrayList; // holds all workouts

    // widgets
    @BindView(R.id.listviewDiary)
    ListView mListView;
    @BindView(R.id.imageDiaryPost)
    ImageView mSend;
    @BindView(R.id.bmb)
    BoomMenuButton bmb;

    // database queries
    @BindString(R.string.db_name_exercises)
    String db_exercises;

    @BindString(R.string.exercise_name_field)
    String exercise_name_field;
    @BindString(R.string.exercise_id_field)
    String exercise_id_field;
    @BindString(R.string.exercise_unit_field)
    String unit;

    // Strings
    @BindString(R.string.calling_activity)
    String calling_activity;
    @BindString(R.string.profile_activity)
    String profile_activity;
    @BindString(R.string.user_extra)
    String user_extra;

    @BindView(R.id.timelineDiary)
    DatePickerTimeline timeline;
    String date;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        ButterKnife.bind(this, view);
        Log.d(TAG, "onCreateView: diary fragment started");

        mFirebaseMethods = new FirebaseMethods(getActivity());

        mContext = getActivity(); // keeps context constant

        setupFirebaseAuth();
        setupWidgets();

        // setting up date here first so page auto loads with diary entries
        date = dateGet();

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                String dayString;
                // check if day is less than 10 so it appears in correct format for db retrieval
                if(day < 10){
                    dayString = "0"+String.valueOf(day);
                } else {
                    dayString = String.valueOf(day);
                }
                // changed format so if month is less than 9 it appends a zero before it
                if (month > 10) {
                    date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + dayString;
                    Log.d(TAG, "onDateSelected: " + date);
                } else {
                    date = String.valueOf(year) + "-0" + String.valueOf(month + 1) + "-" + dayString;
                    Log.d(TAG, "onDateSelected: " + date);
                }

                Toasty.info(mContext, date, Toast.LENGTH_SHORT).show();
                queryDB();
            }
        });
        //sets up the query
        queryDB();

        return view;
    }

    // gets a time stamp in YYYY/MM/DD
    private String dateGet() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Log.d(TAG, "timeStampGet: " + simpleDateFormat.format(new Date()));
        return simpleDateFormat.format(new Date());  // returns formatted date in London timezone
    }


    // sets up widgets
    private void setupWidgets() {
        bmb.addBuilder(new HamButton.Builder().normalText("Add To Diary!").listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                // When the boom-button corresponding this builder is clicked.
                Log.d(TAG, "onClick: clicked diary button");
                Toasty.success(mContext, "button works", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: navigating to add diary");
                Intent intent = new Intent(mContext, ActivityAddDiary.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        }));
        bmb.addBuilder(new HamButton.Builder().normalText("Goals!").listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                // When the boom-button corresponding this builder is clicked.
                Log.d(TAG, "onClick: clicked goals button");
                Toasty.success(mContext, "button works", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: navigating to add diary");
                Intent intent = new Intent(mContext, ActivityGoals.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        }));
    }


    public void queryDB() {
        final ArrayList<Exercise> exerciseArrayList = new ArrayList<Exercise>();
        final ArrayList<String> keyList = new ArrayList<>();
        Query query = myDBRefFirebase
                .child(db_exercises) // looks in exercises node
                .child(FirebaseAuth.getInstance() // looks in current user node
                        .getCurrentUser().getUid()).child(date); // looks in date chosen in calendar

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    keyList.add(singleDataSnapshot.getKey()); // keylist is for deleting from firebase database
                    Exercise exercise = new Exercise();
                    exercise.setExercise_id(singleDataSnapshot.getValue(Exercise.class).getExercise_id().toString());
                    exercise.setExercise_name(singleDataSnapshot.getValue(Exercise.class).getExercise_name().toString());
                    exercise.setExercise_weight(singleDataSnapshot.getValue(Exercise.class).getExercise_weight().toString());
                    exercise.setExercise_reps(singleDataSnapshot.getValue(Exercise.class).getExercise_reps().toString());
                    exerciseArrayList.add(exercise); //adds the data to this array list
                    Log.d(TAG, "onDataChange: looping");
                }
                Log.d(TAG, "onDataChange: number of loops " + exerciseArrayList.size());
                final AdapterExerciseList adapter = new AdapterExerciseList(mContext, R.layout.listitem_exercises, exerciseArrayList);
                mListView.setAdapter(adapter); // arraylist is adapted to the list view
                registerForContextMenu(mListView);

                // if delete is true tthe item from list is deleted
                if(delete == true){
                    exerciseArrayList.remove(position);
                    adapter.notifyDataSetChanged();
                    //new code below
                    myDBRefFirebase
                            .child(db_exercises) // looks in exercises node
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid()).child(date)
                            .child(keyList.get(position)).removeValue();
                    keyList.remove(position);
                    delete = false;

                }

                // if edit is true
                if(edit == true){
                    String name = exerciseArrayList.get(position).getExercise_name();
                    String weight = exerciseArrayList.get(position).getExercise_weight();
                    String reps = exerciseArrayList.get(position).getExercise_reps();
                    Log.d(TAG, "onDataChange: " + name);
                    Log.d(TAG, "onDataChange: " + weight);
                    Log.d(TAG, "onDataChange: " + reps);
                    // When edit is clicked edit diary is opened
                    Log.d(TAG, "onClick: clicked edit diary button");
                    Toasty.success(mContext, "button works", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: navigating to add diary");
                    Intent intent = new Intent(mContext, ActivityEditDiary.class);
                    intent.putExtra("date", date);
                    startActivity(intent);
                    edit = false;
                }


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    // check to see if the edit or delete buttons have been clicked, boolean true means it has
    boolean delete, edit;
    int position; // position in arraylist that has been clicked to bring up context menu

    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.edit_delete_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        position = info.position;
        switch (item.getItemId()) {
            case R.id.edit:
                Log.d(TAG, "onContextItemSelected: edit pressed");
                edit = true;
                queryDB();
                return true;

            case R.id.delete:
                Log.d(TAG, "onContextItemSelected: delete pressed");
                delete = true;
                queryDB();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    //------------------------FIREBASE STUFF------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: firebase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mExerciseArrayList = new ArrayList<>();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: user signed in " + user);
                } else {
                    Log.d(TAG, "onAuthStateChanged: user signed out");
                }
            }
        };

        // if no in diary this is called to instantiate diary
        if (mExerciseArrayList.size() == 0) {
            mExerciseArrayList.clear(); // makes sure we have fresh list every time
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
