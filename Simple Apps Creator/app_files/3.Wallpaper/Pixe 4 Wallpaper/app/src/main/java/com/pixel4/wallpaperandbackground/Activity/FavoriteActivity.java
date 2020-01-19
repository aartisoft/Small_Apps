package com.pixel4.wallpaperandbackground.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appnext.ads.AdsError;
import com.appnext.ads.interstitial.Interstitial;
import com.appnext.core.AppnextAdCreativeType;
import com.appnext.core.callbacks.OnAdClicked;
import com.appnext.core.callbacks.OnAdClosed;
import com.appnext.core.callbacks.OnAdError;
import com.appnext.core.callbacks.OnAdLoaded;
import com.appnext.core.callbacks.OnAdOpened;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.snackbar.Snackbar;
import com.pixel4.wallpaperandbackground.Adapter.ImageAdapter;
import com.pixel4.wallpaperandbackground.ConnectionDetector;
import com.pixel4.wallpaperandbackground.Constant;
import com.pixel4.wallpaperandbackground.DbAdapter;
import com.pixel4.wallpaperandbackground.R;
import com.pixel4.wallpaperandbackground.gettersetter.Item_collections;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements ImageAdapter.MyClickListener {
    TextView tool_title;
    Toolbar toolbar;
    ImageView back;
    Constant constantfile;
    DrawerLayout drawer_layout;
    private ConnectionDetector detectorconn;
    Boolean conn;
    private ProgressBar progressBar;
    TextView no_data_text;
    RecyclerView Image_list;
    ImageAdapter mAdapter;
    ArrayList<Item_collections> fav_item_list;
    DbAdapter db;

    SharedPreferences pref;
    LinearLayout content_fav;
    String PassUrl = "";


    Interstitial interstitial_Ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tool_title = (TextView) toolbar.findViewById(R.id.tool_title);
        drawer_layout = (DrawerLayout) toolbar.findViewById(R.id.drawer_layout);
        content_fav = (LinearLayout) findViewById(R.id.content_fav);
        if(!AudienceNetworkAds.isInitialized(FavoriteActivity.this)){
            AudienceNetworkAds.initialize(FavoriteActivity.this);
        }
        this.conn = null;
        constantfile = new Constant();
        fav_item_list = new ArrayList<>();
        db = new DbAdapter(FavoriteActivity.this);
        db.open();

        intializeappnextads();

        this.detectorconn = new ConnectionDetector(getApplicationContext());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        back = (ImageView) findViewById(R.id.back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        no_data_text = (TextView) findViewById(R.id.no_data_text);
        no_data_text.setVisibility(View.GONE);

        Image_list = (RecyclerView) findViewById(R.id.Image_list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(FavoriteActivity.this, 3);
        Image_list.setLayoutManager(mLayoutManager);
        Image_list.setItemAnimator(new DefaultItemAnimator());
        Image_list.setHasFixedSize(true);

        mAdapter = new ImageAdapter(FavoriteActivity.this);
        mAdapter.setClickListener(this);
        Image_list.setAdapter(mAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case ImageAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case ImageAdapter.VIEW_TYPE_LOADING:
                        return 3;
                    default:
                        return -1;
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        pref = getApplicationContext().getSharedPreferences(Constant.SHARED_PREF, 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
        if (interstitial_Ad != null){
            loadappnextads();
        }
    }

    private void showData() {
        mAdapter.clearalldata();
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            fav_item_list = new ArrayList<Item_collections>();
            Cursor row = db.fetchAllData();
            for (row.moveToFirst(); !row.isAfterLast(); row.moveToNext()) {
                Item_collections item = new Item_collections();
                item.setId(row.getString(row.getColumnIndex("item_id"))+"");
                item.setWall_name(row.getString(row.getColumnIndex("name"))+"");
                item.setWallpaper_image(row.getString(row.getColumnIndex("image"))+"");
                item.setWallpaper_image_thumb(row.getString(row.getColumnIndex("image_thumb"))+"");
                item.setTotal_views(row.getString(row.getColumnIndex("tview"))+"");
                item.setTotal_download(row.getString(row.getColumnIndex("tdownload"))+"");
                item.setWall_tags(row.getString(row.getColumnIndex("tags"))+"");
                item.setDate(row.getString(row.getColumnIndex("date"))+"");
                fav_item_list.add(item);
            }

            int ii = fav_item_list.size();
            if (ii <= 0) {
                no_data_text.setVisibility(View.VISIBLE);
                Image_list.setVisibility(View.GONE);
            } else {
                no_data_text.setVisibility(View.GONE);
                Image_list.setVisibility(View.VISIBLE);
                mAdapter.adddata(fav_item_list, 1);
                mAdapter.setnomoredata();
            }
        } else {
            Image_list.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);
            snackbarcommonrelativeLong(FavoriteActivity.this, content_fav, "No Internet Connection!");
        }
    }

    public void snackbarcommonrelativeLong(Context mcontext, View coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showData();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        TextView textaction = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
        textView.setTextSize(16);
        textaction.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textaction.setTextColor(Color.BLACK);
        snackbar.show();
    }


    @Override
    public void onBackPressed() {
        super.finish();
        finish();
    }


    @Override
    public void onItemClick(int position, String ImgUrl, ArrayList<Item_collections> passarray, View v) {
        Constant.passing_object = new Item_collections();
        Constant.passing_object = passarray.get(position);
        Constant.passing_from = 2;
        PassUrl = ImgUrl;
        if (Constant.Adscount >= 2){
            callingadsonclick();
        }else{
            Constant.Adscount++;
            callnextscreen();
        }
    }

    public void intializeappnextads() {
        interstitial_Ad = new Interstitial(FavoriteActivity.this, getResources().getString(R.string.appnext_placement_id));
        interstitial_Ad.setOnAdLoadedCallback(new OnAdLoaded() {
            @Override
            public void adLoaded(String bannerId, AppnextAdCreativeType creativeType) {

            }
        });
        interstitial_Ad.setOnAdOpenedCallback(new OnAdOpened() {
            @Override
            public void adOpened() {

            }
        });
        interstitial_Ad.setOnAdClickedCallback(new OnAdClicked() {
            @Override
            public void adClicked() {

            }
        });
        interstitial_Ad.setOnAdClosedCallback(new OnAdClosed() {
            @Override
            public void onAdClosed() {
                Constant.Adscount = 1;
                callnextscreen();
            }
        });
        interstitial_Ad.setOnAdErrorCallback(new OnAdError() {
            @Override
            public void adError(String error) {
                switch (error) {
                    case AdsError.NO_ADS:
                        Log.v("appnext", "no ads" + error);
                        break;
                    case AdsError.CONNECTION_ERROR:
                        Log.v("appnext", "connection problem");
                        break;
                    default:
                        Log.v("appnext", "other error");
                }
            }
        });
    }


    public void loadappnextads() {
        if (interstitial_Ad != null) {
            interstitial_Ad.loadAd();
        }
    }

    public void callingadsonclick() {
        final ProgressDialog progress = new ProgressDialog(FavoriteActivity.this, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        if (interstitial_Ad.isAdLoaded()) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    interstitial_Ad.showAd();
                }
            }, 1500);

        } else {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            callnextscreen();
        }

    }


    public void callnextscreen() {
        Intent catwise = new Intent(FavoriteActivity.this, Home_SingleItem_Activity.class);
        catwise.putExtra("image_url", PassUrl + "");
        startActivity(catwise);
    }
}