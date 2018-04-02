package com.napier.mohs.behaviourchangeapp.Utils;

import android.content.Context;
import android.content.Intent;
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
import com.napier.mohs.behaviourchangeapp.Models.Comment;
import com.napier.mohs.behaviourchangeapp.Models.Like;
import com.napier.mohs.behaviourchangeapp.Models.Photo;
import com.napier.mohs.behaviourchangeapp.Models.User;
import com.napier.mohs.behaviourchangeapp.Models.UserAccountSettings;
import com.napier.mohs.behaviourchangeapp.Models.UserSettings;
import com.napier.mohs.behaviourchangeapp.Profile.ActivityAccountSettings;
import com.napier.mohs.behaviourchangeapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 17/03/2018.
 * <p>
 * Fragment to view other peoples profiles
 */


public class FragmentViewProfile extends Fragment {

    private static final String TAG = "FragmentProfile";

    // buildin g interface
    public interface OnImageGridSelectedListener {
        void onImageGridSelected(Photo photo, int activityNumber); // need activity number as we are accessing this view post fragment from lots of different places
    }

    OnImageGridSelectedListener mOnImageGridSelectedListener;

    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_COLS_GRID = 3;


    @BindView(R.id.textviewViewProfilePostsNumber)
    TextView mPosts;
    @BindView(R.id.textviewViewProfileFollowersNumber)
    TextView mFollowers;
    @BindView(R.id.textviewViewProfileFollowingNumber)
    TextView mFollowing;
    @BindView(R.id.textviewViewProfileDisplayName)
    TextView mDisplayName;
    @BindView(R.id.textviewViewProfileUsername)
    TextView mUsername;
    @BindView(R.id.textviewViewProfileWebsite)
    TextView mWebsite;
    @BindView(R.id.textviewViewProfileDescription)
    TextView mDescription;
    @BindView(R.id.textviewViewProfileEditYourProfile)
    TextView mEditProfile;
    @BindView(R.id.textviewViewProfileUnfollowUser)
    TextView mUnfollow;
    @BindView(R.id.textviewViewProfileFollowUser)
    TextView mFollow;
    @BindView(R.id.progressbarViewProfile)
    ProgressBar mProgressBar;
    @BindView(R.id.imageViewProfile)
    CircleImageView mProfilePhoto;
    @BindView(R.id.gridviewViewProfile)
    GridView gridView;
    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationView;
    @BindView(R.id.imageViewProfileBackArrow)
    ImageView backArrow;

    // database queries
    @BindString(R.string.db_name_following)
    String db_following;
    @BindString(R.string.db_name_followers)
    String db_followers;
    @BindString(R.string.db_name_user_photos)
    String db_user_photos;
    @BindString(R.string.db_name_user_account_settings)
    String db_user_account_settings;
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

