package com.Kyoukai.lifecounter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int f) {
        Fragment lifeFrag = new FragmentLife();
        Fragment menuFrag = new FragmentMenu();
        switch(f) {
            case 0:
                return lifeFrag;
            case 1:
                return menuFrag;
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
