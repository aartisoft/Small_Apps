package com.mi9t.k20prowallpapers.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.snackbar.Snackbar;
import com.mi9t.k20prowallpapers.Adapter.ImageAdapter;
import com.mi9t.k20prowallpapers.ConnectionDetector;
import com.mi9t.k20prowallpapers.Constant;
import com.mi9t.k20prowallpapers.DbAdapter;
import com.mi9t.k20prowallpapers.R;
import com.mi9t.k20prowallpapers.gettersetter.Item_collections;

import java.util.ArrayList;

import static com.mi9t.k20prowallpapers.Constant.passing_from;
import static com.mi9t.k20prowallpapers.Constant.passing_object;

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
        passing_object = new Item_collections();
        passing_object = passarray.get(position);
        passing_from = 2;
        if (Constant.Adscount >= 2){
            loadInterstitialAd(ImgUrl);
        }else{
            Constant.Adscount++;
            callnextscreen(ImgUrl);
        }
    }


    private void loadInterstitialAd(final String imgURL) {
        final ProgressDialog progress = new ProgressDialog(FavoriteActivity.this, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        final InterstitialAd interstitialAd = new InterstitialAd(FavoriteActivity.this, getResources().getString(R.string.facebook_interstitial_id));
        interstitialAd.loadAd();
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (interstitialAd != null) {
                    interstitialAd.destroy();
                }
                Constant.Adscount = 1;
                callnextscreen(imgURL);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (interstitialAd != null) {
                    interstitialAd.destroy();
                }
                Constant.Adscount = 1;
                callnextscreen(imgURL);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });

    }


    public void callnextscreen(String imgURL) {
        Intent catwise = new Intent(FavoriteActivity.this, Home_SingleItem_Activity.class);
        catwise.putExtra("image_url", imgURL + "");
        startActivity(catwise);
    }
}