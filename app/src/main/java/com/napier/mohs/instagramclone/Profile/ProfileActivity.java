package com.napier.mohs.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.ViewCommentsFragment;
import com.napier.mohs.instagramclone.Utils.ViewPostFragment;
import com.napier.mohs.instagramclone.Utils.ViewProfileFragment;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 15/03/2018.
 */

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnImageGridSelectedListener,
        ViewProfileFragment.OnImageGridSelectedListener,
        ViewPostFragment.OnThreadCommentSelectedListener {
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_COLS_GRID = 3;


    private Context mContext = ProfileActivity.this;
    private ProgressBar mProgressBar;

    private ImageView mProfilePhoto;

    // Intent Strings
    @BindString(R.string.calling_activity) String calling_activity;
    @BindString(R.string.user_extra) String user_extra;
    @BindString(R.string.fragment_view_profile) String viewprofile_fragment;
    @BindString(R.string.fragment_profile) String profile_fragment;
    @BindString(R.string.fragment_post) String post_fragment;
    @BindView(R.id.containerProfile) FrameLayout containerProfile;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started profile ");

        // inflates fragments for profile
        initialiseProfileFragment();

    }

    private void initialiseProfileFragment() {
        Log.d(TAG, "initialiseProfileFragment: inflating " + R.string.fragment_profile);

        // intent to differentiate whther we go to user profile or someone elses
        Intent intent = getIntent();
        // if intent has calling_acivity string we are coming from search activity
        if(intent.hasExtra(calling_activity)){ // check whether extra has calling_activity string
            Log.d(TAG, "initialiseProfileFragment: has calling_activity");
            User user = intent.getParcelableExtra(user_extra);
            // this is if you are clicking on photo from main feed you are directed to your own profile
            if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                Log.d(TAG, "initialiseProfileFragment: searching for user which was attached as intent extra");
                if (intent.hasExtra(user_extra)) { // not needed if but good to use in case we have more calling_activity extras in future development
                    Log.d(TAG, "initialiseProfileFragment: viewing someone elses profile");
                    ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
                    Bundle bundleArgs = new Bundle(); // bundle to pass photo
                    bundleArgs.putParcelable(user_extra,
                            intent.getParcelableExtra(user_extra)); // getting parcelable extra of username
                    viewProfileFragment.setArguments(bundleArgs); // sets fragment arguments to bundle arguments
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.containerProfile, viewProfileFragment); // replace container profile with view comments fragment
                    fragmentTransaction.addToBackStack(viewprofile_fragment);
                    fragmentTransaction.commit();
                } else { // if no calling_activity user is just navigating to their own profile
                    Log.d(TAG, "initialiseProfileFragment: users profile is being inflated");
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.containerProfile, profileFragment); // replacing activity container with fragment
                    // fragments have different stacks to activities, have to manually track stacks with fragments
                    transaction.addToBackStack(profile_fragment);
                    transaction.commit();
                }

            }else { // if no calling_activity user is just navigating to their own profile
                Log.d(TAG, "initialiseProfileFragment: users profile is being inflated");
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.containerProfile, profileFragment); // replacing activity container with fragment
                // fragments have different stacks to activities, have to manually track stacks with fragments
                transaction.addToBackStack(profile_fragment);
                transaction.commit();
            }
//            } else {
//                Toast.makeText(mContext, "oops something went wrong", Toast.LENGTH_SHORT).show();
//            }

        }


    }

    // makes it so when we click on any photo from anywhere it loads up the post of the photo
    @Override
    public void onImageGridSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onImageGridSelected: image in gridview selected: " + photo.toString());

        ViewPostFragment viewPostFragment = new ViewPostFragment();
        Bundle bundleArguments = new Bundle();
        bundleArguments.putParcelable(getString(R.string.photo), photo);
        bundleArguments.putInt(getString(R.string.activity_number), activityNumber);
        viewPostFragment.setArguments(bundleArguments);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerProfile, viewPostFragment);
        fragmentTransaction.addToBackStack(post_fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onThreadCommentSelectedListener(Photo photo) {
        Log.d(TAG, "onThreadCommentSelectedListener: comment thread is seleceted");

        ViewCommentsFragment viewCommentsFragment = new ViewCommentsFragment();
        Bundle bundleArgs = new Bundle(); // bundle to pass photo
        bundleArgs.putParcelable(getString(R.string.photo), photo); // pass photo
        viewCommentsFragment.setArguments(bundleArgs); // sets fragment arguments to bundle arguments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerProfile, viewCommentsFragment); // replace container profile with view comments fragment
        fragmentTransaction.addToBackStack(getString(R.string.fragment_viewcomments));
        fragmentTransaction.commit();
    }
}
