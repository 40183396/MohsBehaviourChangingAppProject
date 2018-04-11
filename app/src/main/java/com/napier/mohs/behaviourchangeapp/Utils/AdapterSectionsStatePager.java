package com.napier.mohs.behaviourchangeapp.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 6/4/2017.
 */

public class AdapterSectionsStatePager extends FragmentStatePagerAdapter {

    private final List<Fragment> mListFragments = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mNumberFragments = new HashMap<>();
    private final HashMap<Integer, String> mNameFragments = new HashMap<>();

    public AdapterSectionsStatePager(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mListFragments.get(position);
    }

    @Override
    public int getCount() {
        return mListFragments.size();
    }

    public void addFragment(Fragment fragment, String fragmentName){
        mListFragments.add(fragment);
        mFragments.put(fragment, mListFragments.size()-1);
        mNumberFragments.put(fragmentName, mListFragments.size()-1);
        mNameFragments.put(mListFragments.size()-1, fragmentName);
    }


    // for returning fragment with its name
    public Integer getFragmentNumber(String fragmentName){
        if(mNumberFragments.containsKey(fragmentName)){
            return mNumberFragments.get(fragmentName);
        }else{
            return null;
        }
    }

}




















