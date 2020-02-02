package com.tinyapps.fullscreen.videostatus.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tinyapps.fullscreen.videostatus.Activity.MainActivity;
import com.tinyapps.fullscreen.videostatus.Activity.SearchActivity;
import com.tinyapps.fullscreen.videostatus.ConnectionDetector;
import com.tinyapps.fullscreen.videostatus.Constant;
import com.tinyapps.fullscreen.videostatus.R;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "Main_list";
    private ConnectionDetector detectorconn;
    Boolean conn;
    Constant constantfile;
    RelativeLayout content_home;
    BottomNavigationView navigation;
    View rootView;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homefragment, container, false);
        setHasOptionsMenu(true);
        this.conn = null;
        constantfile = new Constant();
        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        content_home = (RelativeLayout) rootView.findViewById(R.id.content_home);


        navigation = (BottomNavigationView) rootView.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SelectItem(Constant.Passing_selected_fragment);
        Constant.Passing_selected_fragment = 0;
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    public void SelectItem(int fragmentpos) {
        String title = getActivity().getResources().getString(R.string.app_name);
        if (fragmentpos == 3) {
            title = getActivity().getResources().getString(R.string.title_categories);
        } else if (fragmentpos == 2) {
            title = getActivity().getResources().getString(R.string.title_favorite);
        } else if (fragmentpos == 1) {
            title = getActivity().getResources().getString(R.string.title_trending);
        }
        ((MainActivity) getActivity()).changetitle(title);
        Fragment fragment = null;
        if (fragmentpos < 0) {
            return;
        } else {
            if (fragmentpos == 0) {
                fragment = new MainFragment();
            } else if (fragmentpos == 1) {
                fragment = new TrendingFragment();
            } else if (fragmentpos == 2) {
                fragment = new FavoriteFragment();
            } else if (fragmentpos == 3) {
                fragment = new CategoriesFragment();
            }

        }

        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            navigation.getMenu().getItem(fragmentpos).setChecked(true);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                return true;
            default:
                break;
        }

        return false;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    SelectItem(0);
                    return true;
                case R.id.navigation_latest:
                    SelectItem(1);
                    return true;
                case R.id.navigation_favorite:
                    SelectItem(2);
                    return true;
                case R.id.navigation_categories:
                    SelectItem(3);
                    return true;
            }
            return false;
        }

    };
}
