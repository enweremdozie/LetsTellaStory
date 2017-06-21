package com.letstellastory.android.letstellastory;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Provides the appropriate {@link Fragment} for a view pager.
 */
public class SimpleFragmentPagerAdaptor extends FragmentPagerAdapter {

    public SimpleFragmentPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Drama_Fragment();
        } else if (position == 1){
            return new Horror_Fragment();
        } else {
            return new Romance_Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "DRAMA";
            case 1:
                return "HORROR";
            case 2:
                return "ROMANCE";
        }

        return null;
    }
}