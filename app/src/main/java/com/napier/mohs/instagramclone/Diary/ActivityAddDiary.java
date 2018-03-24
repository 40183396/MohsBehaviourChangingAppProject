package com.napier.mohs.instagramclone.Diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by Mohs on 24/03/2018.
 */

public class ActivityAddDiary extends AppCompatActivity {
    private static final String TAG = "ActivityAddDiary";

    private Context mContext = ActivityAddDiary.this;
    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;

    @BindView(R.id.edittextAddDiaryName)
    EditText addName;

    @BindView(R.id.edittextAddUnit)
    EditText addUnit;

    @BindView(R.id.buttonAddEntry)
    Button addEntry1;

    String dateIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddiary);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started diary activity");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(mContext);
        ///addEntryToDB();
        setupFirebaseAuth();

        // receiving date intent from FragmentDiary
        try {
            Intent intent = getIntent();
            dateIntent = intent.getStringExtra("date");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Exception " + e.getMessage() );
        }

    }
    @OnClick(R.id.buttonAddEntry)
    public void addEntryToDB() {
        String name = addName.getText().toString();
        String unit = addUnit.getText().toString();
        String date = dateIntent;

        Log.d(TAG, "addEntryToDB: Adding Entry " + name + ", " + ", " + unit + ", " + date);
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(unit)){
            Toasty.error(mContext, "Please Fill Out All Fields", Toast.LENGTH_SHORT).show();
        } else {

            mFirebaseMethods.exerciseAddToDatabase(date, name, unit);
            addName.getText().clear();
            addUnit.getText().clear();
            Toasty.success(mContext, "Success!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ActivityDiary.class);
            startActivity(intent);
            finish();
        }
    }



    //------------------------FIREBASE STUFF------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
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

        // allows to get datasnapshot and allows to read or write to db
        myDBRefFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


