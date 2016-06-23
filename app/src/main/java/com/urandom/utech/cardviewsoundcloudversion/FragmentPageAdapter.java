package com.urandom.utech.cardviewsoundcloudversion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by nopphonyel on 6/14/16.
 */
public class FragmentPageAdapter extends FragmentPagerAdapter {

    public static final int MAIN_ACTIVITY = 1110, NOW_PLAYING_ACTIVITY = 1111;

    public static final int PAGE_AMOUNT = 2;
    protected int pageMode = 0;

    private static final String TAG_FRAGMENT_ADAPTER = "Fragment Adapter";

    public FragmentPageAdapter(FragmentManager fm, int newMode) {
        super(fm);
        pageMode = newMode;
        Log.e(TAG_FRAGMENT_ADAPTER, "MODE = " + pageMode);
    }

    @Override
    public Fragment getItem(int position) {
        switch (pageMode) {
            case MAIN_ACTIVITY:
                if (position == 0) return new FragmentRandom();
                else if (position == 1) return new FragmentFavorite();
                break;
            case NOW_PLAYING_ACTIVITY:
                Log.e(TAG_FRAGMENT_ADAPTER, "Code NOW_PLAYING");
                if (position == 0) return new FragmentMusicDetail();
                else if (position == 1) return new FragmentQueue();
                break;
            default:
                return null;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_AMOUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (pageMode) {
            case MAIN_ACTIVITY:
                if (position == 0) return "Random Track";
                else if (position == 1) return "Favorite Track";
                break;
            case NOW_PLAYING_ACTIVITY:
                if (position == 0) return "Now playing";
                else if (position == 1) return "In queue";
        }
        return null;
    }

}

