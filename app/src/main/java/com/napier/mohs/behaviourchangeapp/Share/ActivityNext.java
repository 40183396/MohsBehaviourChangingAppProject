package com.napier.mohs.behaviourchangeapp.Share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.napier.mohs.behaviourchangeapp.Utils.UniversalImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 18/03/2018.
 */

public class ActivityNext extends AppCompatActivity{
    private static final String TAG = "ActivityNext";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;

    @BindView(R.id.edittextNextCaption) EditText mCaption;
    @BindView(R.id.textviewNextShare) TextView share;
    @BindView(R.id.imageNextBack) ImageView back;
    @BindView(R.id.imageNextShare) ImageView img;


    private String mAppend = "file:/";
    private int imgCount = 0;
    private String imgURL;
    private Intent mIntent;
    private Bitmap mBitmap;

    private Context mContext = ActivityNext.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        ButterKnife.bind(this);

        mFirebaseMethods = new FirebaseMethods(mContext);

        setupFirebaseAuth();
        imageSet();

        Log.d(TAG, "onCreate: recieved selected image: " + getIntent().getStringExtra(getString(R.string.image_selected)));

        // Button to close gallery
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing next activity");
                finish();
            }
        });

        // goes to share activity
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: sharing image to firebase to db");
                // uploads image to fb db
                Toast.makeText(mContext, "Attempting to upload photo", Toast.LENGTH_SHORT).show();
                // takes caption from caption edit text field
                String caption = mCaption.getText().toString();

                // if intent has extra
                if(mIntent.hasExtra(getString(R.string.image_selected))){ // image means came from gallery
                    imgURL = mIntent.getStringExtra(getString(R.string.image_selected)); // imgURL set to incoming intent
                    mFirebaseMethods.newPhotoUpload(getString(R.string.new_photo), caption, imgCount, imgURL, null);

                }
                else if(mIntent.hasExtra(getString(R.string.bitmap_selected))){ // bitmap means came from camera
                    mBitmap = (Bitmap) mIntent.getParcelableExtra(getString(R.string.bitmap_selected));
                    mFirebaseMethods.newPhotoUpload(getString(R.string.new_photo), caption, imgCount, null, mBitmap);
                }


            }
        });

    }



    // when activity starts automatically sets image incoming image url of intent
    private void imageSet(){
        mIntent = getIntent();
        // if intent has extra
        if(mIntent.hasExtra(getString(R.string.image_selected))){ // image means came from gallery
            imgURL = mIntent.getStringExtra(getString(R.string.image_selected)); // imgURL set to incoming intent
            Log.d(TAG, "imageSet: recieved new image " + imgURL);
            // static call to universal image loader
            UniversalImageLoader.setImage(imgURL, img, null, mAppend);
            // do not need to check for null values as universal image loader can handle this
        }
        else if(mIntent.hasExtra(getString(R.string.bitmap_selected))){ // bitmap means came from camera
            mBitmap = (Bitmap) mIntent.getParcelableExtra(getString(R.string.bitmap_selected));
            Log.d(TAG, "imageSet: recieved new bitmap");
            img.setImageBitmap(mBitmap);
        }

    }


    //------------------------FIREBASE STUFF------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: number of images: " + imgCount);

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
                imgCount = mFirebaseMethods.getImgCount(dataSnapshot);
                Log.d(TAG, "onDataChange: number of images: " + imgCount);
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
