package com.napier.mohs.instagramclone.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by User on 6/4/2017.
 */

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    private ImageView mProfilePhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = (ImageView) view.findViewById(R.id.profile_photo);


        setProfileImage();

        // back arrow which goes to profile activity
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going back to profile activity. ");
                getActivity().finish(); // Have to use getActivity().finish(); as we are in fragment
            }
        });
        return view;
    }



    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: profile image is being set");
        String imgURL = "http://cdn.newsapi.com.au/image/v1/9fdbf585d17c95f7a31ccacdb6466af9";
        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "");

        // if have 'www' use append as 'http://'
        // String imgURL = "http://cdn.newsapi.com.au/image/v1/9fdbf585d17c95f7a31ccacdb6466af9";
        // UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "");
    }
}
