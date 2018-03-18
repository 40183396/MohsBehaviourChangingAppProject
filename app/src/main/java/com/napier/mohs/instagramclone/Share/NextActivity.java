package com.napier.mohs.instagramclone.Share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.napier.mohs.instagramclone.R;

/**
 * Created by Mohs on 18/03/2018.
 */

public class NextActivity extends AppCompatActivity{
    private static final String TAG = "NextActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Log.d(TAG, "onCreate: recieved selected image: " + getIntent().getStringExtra(getString(R.string.image_selected)));
    }
}
