package com.napier.mohs.instagramclone.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.napier.mohs.instagramclone.R;

/**
 * Created by Mohs on 15/03/2018.
 */

public class FragmentMessages extends Fragment {
    private static final String TAG = "FragmentMessages";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        return view;
    }
}
