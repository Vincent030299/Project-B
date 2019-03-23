package com.example.triptracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class swipeadapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> myviews;

    public swipeadapter(FragmentManager fm, ArrayList<Fragment> myviews1) {
        super(fm);
        this.myviews = myviews1;
    }

    public int size() {
        return myviews.size();
    }

    public Fragment set(int index, Fragment element) {
        return myviews.set(index, element);
    }

    public void add(Fragment element) {
        myviews.add(element);
    }

    public Fragment remove(int index) {
        return myviews.remove(index);
    }

    @Override
    public Fragment getItem(int i) {
        return myviews.get(i);
    }

    @Override
    public int getCount() {
        return myviews.size();
    }
}
