package com.napier.mohs.instagramclone.Diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.Models.Exercise;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.Profile.ActivityAccountSettings;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.AdapterExerciseList;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;

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

        date = dateGet();

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                date = String.valueOf(year) + "-0" + String.valueOf(month+1) + "-" + String.valueOf(day);
                Toasty.info(mContext, date, Toast.LENGTH_SHORT).show();
                queryDB();
            }
        });
        queryDB();
        // setupWidgets();

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

        // button for sending a comment
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked send button");
                Toasty.success(mContext, "button works", Toast.LENGTH_SHORT).show();
                // mFirebaseMethods.exerciseAddToDatabase("pushup", "reps");
                //adapter.notifyDataSetChanged();
                // mListView.setAdapter(adapter); //list view receives data from adapter
                Log.d(TAG, "onClick: navigating to add diary");
                Intent intent = new Intent(mContext, ActivityAddDiary.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });


    }

    private void queryDB(){

        //final ListView listView = (ListView) findViewById(R.id.listviewDiary);
        final ArrayList<Exercise> friends = new ArrayList<Exercise>();

        Query query = myDBRefFirebase
                .child(db_exercises) // looks in exercises node
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child(date);
        // .orderByChild(exercise_id_field); // looks in photo_id field
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    Exercise exercise = new Exercise();
                    exercise.setExercise_id(singleDataSnapshot.getValue(Exercise.class).getExercise_id().toString());
                    exercise.setExercise_name(singleDataSnapshot.getValue(Exercise.class).getExercise_name().toString());
                    exercise.setUnit(singleDataSnapshot.getValue(Exercise.class).getUnit().toString());
                    friends.add(exercise);
                    Log.d(TAG, "onDataChange: looping");
                }
                Log.d(TAG, "onDataChange: number of loops " + friends.size());
                AdapterExerciseList adapter = new AdapterExerciseList(mContext, R.layout.listitem_exercises, friends);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

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

        // if no comments on photo this is called to instantiate comments thread
        if (mExerciseArrayList.size() == 0) {
            mExerciseArrayList.clear(); // makes sure we have fresh list every time

            setupWidgets(); // widgets still get set up even with no comments
        }




//        Log.d(TAG, "onChildAdded: ");
//        // query that queries photo so we can get updated comments
//        Query query = myDBRefFirebase
//                .child(db_exercises) // looks in exercises node
//                .child(FirebaseAuth.getInstance()
//                        .getCurrentUser().getUid()).child(date);
//        // .orderByChild(exercise_id_field); // looks in photo_id field
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: ");
//                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
//
//                    //mExerciseArrayList.clear(); // makes sure we have fresh list every time
//                    Log.d(TAG, "onDataChange: looping");
//
//                    if (!mExerciseArrayList.contains(singleDataSnapshot.getValue(Exercise.class).getExercise_id())) {
//
//                        Exercise exercise = new Exercise();
//                        exercise.setExercise_id(singleDataSnapshot.getValue(Exercise.class).getExercise_id());
//                        exercise.setExercise_name(singleDataSnapshot.getValue(Exercise.class).getExercise_name());
//                        exercise.setUnit(singleDataSnapshot.getValue(Exercise.class).getUnit());
//                        mExerciseArrayList.add(exercise);
//
//                    } else {
//                        Toasty.warning(mContext, "fml.", Toast.LENGTH_SHORT).show();
//                    }
//
//                    Log.d(TAG, "setupWidgets: setting up widgets");
//                    final AdapterExerciseList adapter = new AdapterExerciseList(mContext, R.layout.listitem_exercises, mExerciseArrayList); // adapter with exercises
//                    mListView.setAdapter(adapter); //list view receives data from adapter
//
//                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            try{
//                                adapter.remove(adapter.getItem(position));
//                                adapter.notifyDataSetChanged();
//                                myDBRefFirebase
//                                        .child(db_exercises) // looks in exercises node
//                                        .child(FirebaseAuth.getInstance()
//                                                .getCurrentUser()
//                                                .getUid()).child(date)
//                                        .child(adapter.getItem(position)
//                                                .getExercise_id()).removeValue();
//                            } catch (IndexOutOfBoundsException e){
//                                Log.e(TAG, "onItemClick: IndexOutOfBoundsException" + e.getMessage() );
//                                Toasty.warning(mContext, "oops couldn't delete sorry about that", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    });
//                    setupWidgets();
//
//                    Log.d(TAG, "onDataChange: for loop: " + mExerciseArrayList.size());
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query has been cancelled");
//            }
//        });


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
