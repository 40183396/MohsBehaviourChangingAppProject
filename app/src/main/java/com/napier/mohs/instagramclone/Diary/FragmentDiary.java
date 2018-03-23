package com.napier.mohs.instagramclone.Diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.Models.Comment;
import com.napier.mohs.instagramclone.Models.Exercise;
import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.Models.Workout;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.AdapterCommentsList;
import com.napier.mohs.instagramclone.Utils.AdapterExerciseList;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    @BindString(R.string.db_name_following)
    String db_following;
    @BindString(R.string.db_name_followers)
    String db_followers;
    @BindString(R.string.db_name_user_photos)
    String db_user_photos;
    @BindString(R.string.db_name_exercises)
    String db_exercises;
    @BindString(R.string.user_id_field)
    String userID_field;
    @BindString(R.string.caption_field)
    String caption_field;
    @BindString(R.string.comments_field)
    String comments_field;
    @BindString(R.string.likes_field)
    String likes_field;
    @BindString(R.string.photo_id_field)
    String photoID_field;
    @BindString(R.string.tags_field)
    String tags_field;
    @BindString(R.string.date_created_field)
    String date_created_field;
    @BindString(R.string.image_path_field)
    String image_path_field;

    // Strings
    @BindString(R.string.calling_activity)
    String calling_activity;
    @BindString(R.string.profile_activity)
    String profile_activity;
    @BindString(R.string.user_extra)
    String user_extra;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        ButterKnife.bind(this, view);
        Log.d(TAG, "onCreateView: diary fragment started");

        mFirebaseMethods = new FirebaseMethods(getActivity());

        mExerciseArrayList = new ArrayList<>();

        mContext = getActivity(); // keeps context constant

        setupFirebaseAuth();

        setupWidgets();

        return view;
    }

    // sets up widgets
    private void setupWidgets() {
        Log.d(TAG, "setupWidgets: setting up widgets");
        AdapterExerciseList adapter = new AdapterExerciseList(mContext, R.layout.listitem_exercises, mExerciseArrayList); // adapter with exercises
        mListView.setAdapter(adapter); //list view receives data from adapter

        // button for sending a comment
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked send button");
                Toasty.success(mContext, "button works", Toast.LENGTH_SHORT).show();
                mFirebaseMethods.exerciseAddToDatabase("pushup", "reps");

            }
        });
    }


    //------------------------FIREBASE STUFF------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firebase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: user signed in " + user);
                } else {
                    Log.d(TAG, "onAuthStateChanged: user signed out");
                }
            }
        };

        // allows to get datasnapshot and allows to read or write to db
        myDBRefFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // returns number of images for user
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
