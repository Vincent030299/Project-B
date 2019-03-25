package com.example.triptracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class SwipeAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> chosenViews;

    public SwipeAdapter(FragmentManager fm, ArrayList<Fragment> passedChosenViews) {
        super(fm);
        this.chosenViews = passedChosenViews;
    }

    public int size() {
        return chosenViews.size();
    }

    public Fragment set(int index, Fragment element) {
        return chosenViews.set(index, element);
    }

    public void add(Fragment element) {
        chosenViews.add(element);
    }

    public Fragment remove(int index) {
        return chosenViews.remove(index);
    }

    @Override
    public Fragment getItem(int i) {
        return chosenViews.get(i);
    }

    @Override
    public int getCount() {
        return chosenViews.size();
    }
}
