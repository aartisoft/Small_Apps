package com.teluguvideostatusfor.forwhatsapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.teluguvideostatusfor.forwhatsapp.Activity.DetailActivity;
import com.teluguvideostatusfor.forwhatsapp.Activity.MainActivity;
import com.teluguvideostatusfor.forwhatsapp.Adapter.FavoriteListingAdapter;
import com.teluguvideostatusfor.forwhatsapp.ConnectionDetector;
import com.teluguvideostatusfor.forwhatsapp.Constant;
import com.teluguvideostatusfor.forwhatsapp.DbAdapter;
import com.teluguvideostatusfor.forwhatsapp.R;
import com.teluguvideostatusfor.forwhatsapp.Utility;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.Item_Favgetset;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.Item_collections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class DownloadFragment extends Fragment implements FavoriteListingAdapter.MyClickListener,Constant.Callingafterads  {
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

    public DownloadFragment() {
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
        Log.e("showdatacall "," calling");
        fav_item_list = new ArrayList<Item_Favgetset>();
        boolean result = Utility.checkPermission(getActivity());
        if (result) {
            db = new DbAdapter(getActivity());
            db.open();
            Cursor row = db.fetchAllDownload();
            for (row.moveToFirst(); !row.isAfterLast(); row.moveToNext()) {
                String Item_id = row.getString(row.getColumnIndex("item_id"));
                String video_url = row.getString(row.getColumnIndex("video_url"));
                if (video_url != null && video_url.length() > 0){
                    File file = new File ( video_url );
                    if ( file.exists() ){
                        String item_name = row.getString(row.getColumnIndex("item_name"));
                        String image_url = row.getString(row.getColumnIndex("image_url"));
                        String total_views = row.getString(row.getColumnIndex("total_views"));
                        String is_type = row.getString(row.getColumnIndex("is_type"));
                        String date = row.getString(row.getColumnIndex("date"));
                        String cat_id = row.getString(row.getColumnIndex("cat_id"));
                        String cat_name = row.getString(row.getColumnIndex("cat_name"));
                        fav_item_list.add(new Item_Favgetset(Item_id, item_name,video_url,image_url,total_views,is_type,date,cat_id,cat_name));
                    }else{
                        db.deletedownload(Item_id);
                    }
                }else{
                    db.deletedownload(Item_id);
                }
            }

        }
        int ii = fav_item_list.size();
        if (ii <= 0) {
            favorite_recycler.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);
            no_data_text.setText("No Video Download Yet!");
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
        showData();

    }


    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity)getActivity()).SelectItem(getActivity().getResources().getString(R.string.app_name),0);

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
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.checkPermission(getActivity());
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showData();
                } else {
                   snackbarcommonrelativeLong(getActivity(), content_favorite, "Please give permission to show your Download Videos!");
                }
                return;
            }
        }
    }
}