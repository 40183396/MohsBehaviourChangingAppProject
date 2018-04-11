package com.napier.mohs.behaviourchangeapp.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.behaviourchangeapp.Models.Like;
import com.napier.mohs.behaviourchangeapp.Models.Photo;
import com.napier.mohs.behaviourchangeapp.Models.User;
import com.napier.mohs.behaviourchangeapp.Models.AccountSettings;
import com.napier.mohs.behaviourchangeapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mohs on 19/03/2018.
 */

public class FragmentViewPost extends Fragment {
    private static final String TAG = "FragmentViewPost";

    public interface OnThreadCommentSelectedListener{
        void onThreadCommentSelectedListener(Photo photo);
    }
    OnThreadCommentSelectedListener mOnThreadCommentSelectedListener;

    private Photo mPhoto;
    private int mActivityNumber = 0;

    @BindView(R.id.imagePostPicture) ImagesSquaredView mImagePost;
    @BindView(R.id.bottomNavViewBar) BottomNavigationViewEx mBottomNavigationView;
    @BindView(R.id.textviewPostCaption) TextView mCaption;
    @BindView(R.id.textviewPostUsername) TextView mUsername;
    @BindView(R.id.textviewPostDate) TextView mDateTimestamp;
    @BindView(R.id.textviewPostLikes) TextView mLikes;
    @BindView(R.id.textviewPostComments) TextView mComments;
    @BindView(R.id.imagePostStarYellow) ImageView mStarYellow;
    @BindView(R.id.imagePostStar) ImageView mStarHollow;
    @BindView(R.id.imagePostProfile) ImageView mImageProfile;
    @BindView(R.id.imagePostComment) ImageView mCommentLink;
    //@BindView(R.id.imagePostMenu) ImageView menu;
    @BindView(R.id.imagePostBackArrow) ImageView backArrow;

    private String mPostUsername = "";
    private String mUrl = "";
    private AccountSettings mAccountSettings;
    private StringBuilder mUsersStringBuilder;

    private String mStringLikes;

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private MethodsFirebase mMethodsFirebase;

    private GestureDetector mGestureDetector;

    private Star mStar;
    private boolean mLikeCurrentUser; // boolean if user like s current photo
    private User mCurrentUser;


