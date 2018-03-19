package com.napier.mohs.instagramclone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;
import com.napier.mohs.instagramclone.Utils.GridImageAdapter;
import com.napier.mohs.instagramclone.Utils.ImagesSquaredView;
import com.napier.mohs.instagramclone.Utils.UniversalImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Mohs on 19/03/2018.
 */

public class ViewPostFragment extends Fragment {
    private static final String TAG = "ViewPostFragment";
    private Photo mPhoto;
    private int mActivityNumber = 0;

    private ImagesSquaredView mImagePost;
    private BottomNavigationViewEx mBottomNavigationView;
    private TextView mCaption, mUsername, mDateTimestamp;
    private ImageView mStarYellow, mStarHollow, mImageProfile;

    private String mPostUsername = "";
    private String mUrl = "";
    private UserAccountSettings mUserAccountSettings;

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;

    // Bundle constructor so we don't have an empty bundle (can cause Null Pointer if we dont do this)
    public ViewPostFragment(){
        super();
        setArguments(new Bundle()); // Always do this when passing info with a bundle
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpost, container, false);
        mImagePost = (ImagesSquaredView) view.findViewById(R.id.imagePostPicture);
        mBottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);

        mCaption = (TextView) view.findViewById(R.id.textviewPostCaption);
        mUsername = (TextView) view.findViewById(R.id.textviewPostUsername);
        mDateTimestamp = (TextView) view.findViewById(R.id.textviewPostDate);
        mStarHollow = (ImageView) view.findViewById(R.id.imagePostStar);
        mStarYellow = (ImageView) view.findViewById(R.id.imagePostStarYellow);
        mImageProfile = (ImageView) view.findViewById(R.id.imagePostProfile);


        // bundle could potentially be null so need a try catch
        try{
            mPhoto = getFromBundlePhoto(); // photo retrieved form bundle
            UniversalImageLoader.setImage(mPhoto.getImage_path(), mImagePost, null, "");
            mActivityNumber = getActivityNumberFromBundle(); // activity number retrieved from bundle
        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }



        // button for menu in post
        ImageView menu = (ImageView) view.findViewById(R.id.imagePostMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //setup the backarrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.imagePostBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");

            }
        });

        setupFirebaseAuth();
        setupBottomNavigationView();
        getPostDetails();
       //
        return view;
    }

    private void getPostDetails() {
        Log.d(TAG, "getPostDetails: started");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // points to user id and retrieves their photos instead of going through db of all  photos
        Query query = databaseReference
                .child(getString(R.string.db_name_user_account_settings))
                .orderByChild("user_id")
                .equalTo(mPhoto.getUser_id());

        Log.d(TAG, "getPostDetails: query: " + mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener(){


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                    mUserAccountSettings = singleDataSnapshot.getValue(UserAccountSettings.class); // gets user acccount settings for that photo
                    Log.d(TAG, "onDataChange: " + mUserAccountSettings. );

                }
                setupWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query has been cancelled");
            }
        });
    }


    private void setupWidgets(){
        String differenceTimestamp = getDateTimeStampDifference();

        if(!differenceTimestamp.equals("0")){ // if there is a difference
            mDateTimestamp.setText(differenceTimestamp + " DAYS AGO");
        } else { // if no difference
            mDateTimestamp.setText("TODAY");
        }

        UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mImageProfile, null, "");
        mUsername.setText((mUserAccountSettings.getUsername()));
    }

    // returns date string that shows how many days ago the post was made
    private String getDateTimeStampDifference(){
        Log.d(TAG, "getDateTimeStampDifference: retrieving date timestamp");

        String difference = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        Date timestamp;
        Date today = calendar.getTime();
        simpleDateFormat.format(today); // need to format date object and convert to string
        final String timestampPhoto = mPhoto.getDate_created();
        try{
            timestamp = simpleDateFormat.parse(timestampPhoto);
            difference = String.valueOf(Math.round(((today.getTime() -  timestamp.getTime()) / 1000 / 60 / 60 / 24))); // works out number of days, getTime() converts string to long
            Log.d(TAG, "getDateTimeStampDifference: timestamp: " + difference);
        } catch(ParseException e){
            Log.e(TAG, "getDateTimeStampDifference: ParseException" + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

    // gets incoming activity number from bundle  from pofile activity interface
    private int getActivityNumberFromBundle(){
        Log.d(TAG, "getActivityNumberFromBundle: " + getArguments());

        Bundle bundle = this.getArguments();
        // if bundle is not null we actually have recieved somethin
        if(bundle != null){
            return bundle.getInt(getString(R.string.activity_number));
        } else {
            Log.d(TAG, "getActivityNumberFromBundle: No activity number returned");
            return 0;
        }
    }

    // gets from the bundle the photo from the profile activity interface
    private Photo getFromBundlePhoto(){
        Log.d(TAG, "getFromBundlePhoto: " + getArguments());

        Bundle bundle = this.getArguments();
        // if bundle is not null we actually have recieved somethin
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.photo));
        } else {
            Log.d(TAG, "getActivityNumberFromBundle: No Photo recieved");
            return null;
        }
    }

    // setup of the bottom navigation
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(mBottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(getActivity(), getActivity(), mBottomNavigationView); //getActivity as we are in fragment
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }



//------------------------FIRESBASE STUFF------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
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

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
