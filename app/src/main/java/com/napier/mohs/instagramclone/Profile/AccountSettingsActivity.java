package com.napier.mohs.instagramclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.napier.mohs.instagramclone.R;

import java.util.ArrayList;

/**
 * Created by Mohs on 16/03/2018.
 */

public class AccountSettingsActivity extends AppCompatActivity{

    private static final String TAG = "AccountSettingsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        Log.d(TAG, "onCreate: started account settings activity");
    }

    private void setupSettingsList(){
        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list.");
        ListView listview = (ListView) findViewById(R.id.listviewAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add("Edit Profile");
        
    }
}
