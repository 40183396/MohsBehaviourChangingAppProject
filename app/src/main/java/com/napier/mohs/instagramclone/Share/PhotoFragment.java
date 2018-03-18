package com.napier.mohs.instagramclone.Share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.napier.mohs.instagramclone.R;

/**
 * Created by Mohs on 18/03/2018.
 */

public class PhotoFragment extends Fragment{

    private static final String TAG = "PhotoFragment";
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        Log.d(TAG, "onCreateView: photo frgamnet started");
        return view;
    }
}
