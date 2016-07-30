package com.pack.pack.application.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pack.pack.application.fragments.ArtViewFragment;
import com.pack.pack.application.fragments.EducationViewFragment;
import com.pack.pack.application.fragments.FunViewFragment;
import com.pack.pack.application.fragments.LifestyleViewFragment;
import com.pack.pack.application.fragments.MiscViewFragment;
import com.pack.pack.application.fragments.MusicViewFragment;
import com.pack.pack.application.fragments.SpiritualViewFragment;
import com.pack.pack.application.fragments.TabType;
import com.pack.pack.application.fragments.HomeViewFragment;
import com.pack.pack.application.fragments.TopicViewFragment;

/**
 * Created by Saurav on 08-04-2016.
 */
public class MainActivityAdapter extends FragmentPagerAdapter {

    private int countOfTabs;

    public static final int DEFAULT_COUNT_OF_TABS = 8;

    public MainActivityAdapter(FragmentManager fragmentManager) {
        this(fragmentManager, DEFAULT_COUNT_OF_TABS);
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
            TopicViewFragment fragment = null;
            switch (position) {
                case 1:
                    fragment = new LifestyleViewFragment();
                    break;
                case 2:
                    fragment = new ArtViewFragment();
                    break;
                case 3:
                    fragment = new MusicViewFragment();
                    break;
                case 4:
                    fragment = new EducationViewFragment();
                    break;
                case 5:
                    fragment = new FunViewFragment();
                    break;
                case 6:
                    fragment = new SpiritualViewFragment();
                    break;
                case 7:
                    fragment = new MiscViewFragment();
                    break;
            }
            if(fragment !=  null) {
                fragment.setTabType(tabType);
            }
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