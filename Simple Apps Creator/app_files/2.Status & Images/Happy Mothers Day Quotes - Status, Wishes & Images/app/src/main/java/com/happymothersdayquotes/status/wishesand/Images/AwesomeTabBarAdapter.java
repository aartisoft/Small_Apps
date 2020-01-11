package com.happymothersdayquotes.status.wishesand.Images;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public abstract class AwesomeTabBarAdapter extends FragmentPagerAdapter {

    public AwesomeTabBarAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public abstract int getCount();

    public abstract Fragment getItem(int position);

    public abstract CharSequence getPageTitle(int position);

    public abstract int getColorResource(int position);

    public abstract int getTextColorResource(int position);

    public abstract int getIconResource(int position);
}
