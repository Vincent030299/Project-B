package com.example.test_app;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class swipeadapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> myfragslist;

    public swipeadapter(FragmentManager fm, ArrayList<Fragment> myfrags) {
        super(fm);
        myfragslist=myfrags;
    }

    public Fragment set(int index, Fragment element) {
        return myfragslist.set(index,element);

    }

    public void add(Fragment element){
        myfragslist.add(element);

    }

    @Override
    public Fragment getItem(int i) {
        return myfragslist.get(i);
    }

    @Override
    public int getCount() {
        return myfragslist.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }
}
