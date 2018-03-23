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
import android.widget.AdapterView;
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
import com.napier.mohs.instagramclone.Models.Comment;
import com.napier.mohs.instagramclone.Models.Like;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 17/03/2018.
 */

// Changed Profile Activity to This Fragment, Just Straight Copy And Paste
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // building interface
    public interface OnImageGridSelectedListener {
        void onImageGridSelected(Photo photo, int activityNumber); // need activity number as we are accessing this view post fragment from lots of different places
    }

    OnImageGridSelectedListener mOnImageGridSelectedListener;

    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_COLS_GRID = 3;

    // Widgets
    @BindView(R.id.textviewProfilePostsNumber) TextView mPosts;
    @BindView(R.id.textviewProfileFollowersNumber) TextView mFollowers;
    @BindView(R.id.textviewProfileFollowingNumber) TextView mFollowing;
    @BindView(R.id.textviewProfileDisplayName) TextView mDisplayName;
    @BindView(R.id.textviewProfileName) TextView mUsername;
    @BindView(R.id.textviewProfileWebsite) TextView mWebsite;
    @BindView(R.id.textviewProfileDescription) TextView mDescription;
    @BindView(R.id.textviewProfileEditYourProfile) TextView mEditProfile;
    @BindView(R.id.progressbarProfile) ProgressBar mProgressBar;
    @BindView(R.id.imageProfile) CircleImageView mProfilePhoto;
    @BindView(R.id.gridviewProfile) GridView gridView;
    @BindView(R.id.toolbarEdit) Toolbar toolbar;
    @BindView(R.id.imageProfileMenu) ImageView profileMenu;
    @BindView(R.id.bottomNavViewBar) BottomNavigationViewEx bottomNavigationView;

    private Context mContext;

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;

    private int mCountFollowers;
    private int mCountFollowing;
    private int mCountPosts;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());


        mContext = getActivity();
        Log.d(TAG, "onCreateView: started profile fragment");

        setupFirebaseAuth();
        setupBottomNavigationView();
        setupToolbar();
        setupGridView();
        retrieveFollowersCount();
        retrieveFollowingCount();
        retrievePostsCount();

        // Goes to edit page fragment
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going to edit profile fragment");
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                // flag to know that this is just a calling activity
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);// not finishing as we want to be able to nav back to this activity
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // page transition to edit profile fragment
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnImageGridSelectedListener = (OnImageGridSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }

    // sets up the profile page with data from db
    private void setupWidgets(UserSettings userSettings) {
        Log.d(TAG, "seupWidgets: settings up widget with data from firebase db ");

        // User settings not needed here but added here anyway
        User user = userSettings.getUser();
        UserAccountSettings userAccountSettings = userSettings.getUserAccountsettings();

        UniversalImageLoader.setImage(userAccountSettings.getProfile_photo(), mProfilePhoto, null, ""); // image loader for profile photo

        // sets up widgets with db data
        mDisplayName.setText(userAccountSettings.getDisplay_name());
        mUsername.setText(userAccountSettings.getUsername());
        mWebsite.setText(userAccountSettings.getWebsite());
        mDescription.setText(userAccountSettings.getDescription());
        mProgressBar.setVisibility(View.GONE);
    }

    private void setupToolbar() {

        ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);

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
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, getActivity(), bottomNavigationView); //getActivity as we are in fragment
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    // method that sets up grid view with images from db
    private void setupGridView() {
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
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    //photos.add(singleDataSnapshot.getValue(Photo.class)); // gets all photos user has
                    // type casting snapshot to hashmap and then adding fields manually to field object
                    // there is issue where datasnapshot is trying to read hashmap instead of list
                    // work around is typecasting to hashmap and adding fields manually to objects
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleDataSnapshot.getValue();

                    // added a try catch as kept getting null pointer exception
                    try {
                        photo.setCaption(objectMap.get(getString(R.string.caption_field)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.user_id_field)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.photo_id_field)).toString());
                        photo.setTags(objectMap.get(getString(R.string.tags_field)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.date_created_field)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.image_path_field)).toString());

                        ArrayList<Comment> commentsArrayList = new ArrayList<Comment>();
                        for (DataSnapshot dataSnapshot1 : singleDataSnapshot
                                .child(getString(R.string.comments_field)).getChildren()) { // loop[ through all comments
                            Comment comment = new Comment();
                            comment.setUser_id(dataSnapshot1.getValue(Comment.class).getUser_id());
                            comment.setComment(dataSnapshot1.getValue(Comment.class).getComment());
                            comment.setDate_created(dataSnapshot1.getValue(Comment.class).getDate_created());
                            commentsArrayList.add(comment);
                        }

                        photo.setComments(commentsArrayList);

                        // list for all the photo likes
                        List<Like> likeList = new ArrayList<Like>();
                        for (DataSnapshot dataSnapshot1 : singleDataSnapshot
                                .child(getString(R.string.likes_field)).getChildren()) { // loop[ through all likes
                            Like like = new Like();
                            like.setUser_id(dataSnapshot1.getValue(Like.class).getUser_id());
                            likeList.add(like);
                        }
                        photo.setLikes(likeList);
                        photos.add(photo);
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onDataChange: NullPointerException " + e.getMessage());
                    }

                }

                // image grid is setup
                int widthGrid = getResources().getDisplayMetrics().widthPixels;
                int widthImage = widthGrid / NUM_COLS_GRID;
                gridView.setColumnWidth(widthImage); // sets up images so they are all same size in grid

                // Array list of img urls
                ArrayList<String> imgURLs = new ArrayList<String>();
                for (int i = 0; i < photos.size(); i++) {
                    imgURLs.add(photos.get(i).getImage_path());
                }

                // creates an adappter and sets up grid view with adapter
                GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, imgURLs, "");
                gridView.setAdapter(adapter);

                // attaching on click listener to gird view items
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        // navigate to new fragment with interface
                        mOnImageGridSelectedListener.onImageGridSelected(photos.get(position), ACTIVITY_NUM);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query has been cancelled");
            }
        });
    }

    // method to count number of followers
    private void retrieveFollowersCount(){
        mCountFollowers = 0;

        DatabaseReference followersReference = FirebaseDatabase.getInstance().getReference();
        Query followersQuery = followersReference.child(getString(R.string.db_name_followers)) // look in user_account_settings node
                .child(mAuth.getCurrentUser().getUid()); // look in user_id node see if we have a match
        //
        followersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // every time we find a match in this query we add one to followers
                for(DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: follower found" + singleDataSnapShot.getValue());
                    mCountFollowers++;
                }
                mFollowers.setText(String.valueOf(mCountFollowers)); // sets the followers text field with the number of followers found
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // method to count number of users following
    private void retrieveFollowingCount(){
        mCountFollowing = 0;

        DatabaseReference followingReference = FirebaseDatabase.getInstance().getReference();
        Query followingQuery = followingReference.child(getString(R.string.db_name_following)) // look in following node
                .child(mAuth.getCurrentUser().getUid()); // look in user_id node see if we have a match
        //
        followingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // every time we find a match in this query we add one to following
                for(DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: following found" + singleDataSnapShot.getValue());
                    mCountFollowing++;
                }
                mFollowing.setText(String.valueOf(mCountFollowing)); // sets the following text field with the number of followers found
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // method to count number of followers
    private void retrievePostsCount(){
        mCountPosts = 0;

        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference();
        Query postsQuery = postsReference.child(getString(R.string.db_name_user_photos)) // look in user_account_settings node
                .child(mAuth.getCurrentUser().getUid()); // look in user_id node see if we have a match
        //
        postsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // every time we find a match in this query we add one to followers
                for(DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: number of posts found" + singleDataSnapShot.getValue());
                    mCountPosts++;
                }
                mPosts.setText(String.valueOf(mCountPosts)); // sets the followers text field with the number of followers found
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //------------------------FIRESBASE STUFF------------
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
                // retrievs the user info from db
                setupWidgets(mFirebaseMethods.getUserSettings(dataSnapshot)); // retrieves datasnapshot of user settings and sets up widgets
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
