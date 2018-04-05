package com.napier.mohs.behaviourchangeapp.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.behaviourchangeapp.Home.ActivityHome;
import com.napier.mohs.behaviourchangeapp.Models.Comment;
import com.napier.mohs.behaviourchangeapp.Models.Photo;
import com.napier.mohs.behaviourchangeapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 19/03/2018.
 */

public class FragmentViewComments extends Fragment {
    private static final String TAG = "FragmentViewComments";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;

    // this constructor prevents NullPointerException when receiving a bundle from a interface
    public FragmentViewComments() {
        super();
        setArguments(new Bundle());
    }

    private Photo mPhoto;
    private Context mContext;

    // widgets
    @BindView(R.id.imageCommentPost)
    ImageView mSend;
    @BindView(R.id.edittextCommentsComment)
    EditText mComment;
    @BindView(R.id.listviewComments)
    ListView mListView;
    @BindView(R.id.imageCommentsBackArrow)
    ImageView mBackArrow;

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

    private ArrayList<Comment> mCommentArrayList; // contains list of all comments in thread


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewcomments, container, false);
        ButterKnife.bind(this, view); // butterknife for fragments

        mCommentArrayList = new ArrayList<>();
        mContext = getActivity(); // keeps context constant

        // bundle could potentially be null so need a try catch
        try {
            mPhoto = getFromBundlePhoto(); // photo retrieved from bundle
            getFromBundleCallingActivity();

        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

        setupFirebaseAuth();

        //setup the backarrow for navigating back to previous activity

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to previous activity");
                try {
                    if (getFromBundleCallingActivity().equals(getString(R.string.home_activity))) { // means from home activity

                        getActivity().getSupportFragmentManager().popBackStack();
                        ((ActivityHome) getActivity()).layoutShow(); // fix so when you press back after view photo on main feed you return to home activity

                    } else {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onClick: NullPointerException" + e.getMessage());
                }
            }
        });

        return view;
    }


    // sets up widgets
    private void setupWidgets() {
        Log.d(TAG, "setupWidgets: setting up widgets");
        AdapterCommentsList adapter = new AdapterCommentsList(mContext, R.layout.listitem_comments, mCommentArrayList); // adapter with comments
        mListView.setAdapter(adapter); //list view receives data from adapter

        // button for sending a comment
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked send button");
                // only sends if comment field isn't blank
                if (!mComment.getText().toString().equals("")) {
                    Log.d(TAG, "onClick: attempting send comment");
                    newCommentAdd(mComment.getText().toString()); // gets text from edit text field and posts a comment on photo

                    mComment.setText(""); // clears field after posting comment
                    keyboardClose();
                } else {
                    Log.d(TAG, "onClick: Comments field is blank");
                }
            }
        });
    }

    // closes keyboard method
    private void keyboardClose() {
        Log.d(TAG, "keyboardClose: keyboard being closed");

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromInputMethod(view.getWindowToken(), 0); // hides keyboard
        }
    }

    // gets from the bundle the photo from the profile activity interface
    private String getFromBundleCallingActivity() {
        Log.d(TAG, "getFromBundleCallingActivity: " + getArguments());

        Bundle bundle = this.getArguments();
        // if bundle is not null we actually have received something
        if (bundle != null) {
            Log.d(TAG, "getFromBundleCallingActivity: recieved from calling activity" + bundle.getString(getString(R.string.home_activity)));
            return bundle.getString(getString(R.string.home_activity));
        } else {
            Log.d(TAG, "getActivityNumberFromBundle: No Calling Activity recieved");
            return null;
        }
    }

    // gets from the bundle the photo from the profile activity interface
    private Photo getFromBundlePhoto() {
        Log.d(TAG, "getFromBundlePhoto: " + getArguments());

        Bundle bundle = this.getArguments();
        // if bundle is not null we actually have recieved somethin
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        } else {
            Log.d(TAG, "getActivityNumberFromBundle: No Photo received");
            return null;
        }
    }


    private void newCommentAdd(String newComment) {
        Log.d(TAG, "newCommentAdd: adding comment to thread: " + newComment);

        String commentID = myDBRefFirebase.push().getKey(); // to get comment id need to generate a key

        Comment comment = new Comment();
        comment.setComment(newComment); // sets comment
        comment.setDate_created(timeStampGet()); // sets timestamp
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid()); // set user_id

        // inserts into photos node
        myDBRefFirebase.child(getString(R.string.db_name_photos)) // look in photo node
                .child(mPhoto.getPhoto_id()) //get photo_id
                .child(getString(R.string.comments_field)) // gets list of comments
                .child(commentID) // gets id of comment
                .setValue(comment); // sets comment here

        // inserts into user_photos node
        myDBRefFirebase.child(getString(R.string.db_name_user_photos)) // look in user_photos node
                .child(mPhoto.getUser_id()) //get user_id of whoevers photo it is
                .child(mPhoto.getPhoto_id()) //get photo_id
                .child(getString(R.string.comments_field)) // gets list of comments
                .child(commentID) // gets id of new comment
                .setValue(comment); // sets comment here
    }

    // gets a time stamp of when comment is posted
    private String timeStampGet() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        Log.d(TAG, "timeStampGet: " + simpleDateFormat.format(new Date()));
        return simpleDateFormat.format(new Date());  // returns formatted date in London timezone
    }


