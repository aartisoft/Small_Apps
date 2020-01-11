package com.happymothersdayquotes.status.wishesand.Images.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.happymothersdayquotes.status.wishesand.Images.Fragment.ImageFragment;
import com.happymothersdayquotes.status.wishesand.Images.Fragment.StatusFragment;
import com.happymothersdayquotes.status.wishesand.Images.Fragment.VideoFragment;
import com.happymothersdayquotes.status.wishesand.Images.R;

import java.util.ArrayList;


import com.happymothersdayquotes.status.wishesand.Images.AwesomeTabBarAdapter;


public class PagerAdapter extends AwesomeTabBarAdapter
{
    ArrayList<Fragment> fragments=new ArrayList<>();
    ArrayList<String> titles=new ArrayList<>();
    int[] colors={R.color.white,R.color.white,R.color.white};
    int[] textColors={android.R.color.black};
    int[] icons={R.drawable.ic_status_icon,R.drawable.ic_image_icon,R.drawable.ic_video_camera};

    public PagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
        fragments.add(new ImageFragment());
        fragments.add(new VideoFragment());
        fragments.add(new StatusFragment());

        titles.add("Images");
        titles.add("Videos");
        titles.add("Status");

    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getColorResource(int position) {
        return colors[position];
    }

    @Override
    public int getTextColorResource(int position) {
        return textColors[0];
    }

    @Override
    public int getIconResource(int position) {
        return icons[position];
    }
}
