package com.napier.mohs.instagramclone.Share;

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
import com.napier.mohs.instagramclone.Profile.ProfileActivity;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;
import com.napier.mohs.instagramclone.Utils.UniversalImageLoader;

/**
 * Created by Mohs on 18/03/2018.
 */

public class NextActivity extends AppCompatActivity{
    private static final String TAG = "NextActivity";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;

    private EditText mCaption;

    private String mAppend = "file:/";
    private int imgCount = 0;
    private String imgURL;
    private Intent mIntent;
    private Bitmap mBitmap;

    private Context mContext = NextActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods = new FirebaseMethods(mContext);

        mCaption = (EditText) findViewById(R.id.edittextNextCaption) ;

        setupFirebaseAuth();
        imageSet();

        Log.d(TAG, "onCreate: recieved selected image: " + getIntent().getStringExtra(getString(R.string.image_selected)));

        // Button to close gallery
        ImageView back = (ImageView) findViewById(R.id.imageNextBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing next activity");
                finish();
            }
        });

        // goes to share activty
        TextView share = (TextView) findViewById(R.id.textviewNextShare);
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


    // when activity starts automatically sets image incomming image url of intent
    private void imageSet(){
        mIntent = getIntent();
        ImageView img = (ImageView) findViewById(R.id.imageNextShare);

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


    private void someMethod(){
        /*
        1) first data model of photos
        2) properties added to Photo Objects: (caption, date, imageURL, photo_id, tags, user_id)
        3) Count number photos user has already
        4) photo is uploaded firbase storage and insert two more nodes in firebase db
            'photo' node
            'user_photos' node
         */
    }



    //------------------------FIRESBASE STUFF------------
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