    private Context mContext;

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // variables
    private User mUser;
    private int mCountFollowers, mCountFollowing, mCountPosts;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewprofile, container, false);
        ButterKnife.bind(this, view); // butterknife for fragments

        mAuth = FirebaseAuth.getInstance();

        mContext = getActivity();

        Log.d(TAG, "onCreateView: started view profile fragment");

        // try catch in case bundle returns null
        try {
            mUser = retrieveUserBundle(); // retrieves user from bundle and set it as mUser
            initialiseUserDetails(); // sets up user details with user that was sent in bundle
        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
            // in case something goes wrong we display toast and pop back stack
            Toast.makeText(mContext, "oops something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack(); // navigates back to whatever we were doing previously
        }

        setupFirebaseAuth();
        setupBottomNavigationView();

        checkFollowing();
        retrieveFollowersCount();
        retrieveFollowingCount();
        retrievePostsCount();

        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: started following: " + mUser.getUsername());

                // updates followers and following nodes
                FirebaseDatabase.getInstance().getReference()
                        .child(db_following) // following node
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .child(userID_field)
                        .setValue(mUser.getUser_id()); // not creating following object just inserting id

                FirebaseDatabase.getInstance().getReference()
                        .child(db_followers) // followers node
                        .child(mUser.getUser_id()) // insert to other users followers node
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userID_field)
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()); // not creating following object just inserting id

                setFollowingUser();
            }
        });

        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: started unfollowing: " + mUser.getUsername());
                // updates followers and following nodes
                FirebaseDatabase.getInstance().getReference()
                        .child(db_following) // following node
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .removeValue(); // not creating following object just inserting id

                FirebaseDatabase.getInstance().getReference()
                        .child(db_followers) // followers node
                        .child(mUser.getUser_id()) // insert to other users followers node
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue(); // not creating following object just inserting id

                setUnfollowingUser();
            }
        });

        // Goes to edit page fragment
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going to edit profile fragment");
                Intent intent = new Intent(getActivity(), ActivityAccountSettings.class);
                // flag to know that this is just a calling activity
                intent.putExtra(calling_activity, profile_activity);
                startActivity(intent);// not finishing as we want to be able to nav back to this activity
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // page transition to edit profile fragment
            }
        });

        // back arrow which goes to profile activity
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going back to profile activity. ");
                getActivity().getSupportFragmentManager().popBackStack(); // Have to use getActivity().finish(); as we are in fragment
                getActivity().finish();
            }
        });

        return view;
    }

    private void initialiseUserDetails() {
        // sets the profile widgets
        DatabaseReference widgetsReference = FirebaseDatabase.getInstance().getReference();
        Query widgetsQuery = widgetsReference.child(db_user_account_settings) // look in user_account_settings node
                .orderByChild(userID_field).equalTo(mUser.getUser_id()); // look in user_id node see if we have a match
        // if match set widgets
        widgetsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: search has found user " + singleDataSnapShot.getValue(UserAccountSettings.class).toString());

                    UserSettings userSettings = new UserSettings(); // UserSettings is User Object and UserAccountSettings Object combined into one
                    userSettings.setUser(mUser); // set user to mUser
                    userSettings.setUserAccountsettings(singleDataSnapShot.getValue(UserAccountSettings.class)); // sets the user account settings retrieved from db
                    // the other users widgets are set up
                    setupWidgets(userSettings);
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
                .child(db_user_photos)
                .child(mUser.getUser_id());
        photosQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Photo> photos = new ArrayList<Photo>();
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    //photos.add(singleDataSnapshot.getValue(Photo.class)); // gets all photos user has
                    // type casting snapshot to hashmap and then adding fields manually to field object
                    // there is issue where datasnapshot is trying to read hashmap instead of list
                    // work around is typecasting to hashmap and adding fields manually to objects
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleDataSnapshot.getValue();

                    photo.setCaption(objectMap.get(caption_field).toString());
                    photo.setUser_id(objectMap.get(userID_field).toString());
                    photo.setPhoto_id(objectMap.get(photoID_field).toString());
                    photo.setTags(objectMap.get(tags_field).toString());
                    photo.setDate_created(objectMap.get(date_created_field).toString());
                    photo.setImage_path(objectMap.get(image_path_field).toString());

                    ArrayList<Comment> commentsArrayList = new ArrayList<Comment>();
                    for (DataSnapshot dataSnapshot1 : singleDataSnapshot
                            .child(comments_field).getChildren()) { // loop[ through all comments
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
                            .child(likes_field).getChildren()) { // loop[ through all likes
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

    // check if user is being followed
    private void checkFollowing() {
        Log.d(TAG, "checkFollowing: checking if you are following this user");
        setUnfollowingUser();

        // checking to see if logged in user is following the user who is in bundle
        DatabaseReference checkReference = FirebaseDatabase.getInstance().getReference();
        Query checkQuery = checkReference.child(db_following) // look in user_account_settings node
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(userID_field).equalTo(mUser.getUser_id()); // look in user_id node see if we have a match
        // if match set widgets
        checkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: search has found user " + singleDataSnapShot.getValue());

                    // if something is found we can just set to following
                    setFollowingUser();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // method to count number of followers
    private void retrieveFollowersCount() {
        mCountFollowers = 0;

        DatabaseReference followersReference = FirebaseDatabase.getInstance().getReference();
        Query followersQuery = followersReference.child(db_followers) // look in user_account_settings node
                .child(mUser.getUser_id()); // look in user_id node see if we have a match
        //
        followersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // every time we find a match in this query we add one to followers
                for (DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()) {
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
    private void retrieveFollowingCount() {
        mCountFollowing = 0;

        DatabaseReference followingReference = FirebaseDatabase.getInstance().getReference();
        Query followingQuery = followingReference.child(db_following) // look in following node
                .child(mUser.getUser_id()); // look in user_id node see if we have a match
        //
        followingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // every time we find a match in this query we add one to following
                for (DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()) {
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
    private void retrievePostsCount() {
        mCountPosts = 0;

        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference();
        Query postsQuery = postsReference.child(db_user_photos) // look in user_account_settings node
                .child(mUser.getUser_id()); // look in user_id node see if we have a match
        //
        postsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // every time we find a match in this query we add one to followers
                for (DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()) {
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

    private void setFollowingUser() {
        Log.d(TAG, "setFollowingUser: user interface is being updated for following this user");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.VISIBLE);
        mEditProfile.setVisibility(View.GONE);
    }

    private void setUnfollowingUser() {
        Log.d(TAG, "setFollowingUser: user interface is being updated for unfollowing this user");
        mFollow.setVisibility(View.VISIBLE);
        mUnfollow.setVisibility(View.GONE);
        mEditProfile.setVisibility(View.GONE);
    }

    private void setCurrentProfileUser() {
        Log.d(TAG, "setFollowingUser: user interface is being updated to current user own profile");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.GONE);
        mEditProfile.setVisibility(View.VISIBLE);
    }


    private User retrieveUserBundle() {
        Log.d(TAG, "retrieveUserBundle: bundle arguments " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(user_extra); // returns username from bundle
        } else {
            // if no bundle return null
            return null;
        }
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
        Log.d(TAG, "setupWidgets: settings up widget with data from firebase db ");

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
    private void setupGridView(final ArrayList<Photo> photos) {
        Log.d(TAG, "setupGridView: image grid is being set up");

        // image grid is setup
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int widthImage = widthGrid / NUM_COLS_GRID;
        gridView.setColumnWidth(widthImage); // sets up images so they are all same size in grid

        // Array list of img urls
        ArrayList<String> imgURLs = new ArrayList<String>();
        for (int i = 0; i < photos.size(); i++) {
            imgURLs.add(photos.get(i).getImage_path());
        }

        // creates an adapter and sets up grid view with adapter
        AdapterGridImage adapter = new AdapterGridImage(getActivity(), R.layout.layout_grid_imageview, imgURLs, "");
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