    // Bundle constructor so we don't have an empty bundle (can cause Null Pointer if we dont do this)
    public FragmentViewPost(){
        super();
        setArguments(new Bundle()); // Always do this when passing info with a bundle
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpost, container, false);
        ButterKnife.bind(this, view); // butterknife for fragments
        mStar = new Star(mStarHollow, mStarYellow); // constructor to star
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());

        setupFirebaseAuth();
        setupBottomNavigationView();

        // button for going to see comments in post
        mCommentLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked comment button");
                mOnThreadCommentSelectedListener.onThreadCommentSelectedListener(mPhoto); // navigates us to our comments thread
            }
        });



        //setup the backarrow for navigating back to "ActivityProfile"
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to previous activty");
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });

        return view;
    }

    // button for menu in post ==================NOT USED YET=============
    // Butterknife example
    @OnClick(R.id.imagePostMenu)
    public void submit() {
        Toast.makeText(getActivity(),
                "Hello from Butterknife OnClick annotation", Toast.LENGTH_SHORT).show();
    }

    private void initialiseFragment(){
        // bundle could potentially be null so need a try catch
        try{
            mPhoto = getFromBundlePhoto(); // photo retrieved from bundle
            SettingsUniversalImageLoader.setImage(mPhoto.getImage_path(), mImagePost, null, "");
            mActivityNumber = getActivityNumberFromBundle(); // activity number retrieved from bundle
            retrieveCurrentUser();
            getPostDetails(); //retrieves user account settings for post
            //getStringLikes(); // needs to come after user account settings have been retrieved

        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(isAdded()){
            initialiseFragment();
        }
    }

    // for interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mOnThreadCommentSelectedListener = (OnThreadCommentSelectedListener) getActivity();
        } catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException" + e.getMessage() );
        }
    }

    // responsible for getting likes
    // gets string of all people who liked photo
    private void getStringLikes(){
        Log.d(TAG, "getStringLikes: getting likes");

        // Test to see if star was working
        // mStar.likeToggle();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // query that checks if the photo has any likes
        Query query = databaseReference
                .child(getString(R.string.db_name_photos)) // looks in photos node
                .child(mPhoto.getPhoto_id()) // looks for photo_id of photo
                .child(getString(R.string.likes_field)); // checks likes field
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsersStringBuilder = new StringBuilder();
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    // user liked photo already

                    // user has not liked photo
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    // query that checks if the photo has any likes
                    Query query = databaseReference
                            .child(getString(R.string.db_name_users)) // looks in users node
                            .orderByChild(getString((R.string.user_id_field))) // looks for particular user_id
                            .equalTo(singleDataSnapshot.getValue(Like.class).getUser_id()); // checks likes field
                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) { // this is only called if a like is found
                            // if we we find anyything we want to append those found strings
                            for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                                // user liked photo already we loop and add user to string builder
                                Log.d(TAG, "onDataChange: like has been found " + singleDataSnapshot.getValue(User.class).getUsername());
                                // user is appended to string builder
                                mUsersStringBuilder.append(singleDataSnapshot.getValue(User.class).getUsername());
                                // append comma to make it easier to handle data
                                mUsersStringBuilder.append(",");

                            }
                            // when loop done we have to split users where we have comma
                            String[] usersSplit = mUsersStringBuilder.toString().split(",");

                            //deteermine if current user has liked photo or not
                            if(mUsersStringBuilder.toString().contains(mCurrentUser.getUsername() + ",")){ // needs comma otherwise there is a problem when multiple users like it
                                // means user has liked photo
                                mLikeCurrentUser = true;
                            } else {
                                mLikeCurrentUser = false;
                            }

                            int length = usersSplit.length;
                            // different cases for number of users who liked photo determines how like comment is displayed
                            if(length == 1){ // only one user likes photo etc.
                                mStringLikes = "Liked by " + usersSplit[0];
                            } else if (length == 2){
                                mStringLikes = "Liked by " + usersSplit[0] +
                                            " and " + usersSplit[1];
                            }else if (length == 3){
                                mStringLikes = "Liked by " + usersSplit[0] +
                                        ", " + usersSplit[1] +
                                        " and " + usersSplit[2];
                            }else if (length == 4){
                                mStringLikes = "Liked by " + usersSplit[0] +
                                        ", " + usersSplit[1] +
                                        ", " + usersSplit[2] +
                                        " and " + usersSplit[3];
                            }else if (length > 4){
                                mStringLikes = "Liked by " + usersSplit[0] +
                                        ", " + usersSplit[1] +
                                        ", " + usersSplit[2] +
                                        " and " +  (usersSplit.length - 3) + " others"; // -3 as we have already 3 users displayed
                            }
                            setupWidgets(); // setting up widets after callling get likes string
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                // if no users have liked photo
                if(!dataSnapshot.exists()){
                    mStringLikes = "";
                    mLikeCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrieveCurrentUser(){
        DatabaseReference currentUserReference = FirebaseDatabase.getInstance().getReference();
        Query currentUserQuery = currentUserReference
                .child(getString(R.string.db_name_users)) // look in user_account_settings node
                .orderByChild(getString(R.string.user_id_field))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()); // look in user_id node see if we have a match

        currentUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()){
                    mCurrentUser = singleDataSnapShot.getValue(User.class);
                    getStringLikes();
                    Log.d(TAG, "onDataChange: search has found user " + mCurrentUser);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: ");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: ");

            // Test to see if star was working
           // mStar.likeToggle();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference
                    .child(getString(R.string.db_name_photos)) // looks in photos node
                    .child(mPhoto.getPhoto_id()) // looks for photo_id of photo
                    .child(getString(R.string.likes_field)); // checks likes field
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleDataSnapshot.getKey();
                        //  user liked photo already
                        if(mLikeCurrentUser &&
                                singleDataSnapshot.getValue(Like.class).getUser_id()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){ // this makes sure we are removing a like for the current user only

                            // removes like from photos node
                            myDBRefFirebase.child(getString(R.string.db_name_photos))
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.likes_field))
                                    .child(keyID) // gets key of id
                                    .removeValue();

                            // removes like from user photos node
                            myDBRefFirebase.child(getString(R.string.db_name_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) //current users user_id
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.likes_field))
                                    .child(keyID) // gets key of id
                                    .removeValue();

                            mStar.likeToggle();
                            getStringLikes();
                        }
                        // user has not liked photo
                        else if(!mLikeCurrentUser){
                            // add a like to db
                            addLikeNew();
                            Log.d(TAG, "onDataChange: new like added");
                            break;
                        }

                    }

                    // checks if datasnapshot does not exist
                    if(!dataSnapshot.exists()){
                        // new like is added to db
                        Log.d(TAG, "onDataChange: datasnapshot did not exist, new like added");
                        addLikeNew();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void addLikeNew(){
        Log.d(TAG, "addLikeNew: addin a new like");

        String newLikeID = myDBRefFirebase.push().getKey(); // creates a new key
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // adds like to photos node
        myDBRefFirebase.child(getString(R.string.db_name_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.likes_field))
                .child(newLikeID) // gets key of id
                .setValue(like);

        // adds like to user photos node
        myDBRefFirebase.child(getString(R.string.db_name_user_photos))
                .child(mPhoto.getUser_id()) //current users user_id
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.likes_field))
                .child(newLikeID) // gets key of id
                .setValue(like);

        mStar.likeToggle();
        getStringLikes();
    }

    private void getPostDetails() {
        Log.d(TAG, "getPostDetails: started");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // points to user id and retrieves their photos instead of going through db of all  photos
        Query query = databaseReference
                .child(getString(R.string.db_name_user_account_settings)) // looks in user_account_settings node
                .orderByChild(getString(R.string.user_id_field)) // looks for user_id field
                .equalTo(mPhoto.getUser_id()); // checks if photo user_id matches a user_id

        Log.d(TAG, "getPostDetails: query: " + mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener(){


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                    mAccountSettings = singleDataSnapshot.getValue(AccountSettings.class); // gets user acccount settings for that photo
                    Log.d(TAG, "onDataChange: User: " + mAccountSettings.getUsername() );

                }
                //setupWidgets();
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

        if(mPhoto.getComments().size() > 0){
            mComments.setText("View all " + mPhoto.getComments().size() + " comments"); // displays link how many comments are on photo
        } else {
            mComments.setText(""); // if no comments link is not displayed
        }

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked button to go to comments");

                mOnThreadCommentSelectedListener.onThreadCommentSelectedListener(mPhoto); // goes to comment thread of photo
            }
        });

        SettingsUniversalImageLoader.setImage(mAccountSettings.getProfile_photo(), mImageProfile, null, "");
        mUsername.setText((mAccountSettings.getUsername()));
        mLikes.setText(mStringLikes);
        mCaption.setText(mPhoto.getCaption());

        // if liked by current user star is yellow
        if(mLikeCurrentUser){
            mStarYellow.setVisibility(View.VISIBLE);
            mStarHollow.setVisibility(View.GONE);
            mStarYellow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.d(TAG, "onTouch: yellow star touch detected");
                    return mGestureDetector.onTouchEvent(motionEvent);
                }
            });

        } else { // not liked by user star is white
            mStarYellow.setVisibility(View.GONE);
            mStarHollow.setVisibility(View.VISIBLE);
            mStarHollow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.d(TAG, "onTouch: hollow star touched");
                    return mGestureDetector.onTouchEvent(motionEvent);
                }
            });
        }
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
        Log.d(TAG, "bottomNavigationViewExSetup: setting up BottomNavigationView");
        SettingsBottomNavigationViewEx.bottomNavigationViewExSetup(mBottomNavigationView);
        SettingsBottomNavigationViewEx.enableNavigation(getActivity(), getActivity(), mBottomNavigationView); //getActivity as we are in fragment
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
