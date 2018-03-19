package com.napier.mohs.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.Models.UserSettings;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;
import com.napier.mohs.instagramclone.Utils.GridImageAdapter;
import com.napier.mohs.instagramclone.Utils.UniversalImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 17/03/2018.
 */

// Changed Profile Activty to This Fragment, Just Straight Copy And Paste
public class ProfileFragment extends Fragment{

    private static final String TAG = "ProfileFragment";

    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_COLS_GRID = 3;


    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription, mEditProfile;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;

    private Context mContext;

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        mDisplayName = (TextView) view.findViewById(R.id.textviewProfileDisplayName);
        mUsername = (TextView) view.findViewById(R.id.textviewProfileName);
        mWebsite = (TextView) view.findViewById(R.id.textviewProfileWebsite);
        mDescription = (TextView) view.findViewById(R.id.textviewProfileDescription);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.imageProfile);
        mPosts = (TextView) view.findViewById(R.id.textviewProfilePostsNumber);
        mFollowers = (TextView) view.findViewById(R.id.textviewProfileFollowersNumber);
        mFollowing = (TextView) view.findViewById(R.id.textviewProfileFollowingNumber);
        mEditProfile = (TextView) view.findViewById(R.id.textviewProfileEditYourProfile);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbarProfile);
        gridView = (GridView) view.findViewById(R.id.gridviewProfile);
        toolbar = (Toolbar) view.findViewById(R.id.toolbarEdit);
        profileMenu = (ImageView) view.findViewById(R.id.imageProfileMenu);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        Log.d(TAG, "onCreateView: stared.");

        setupFirebaseAuth();
        setupBottomNavigationView();
        setupToolbar();
        setupGridView();

        // Goes to edit page fragment
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going to edit profile fragment");
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    // flag to know that this is just a calling activity
                    intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                    startActivity(intent);
                    // not finishing as we want to be able to nav back to this activity
            }
        });

        return view;
    }

    // sets up the profile page with data from db
    private void seupWidgets(UserSettings userSettings){
        Log.d(TAG, "seupWidgets: settings up widget with data from firebase db " );

        // User settings not needed here but added here anyway
        User user = userSettings.getUser();
        UserAccountSettings userAccountSettings = userSettings.getUserAccountsettings();

        UniversalImageLoader.setImage(userAccountSettings.getProfile_photo(), mProfilePhoto, null, ""); // image loader for profile photo

        // sets up widgets with db data
        mDisplayName.setText(userAccountSettings.getDisplay_name());
        mUsername.setText(userAccountSettings.getUsername());
        mWebsite.setText(userAccountSettings.getWebsite());
        mDescription.setText(userAccountSettings.getDescription());
        mPosts.setText(String.valueOf(userAccountSettings.getPosts()));
        mFollowers.setText(String.valueOf(userAccountSettings.getFollowers()));
        mFollowing.setText(String.valueOf(userAccountSettings.getFollowing()));

        mProgressBar.setVisibility(View.GONE);
    }

    private void setupToolbar(){

        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    // setup of the bottom navigation
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    // method that sets up grid view with images from db
    private void setupGridView(){
        Log.d(TAG, "setupGridView: image grid is being set up");

        // ArrayList that is populated with photos from db
        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // points to user id and retrieves their photos instead of going through db of all  photos
        Query query = databaseReference
                .child(getString(R.string.db_name_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                    photos.add(singleDataSnapshot.getValue(Photo.class)); // gets all photos user has
                }

                // image grid is setup
                int widthGrid = getResources().getDisplayMetrics().widthPixels;
                int widthImage = widthGrid/NUM_COLS_GRID;
                gridView.setColumnWidth(widthImage); // sets up images so they are all same size in grid

                // Array list of img urls
                ArrayList<String> imgURLs = new ArrayList<String>();
                for(int i = 0; i < photos.size(); i++){
                    imgURLs.add(photos.get(i).getImage_path());
                }

                // creates an adappter and sets up grid view with adapter
                GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, imgURLs, "");
                gridView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query has been cancelled");
            }
        });
    }



    //------------------------FIRESBASE STUFF------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
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
                // retrievs the user info from db
                seupWidgets(mFirebaseMethods.getUserSettings(dataSnapshot)); // retrieves datasnapshot of user settings and sets up widgets
                // retrievs images for the user
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
