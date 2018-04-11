package com.napier.mohs.behaviourchangeapp.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.napier.mohs.behaviourchangeapp.Models.Photo;
import com.napier.mohs.behaviourchangeapp.Models.User;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.FragmentViewComments;
import com.napier.mohs.behaviourchangeapp.Utils.FragmentViewPost;
import com.napier.mohs.behaviourchangeapp.Utils.FragmentViewProfile;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 15/03/2018.
 */

public class ActivityProfile extends AppCompatActivity implements FragmentProfile.OnImageGridSelectedListener,
        FragmentViewProfile.OnImageGridSelectedListener,
        FragmentViewPost.OnThreadCommentSelectedListener {
    private static final String TAG = "ActivityProfile";

    // Intent Strings
    @BindString(R.string.calling_activity) String calling_activity;
    @BindString(R.string.user_extra) String user_extra;
    @BindString(R.string.photo) String photo_extra;
    @BindString(R.string.activity_number) String activity_number;
    @BindString(R.string.fragment_view_profile) String viewprofile_fragment;
    @BindString(R.string.fragment_viewcomments) String viewcomments_fragment;
    @BindString(R.string.fragment_profile) String fragment_profile;
    @BindString(R.string.fragment_post) String fragment_post;
    @BindView(R.id.containerProfile) FrameLayout containerProfile;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started profile ");

        // inflates fragments for profile
        setupFragmentProfile();

    }

    private void setupFragmentProfile() {
        Log.d(TAG, "setupFragmentProfile: inflating " + R.string.fragment_profile);

        // intent to differentiate whther we go to user profile or someone elses
        Intent intent = getIntent();
        // if intent has calling_acivity string we are coming from search activity
        if(intent.hasExtra(calling_activity)){ // check whether extra has calling_activity string
            Log.d(TAG, "setupFragmentProfile: has calling_activity");
            User user = intent.getParcelableExtra(user_extra);
            // this is if you are clicking on photo from main feed you are directed to your own profile
            if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                Log.d(TAG, "setupFragmentProfile: searching for user which was attached as intent extra");
                if (intent.hasExtra(user_extra)) { // not needed if but good to use in case we have more calling_activity extras in future development
                    Log.d(TAG, "setupFragmentProfile: viewing someone elses profile");
                    FragmentViewProfile fragmentViewProfile = new FragmentViewProfile();
                    Bundle bundle = new Bundle(); // bundle to pass photo
                    bundle.putParcelable(user_extra,
                            intent.getParcelableExtra(user_extra)); // getting parcelable extra of username
                    fragmentViewProfile.setArguments(bundle); // sets fragment arguments to bundle arguments
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.containerProfile, fragmentViewProfile); // replace container profile with view comments fragment
                    transaction.addToBackStack(viewprofile_fragment);
                    transaction.commit();
                }

            }else { // if no calling_activity user is just navigating to their own profile
                Log.d(TAG, "setupFragmentProfile: users profile is being inflated");
                FragmentProfile fragmentProfile = new FragmentProfile();
                FragmentTransaction transaction = ActivityProfile.this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.containerProfile, fragmentProfile); // replacing activity container with fragment
                // fragments have different stacks to activities, have to manually track stacks with fragments
                transaction.addToBackStack(fragment_profile);
                transaction.commit();
            }

        } else { // if no calling_activity user is just navigating to their own profile
            Log.d(TAG, "setupFragmentProfile: users profile is being inflated");
            FragmentProfile fragmentProfile = new FragmentProfile();
            FragmentTransaction transaction = ActivityProfile.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.containerProfile, fragmentProfile); // replacing activity containerHome with fragment
            // fragments have different stacks to activities, have to manually track stacks with fragments
            transaction.addToBackStack(fragment_profile);
            transaction.commit();

    }}

    // makes it so when we click on any photo from anywhere it loads up the post of the photo
    @Override
    public void onImageGridSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onImageGridSelected: image in gridview selected: " + photo.toString());

        FragmentViewPost fragmentViewPost = new FragmentViewPost();
        Bundle bundle = new Bundle();
        bundle.putParcelable(photo_extra, photo);
        bundle.putInt(activity_number, activityNumber);
        fragmentViewPost.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerProfile, fragmentViewPost);
        transaction.addToBackStack(fragment_post);
        transaction.commit();
    }

    @Override
    public void onThreadCommentSelectedListener(Photo photo) {
        Log.d(TAG, "onThreadCommentSelectedListener: comment thread is seleceted");

        FragmentViewComments fragmentViewComments = new FragmentViewComments();
        Bundle bundle = new Bundle(); // bundle to pass photo
        bundle.putParcelable(photo_extra, photo); // pass photo
        fragmentViewComments.setArguments(bundle); // sets fragment arguments to bundle arguments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerProfile, fragmentViewComments); // replace container profile with view comments fragment
        transaction.addToBackStack(viewcomments_fragment);
        transaction.commit();
    }
}
