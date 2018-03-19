package com.napier.mohs.instagramclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.ViewPostFragment;

/**
 * Created by Mohs on 15/03/2018.
 */

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnImageGridSelectedListener{
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_COLS_GRID = 3;


    private Context mContext = ProfileActivity.this;
    private ProgressBar mProgressBar;

    private ImageView mProfilePhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started profile ");

        // inflates fragments for profile
        initialiseProfileFragment();

    }

    private void initialiseProfileFragment(){
        Log.d(TAG, "initialiseProfileFragment: inflating " + R.string.fragment_profile);

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerProfile, profileFragment); // replacing activity container with fragment
        // fragments have different stacks to activities, have to manually track stacks with fragments
        transaction.addToBackStack(getString(R.string.fragment_profile));
        transaction.commit();
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
        fragmentTransaction.addToBackStack(getString(R.string.fragment_post));
        fragmentTransaction.commit();
    }

}
