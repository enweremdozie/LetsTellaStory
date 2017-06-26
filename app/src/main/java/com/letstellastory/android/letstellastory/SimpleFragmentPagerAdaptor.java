package com.letstellastory.android.letstellastory;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Provides the appropriate {@link Fragment} for a view pager.
 */
public class SimpleFragmentPagerAdaptor extends FragmentPagerAdapter {
    int frag = 0;
    MainActivity mActivity = new MainActivity();


    public SimpleFragmentPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            mActivity.setFragPos(position);
            return new Drama_Fragment();
        }
        else if (position == 1){
            mActivity.setFragPos(position);
            return new Horror_Fragment();
        }
        else {
            mActivity.setFragPos(position);
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
                frag = 0;
                mActivity.setFragPos(position);
                return "DRAMA";
            case 1:
               frag = 1;
                mActivity.setFragPos(position);
                return "HORROR";
            case 2:
                frag = 2;
                mActivity.setFragPos(position);
                return "ROMANCE";
        }

        return null;
    }

}