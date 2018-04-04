package com.napier.mohs.behaviourchangeapp.Diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.FirebaseMethods;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by Mohs on 04/04/2018.
 */

public class FragmentEditWeights extends Fragment {
    private static final String TAG = "FragmentEditWeights";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;


    private Context mContext;

    // formats number two decimal places
    private static DecimalFormat REAL_FORMATTER = new DecimalFormat("0.##");
    private double numberWeight;
    private int numberReps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_weightsdiary, container, false);
        ButterKnife.bind(this,view);
        Log.d(TAG, "onCreateView: Starting add diary fragment");

        mContext = getActivity(); // keeps context constant

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(mContext);



        ///addEntryToDB();
        setupFirebaseAuth();
        getFromBundle();
        // starts with fields blank
        diaryEditWeight.setText(weightBundle);
        diaryEditReps.setText(repsBundle);
        numberWeight = Double.parseDouble(weightBundle);
        numberReps = Integer.parseInt(repsBundle);

        //Back pressed Logic for fragment
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), ActivityDiary.class);
                        startActivity(intent);

                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }

    String dateBundle, idBundle, nameBundle, weightBundle, repsBundle;

    // TODO Replace hard coded strings in bundle
    // gets from the bundle the date from the diary activity
    private String getFromBundle() {
        Log.d(TAG, "getFromBundle: " + getArguments());

        Bundle bundle = this.getArguments();
        // if bundle is not null we actually have received something
        if (bundle != null) {
            Log.d(TAG, "getFromBundleCallingActivity: recieved from calling activity " + bundle.getString("exercise_id"));
            Bundle b = getActivity().getIntent().getExtras();
            dateBundle = b.getString("date");
            idBundle = b.getString("exercise_id");
            nameBundle = b.getString("name");
            weightBundle = b.getString("weight");
            repsBundle = b.getString("reps");
            return bundle.getString("dateIntent");
        } else {
            Log.d(TAG, "getActivityNumberFromBundle: No Calling Activity recieved");
            Toasty.warning(mContext, "No Bundle Recieved", Toast.LENGTH_SHORT).show();
            return null;
        }

    }





    @BindView(R.id.edittextEditDiaryReps)
    EditText diaryEditReps;
    @BindView(R.id.edittextEditDiaryWeight) EditText diaryEditWeight;

    @OnClick(R.id.btnIncreaseWeightEditDiary)
    public void increaseWeight(){
        numberWeight += 2.5;
        String stringWeight = Double.toString(numberWeight);
        diaryEditWeight.setText(stringWeight);
    }

    @OnClick(R.id.btnDecreaseWeightEditDiary)
    public void decreaseWeight(){
        numberWeight -= 2.5;
        if(numberWeight<0){
            numberWeight=0;
        }
        String stringReps = Double.toString(numberWeight);
        diaryEditWeight.setText(stringReps);
    }

    @OnClick(R.id.btnIncreaseRepsEditDiary)
    public void increaseReps(){
        numberReps += 1;
        String stringReps = String.valueOf(numberReps);
        diaryEditReps.setText(stringReps);
    }

    @OnClick(R.id.btnDecreaseRepsEditDiary)
    public void decreaseReps(){
        numberReps -= 1;
        if(numberReps<0){
            numberReps=0;
        }
        String stringReps = String.valueOf(numberReps);
        diaryEditReps.setText(stringReps);
    }

    // saves the entered details to the firebase database
    @OnClick(R.id.btnSaveEditDiary)
    public void addEntryToDB() {
        numberWeight = Double.parseDouble(diaryEditWeight.getText().toString());
        numberReps = Integer.parseInt(diaryEditReps.getText().toString());

        // format these to two decimal places
        // set double sto string TODO change this to doubles or longs for firebase
        String weight = String.valueOf(REAL_FORMATTER.format(numberWeight));
        String reps = String.valueOf(REAL_FORMATTER.format(numberReps));
        String date = dateBundle;
        String name = nameBundle;
        String exercise_id = idBundle;

        Log.d(TAG, "addEntryToDB: Attempting edit Entry " + weight + ", " + ", " + reps + ", " + date);
        if(TextUtils.isEmpty(weight) || TextUtils.isEmpty(reps)){
            Toasty.error(mContext, "Please Fill Out All Fields", Toast.LENGTH_SHORT).show();
        } else {

            Log.d(TAG, "onClick: navigating back to previous activity");

            mFirebaseMethods.exerciseUpdateDatabase(exercise_id, date, name, weight, reps);
            diaryEditWeight.getText().clear();
            diaryEditReps.getText().clear();
            Toasty.success(mContext, "Success!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, ActivityDiary.class);
            startActivity(intent);
            getActivity().getFragmentManager().popBackStack();
        }
    }

    // clears the edit text fields
    @OnClick(R.id.btnClearEditDiary)
    public void clearTextFields(){
        diaryEditWeight.getText().clear();
        diaryEditReps.getText().clear();
        numberWeight = 0;
        numberReps = 0;
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
