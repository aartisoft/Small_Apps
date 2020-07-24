package com.techcorp.teluguvideostatus.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.techcorp.teluguvideostatus.DbAdapter;
import com.techcorp.teluguvideostatus.gettersetter.Item_Favgetset;
import com.techcorp.teluguvideostatus.Activity.DetailActivity;
import com.techcorp.teluguvideostatus.Activity.MainActivity;
import com.techcorp.teluguvideostatus.Adapter.FavoriteListingAdapter;
import com.techcorp.teluguvideostatus.ConnectionDetector;
import com.techcorp.teluguvideostatus.Constant;
import com.techcorp.teluguvideostatus.R;
import com.techcorp.teluguvideostatus.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class FavoriteFragment extends Fragment implements FavoriteListingAdapter.MyClickListener, Constant.Callingafterads {
    private ConnectionDetector detectorconn;

    private Gson gson;
    Boolean conn;
    Constant constantfile;
    RelativeLayout content_favorite;
    RecyclerView favorite_recycler;
    TextView no_data_text;
    ProgressBar progressbar;
    ArrayList<Item_Favgetset> fav_item_list;
    DbAdapter db;
    FavoriteListingAdapter mAdapter;
    View rootView;

    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.favorite_fragment, container, false);
        gson = new Gson();
        this.conn = null;
        constantfile = new Constant();
        fav_item_list = new ArrayList<>();
        db = new DbAdapter(getActivity());
        db.open();
        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        content_favorite = (RelativeLayout) rootView.findViewById(R.id.content_favorite);
        favorite_recycler = (RecyclerView) rootView.findViewById(R.id.favorite_recycler);
        progressbar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        no_data_text = (TextView) rootView.findViewById(R.id.no_data_text);


        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        favorite_recycler.setLayoutManager(mLayoutManager);
        favorite_recycler.setItemAnimator(new DefaultItemAnimator());
        favorite_recycler.setHasFixedSize(true);

        mAdapter = new FavoriteListingAdapter(getActivity());
        mAdapter.setClickListener(this);
        favorite_recycler.setAdapter(mAdapter);


        return rootView;
    }


    private void showData() {
        db = new DbAdapter(getActivity());
        db.open();
        fav_item_list = new ArrayList<Item_Favgetset>();
        Cursor row = db.fetchAllData();
        for (row.moveToFirst(); !row.isAfterLast(); row.moveToNext()) {
            fav_item_list.add(new Item_Favgetset(row.getString(row.getColumnIndex("item_id")), row.getString(row.getColumnIndex("item_name")),
                    row.getString(row.getColumnIndex("video_url")),row.getString(row.getColumnIndex("image_url")),
                    row.getString(row.getColumnIndex("total_views")),row.getString(row.getColumnIndex("is_type")),
                    row.getString(row.getColumnIndex("date")),row.getString(row.getColumnIndex("cat_id")),
                    row.getString(row.getColumnIndex("cat_name"))));
        }

        int ii = fav_item_list.size();
        if (ii <= 0) {
            favorite_recycler.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);
            no_data_text.setText("No Video in Favorite Yet!");
        } else {
            no_data_text.setVisibility(View.GONE);
            favorite_recycler.setVisibility(View.VISIBLE);
            mAdapter.adddata(fav_item_list);
        }
        progressbar.setVisibility(View.GONE);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();

        showData();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity) getActivity()).SelectItem(getActivity().getResources().getString(R.string.app_name), 0);
                }
                return true;
            }
        });

    }


    @Override
    public void onFavoriteItemClick(int position, Item_Favgetset passdata,ArrayList<Item_Favgetset> passarray, View v) {
        passarray.removeAll(Collections.singleton(null));
        Constant.Passing_item_object = new Item_collections();
        Constant.Passing_item_object = passdata;
        if (Constant.Adscountlisting == 2){
            constantfile.loadInterstitialAd(getActivity(),this);
        }else{
            Constant.Adscountlisting++;
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            getActivity().startActivity(intent);
        }
    }


    public void snackbarcommonrelativeLong(Context mcontext, View coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // LoadImagedata(currentpage);
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        TextView textaction = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textaction.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textaction.setTextColor(Color.BLACK);
        snackbar.show();
    }

    @Override
    public void onAdsresponce(Boolean showing) {
        Constant.Adscountlisting = 1;
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        getActivity().startActivity(intent);
    }

}