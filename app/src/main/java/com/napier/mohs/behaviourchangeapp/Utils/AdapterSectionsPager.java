package com.napier.mohs.behaviourchangeapp.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


// this class is for tabs and storing fragments as them
public class AdapterSectionsPager extends FragmentPagerAdapter{
    private final List<Fragment> mListFragment = new ArrayList<>();

    public AdapterSectionsPager(FragmentManager manager){
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mListFragment.get(position);
    }

    @Override
    public int getCount() {
        return mListFragment.size();
    }

    public void addFragment(Fragment fragment){
        mListFragment.add(fragment);
    }


}
