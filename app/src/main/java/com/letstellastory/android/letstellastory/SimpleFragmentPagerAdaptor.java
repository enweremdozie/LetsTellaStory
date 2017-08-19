package com.letstellastory.android.letstellastory;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
//Not needed

public class SimpleFragmentPagerAdaptor extends FragmentPagerAdapter {
    int frag = 0;
    MainActivity mActivity = new MainActivity();


    public SimpleFragmentPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            //mActivity.setFragPos(position);
            return new Drama_Fragment();
        }
        else if (position == 1){
            //mActivity.setFragPos(position);
            return new Horror_Fragment();
        }
        else {
            //mActivity.setFragPos(position);
            return new Romance_Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}