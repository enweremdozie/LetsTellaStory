package com.letstellastory.android.letstellastory;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by dozie on 2017-07-08.
 */

public class SimpleStoryPagerAdapter extends FragmentPagerAdapter {
    int frag = 0;
    theStories activity = new theStories();


    public SimpleStoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            activity.setFragPos(position);
            return new My_Stories_Fragment();
        }
        /*else if (position == 1){
            activity.setFragPos(position);
            return new Invited_Stories_Fragment();
        }*/
        else {
            activity.setFragPos(position);
            return new Local_Stories_Fragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                frag = 0;
                activity.setFragPos(position);
                return "MY STORIES";

            case 1:
                frag = 1;
                activity.setFragPos(position);
                return "LOCAL STORIES";
        }

        return null;
    }



}