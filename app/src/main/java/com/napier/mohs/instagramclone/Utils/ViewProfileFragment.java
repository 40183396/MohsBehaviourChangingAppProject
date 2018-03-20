package com.napier.mohs.instagramclone.Utils;

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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.napier.mohs.instagramclone.Models.Comment;
import com.napier.mohs.instagramclone.Models.Like;
import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.Models.UserSettings;
import com.napier.mohs.instagramclone.Profile.AccountSettingsActivity;
import com.napier.mohs.instagramclone.Profile.ProfileActivity;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;
import com.napier.mohs.instagramclone.Utils.GridImageAdapter;
import com.napier.mohs.instagramclone.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 17/03/2018.
 *
 * Fragment to view other peoples profiles
 */


public class ViewProfileFragment extends Fragment{

    private static final String TAG = "ProfileFragment";

    // buildin g interface
    public interface OnImageGridSelectedListener{
        void onImageGridSelected(Photo photo, int activityNumber); // need activity number as we are accessing this view post fragment from lots of different places
    }

    OnImageGridSelectedListener mOnImageGridSelectedListener;

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

    // variables
    private User mUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        mDisplayName = (TextView) view.findViewById(R.id.textviewViewProfileDisplayName);
        mUsername = (TextView) view.findViewById(R.id.textviewProfileName);
        mWebsite = (TextView) view.findViewById(R.id.textviewViewProfileWebsite);
        mDescription = (TextView) view.findViewById(R.id.textviewViewProfileDescription);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.imageViewProfile);
        mPosts = (TextView) view.findViewById(R.id.textviewViewProfilePostsNumber);
        mFollowers = (TextView) view.findViewById(R.id.textviewViewProfileFollowersNumber);
        mFollowing = (TextView) view.findViewById(R.id.textviewViewProfileFollowingNumber);
        mEditProfile = (TextView) view.findViewById(R.id.textviewViewProfileEditYourProfile);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbarViewProfile);
        gridView = (GridView) view.findViewById(R.id.gridviewViewProfile);
        toolbar = (Toolbar) view.findViewById(R.id.toolbarEdit);
        profileMenu = (ImageView) view.findViewById(R.id.imageProfileMenu);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        Log.d(TAG, "onCreateView: started view profile fragment");

        // try catch in case bundle returns null
        try{
            mUser = retrieveUserBundle(); // retrieves user from bundle and set it as mUser
            initialiseUserDetails(); // sets up user details with user that was sent in bundle
        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
            // in case something goes wrong we display toast and pop back stack
            Toast.makeText(mContext, "oops something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack(); // navigates back to whatever we were doing previously
        }

        setupFirebaseAuth();
        setupBottomNavigationView();
        setupToolbar();


        return view;
    }

    private void initialiseUserDetails(){
        // sets the profile widgets
        DatabaseReference widgetsReference = FirebaseDatabase.getInstance().getReference();
        Query widgetsQuery = widgetsReference.child(getString(R.string.db_name_user_account_settings)) // look in user_account_settings node
                .orderByChild(getString(R.string.user_id_field)).equalTo(mUser.getUser_id()); // look in user_id node see if we have a match
        // if match set widgets
        widgetsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: search has found user " + singleDataSnapShot.getValue(UserAccountSettings.class).toString());

                    UserSettings userSettings = new UserSettings(); // UserSettings is User Object and UserAccountSettings Object combined into one
                    userSettings.setUser(mUser); // set user to mUser
                    userSettings.setUserAccountsettings(singleDataSnapShot.getValue(UserAccountSettings.class)); // sets the user account settings retrieved from db
                    // the other users widgets are set up
                    seupWidgets(userSettings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // show users photos
        DatabaseReference photosReference = FirebaseDatabase.getInstance().getReference();
        // points to user id and retrieves their photos instead of going through db of all  photos
        Query photosQuery = photosReference
                .child(getString(R.string.db_name_user_photos))
                .child(mUser.getUser_id());
        photosQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Photo> photos = new ArrayList<Photo>();
                for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                    //photos.add(singleDataSnapshot.getValue(Photo.class)); // gets all photos user has
                    // type casting snapshot to hashmap and then adding fields manually to field object
                    // there is issue where datasnapshot is trying to read hashmap instead of list
                    // work around is typecasting to hashmap and adding fields manually to objects
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleDataSnapshot.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.caption_field)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.user_id_field)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.photo_id_field)).toString());
                    photo.setTags(objectMap.get(getString(R.string.tags_field)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.date_created_field)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.image_path_field)).toString());

                    ArrayList<Comment> commentsArrayList = new ArrayList<Comment>();
                    for(DataSnapshot dataSnapshot1 : singleDataSnapshot
                            .child(getString(R.string.comments_field)).getChildren()){ // loop[ through all comments
                        Comment comment = new Comment();
                        comment.setUser_id(dataSnapshot1.getValue(Comment.class).getUser_id());
                        comment.setComment(dataSnapshot1.getValue(Comment.class).getComment());
                        comment.setDate_created(dataSnapshot1.getValue(Comment.class).getDate_created());
                        commentsArrayList.add(comment);
                    }

                    photo.setComments(commentsArrayList);

                    // list for all the photo likes
                    List<Like> likeList = new ArrayList<Like>();
                    for(DataSnapshot dataSnapshot1 : singleDataSnapshot
                            .child(getString(R.string.likes_field)).getChildren()){ // loop[ through all likes
                        Like like = new Like();
                        like.setUser_id(dataSnapshot1.getValue(Like.class).getUser_id());
                        likeList.add(like);
                    }
                    photo.setLikes(likeList);
                    photos.add(photo);
                }

                // sets up image grid with user photos
                setupGridView(photos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query has been cancelled");
            }
        });
    }

    private User retrieveUserBundle(){
        Log.d(TAG, "retrieveUserBundle: bundle arguments " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.user_extra)); // returns username from bundle
        } else {
            // if no bundle return null
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnImageGridSelectedListener = (OnImageGridSelectedListener) getActivity();
        } catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
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
        BottomNavigationViewHelper.enableNavigation(mContext, getActivity(), bottomNavigationView); //getActivity as we are in fragment
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    // method that sets up grid view with images from db
    private void setupGridView(final ArrayList<Photo> photos){
        Log.d(TAG, "setupGridView: image grid is being set up");

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

        // attaching on click listener to grid view items
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // navigate to new fragment with interface
                mOnImageGridSelectedListener.onImageGridSelected(photos.get(position), ACTIVITY_NUM);
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
