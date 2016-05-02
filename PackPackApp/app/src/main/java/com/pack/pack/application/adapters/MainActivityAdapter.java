package com.pack.pack.application.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pack.pack.application.fragments.LifestyleViewFragment;
import com.pack.pack.application.fragments.TabType;
import com.pack.pack.application.fragments.HomeViewFragment;
import com.pack.pack.application.fragments.TopicViewFragment;

/**
 * Created by Saurav on 08-04-2016.
 */
public class MainActivityAdapter extends FragmentPagerAdapter {

    private int countOfTabs;

    public MainActivityAdapter(FragmentManager fragmentManager) {
        this(fragmentManager, 2);
    }

    public MainActivityAdapter(FragmentManager fragmentManager, int countOfTabs) {
        super(fragmentManager);
        this.countOfTabs = countOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        TabType tabType = TabType.values()[position];
        if(position == 0) {
            HomeViewFragment fragment = new HomeViewFragment();
            fragment.setTabType(tabType);
            return fragment;
        }
        else {
            TopicViewFragment fragment = new LifestyleViewFragment();
            fragment.setTabType(tabType);
            return fragment;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TabType.values()[position].getDisplayName();
    }

    @Override
    public int getCount() {
        return countOfTabs;
    }
}