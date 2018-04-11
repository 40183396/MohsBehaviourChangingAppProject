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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.behaviourchangeapp.Models.Exercise;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterExerciseList;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
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

    private Context mContext;

    private ArrayList<Exercise> mExerciseArrayList; // holds all workouts

    // widgets
    @BindView(R.id.listviewDiary)
    ListView mListView;
    @BindView(R.id.imageDiaryAdd)
    ImageView mAdd;

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


    String date;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        ButterKnife.bind(this, view);
        Log.d(TAG, "onCreateView: diary fragment started");



        mContext = getActivity(); // keeps context constant


        setupFirebaseAuth();
        setupWidgets();

        // setting up date here first so page auto loads with diary entries
        date = dateGet();


        Calendar endDate = Calendar.getInstance(); // End date
        endDate.add(Calendar.DAY_OF_MONTH, 7);

        Calendar startDate = Calendar.getInstance(); // Start date
        startDate.add(Calendar.DAY_OF_MONTH, -7);

        Calendar defaultDate = Calendar.getInstance();
        Log.d(TAG, "onCreateView: " + date);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5).defaultSelectedDate(defaultDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar d, int position) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = simpleDateFormat.format(d.getTime());

                Log.d(TAG, "onDateSelected: " + date);
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
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Button which opens add diary
                Log.d(TAG, "onClick: clicked diary button");
                Log.d(TAG, "onClick: navigating to add diary");
                Intent intent = new Intent(mContext, ActivityExerciseList.class);
                intent.putExtra("date", date);
                startActivity(intent);

            }
        });

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
                    String exercise_id  =exerciseArrayList.get(position).getExercise_id();
                    String name = exerciseArrayList.get(position).getExercise_name();
                    String weight = exerciseArrayList.get(position).getExercise_weight();
                    String reps = exerciseArrayList.get(position).getExercise_reps();
                    Log.d(TAG, "onDataChange: " + exercise_id);
                    Log.d(TAG, "onDataChange: " + weight);
                    Log.d(TAG, "onDataChange: " + reps);

                    // When edit is clicked edit diary is opened
                    Log.d(TAG, "onClick: clicked edit diary button");

                    Intent intent = new Intent(mContext, ActivityEditDiary.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("exercise_id", exercise_id);
                    bundle.putString("date", date);
                    bundle.putString( "name",name);
                    bundle.putString("weight",weight);
                    bundle.putString("reps",reps);
                    intent.putExtras(bundle);
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
