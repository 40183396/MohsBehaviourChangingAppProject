package com.napier.mohs.instagramclone.Diary;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.napier.mohs.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 24/03/2018.
 */

public class ActivityAddDiary extends AppCompatActivity {
    private static final String TAG = "ActivityAddDiary";

    private Context mContext = ActivityAddDiary.this;

    @BindView(R.id.edittextAddDiaryName)
    EditText addName;

    @BindView(R.id.edittextAddUnit)
    EditText addUnit;

    @BindView(R.id.buttonAddEntry)
    Button addEntry;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddiary);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started diary activity");

    }
}