//------------------------FIREBASE STUFF------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: firebase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
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


        // if no comments on photo this is called to instantiate comments thread
        if (mPhoto.getComments().size() == 0) {
            mCommentArrayList.clear(); // makes sure we have fresh list every time
            Comment commentFirst = new Comment();
            commentFirst.setComment(mPhoto.getCaption());
            commentFirst.setUser_id(mPhoto.getUser_id());
            commentFirst.setDate_created(mPhoto.getDate_created());
            mCommentArrayList.add(commentFirst);
            mPhoto.setComments(mCommentArrayList);
            setupWidgets(); // widgets still get set up even with no comments
        }

        // gets called imediately when fragment is activated and also when their is any change to the node
        myDBRefFirebase.child(mContext.getString(R.string.db_name_photos))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.comments_field)) // this all gets called when their is a change to the comments node inside particular photo
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAdded: ");
                        // query that queries photo so we can get updated comments
                        Query query = myDBRefFirebase
                                .child(mContext.getString(R.string.db_name_photos)) // looks in photos node
                                .orderByChild(mContext.getString(R.string.photo_id_field)) // looks in photo_id field
                                .equalTo(mPhoto.getPhoto_id()); // this is the photo comment thread we want to refresh
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onDataChange: ");
                                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                                    //photos.add(singleDataSnapshot.getValue(Photo.class)); // gets all photos user has
                                    // type casting snapshot to hashmap and then adding fields manually to field object
                                    // there is issue where datasnapshot is trying to read hashmap instead of list
                                    // work around is typecasting to hashmap and adding fields manually to objects
                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleDataSnapshot.getValue();

                                    photo.setCaption(objectMap.get(mContext.getString(R.string.caption_field)).toString());
                                    photo.setUser_id(objectMap.get(mContext.getString(R.string.user_id_field)).toString());
                                    photo.setPhoto_id(objectMap.get(mContext.getString(R.string.photo_id_field)).toString());
                                    photo.setTags(objectMap.get(mContext.getString(R.string.tags_field)).toString());
                                    photo.setDate_created(objectMap.get(mContext.getString(R.string.date_created_field)).toString());
                                    photo.setImage_path(objectMap.get(mContext.getString(R.string.image_path_field)).toString());


                                    // make sure to call all these things after receiving photo data from bundle otherwise wont't work
                                    // first comment will have the user who posted the picture with their caption
                                    mCommentArrayList.clear(); // makes sure we have fresh list every time
                                    Comment commentFirst = new Comment();
                                    commentFirst.setComment(mPhoto.getCaption());
                                    commentFirst.setUser_id(mPhoto.getUser_id());
                                    commentFirst.setDate_created(mPhoto.getDate_created());

                                    mCommentArrayList.add(commentFirst); // adds first comment to list for testing
                                    Log.d(TAG, "onDataChange: first comment added to array: " + mCommentArrayList);
                                    // loop checks for any more comments
                                    for (DataSnapshot dataSnapshot1 : singleDataSnapshot
                                            .child(mContext.getString(R.string.comments_field)).getChildren()) { // loop[ through all comments
                                        Comment comment = new Comment();
                                        comment.setUser_id(dataSnapshot1.getValue(Comment.class).getUser_id());
                                        comment.setComment(dataSnapshot1.getValue(Comment.class).getComment());
                                        comment.setDate_created(dataSnapshot1.getValue(Comment.class).getDate_created());
                                        mCommentArrayList.add(comment);
                                    }

                                    photo.setComments(mCommentArrayList); // adds list of comments to photo

                                    mPhoto = photo; // globally updated photo

                                    setupWidgets();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: query has been cancelled");
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildChanged: ");
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onChildRemoved: ");
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildMoved: ");
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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
