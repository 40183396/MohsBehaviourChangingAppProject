package com.napier.mohs.instagramclone.Diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.Models.Workout;
import com.napier.mohs.instagramclone.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohs on 23/03/2018.
 */

public class FragmentDiary extends Fragment{
    private static final String TAG = "FragmentDiary";

    private ArrayList<Workout> mWorkoutArrayList; // holds all workouts

    // widgets
    @BindView(R.id.listviewDiary)
    ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        ButterKnife.bind(this, view);
        Log.d(TAG, "onCreateView: diary fragment started");

        mWorkoutArrayList = new ArrayList<>();

        return view;
    }
}
