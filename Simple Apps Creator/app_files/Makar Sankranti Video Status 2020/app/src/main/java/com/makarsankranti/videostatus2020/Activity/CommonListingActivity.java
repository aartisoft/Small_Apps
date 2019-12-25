package com.makarsankranti.videostatus2020.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.makarsankranti.videostatus2020.Adapter.CommonListingAdapter;
import com.makarsankranti.videostatus2020.ConnectionDetector;
import com.makarsankranti.videostatus2020.Constant;
import com.makarsankranti.videostatus2020.EndlessRecyclerOnScrollListener;
import com.makarsankranti.videostatus2020.R;
import com.makarsankranti.videostatus2020.gettersetter.ItemData;
import com.makarsankranti.videostatus2020.gettersetter.Item_collections;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.makarsankranti.videostatus2020.Constant.NUMBER_OF_ADS;


public class CommonListingActivity extends AppCompatActivity implements CommonListingAdapter.MyClickListener {

    Constant constantfile;
    private TextView no_data_tv;
    RecyclerView category_item_rv;
    FrameLayout catewise_fl;
    ProgressBar progressbar;
    private Boolean conn;
    private ConnectionDetector detectorconn;
    private Gson gson;
    private boolean mIsLoadingMore;
    CommonListingAdapter catwiseadapter;
    private int currentpage = 1;
    String gettype="";
    private Toolbar mTopToolbar;

    StartAppAd startAppAd = new StartAppAd(CommonListingActivity.this);

    //private StartAppNativeAd mStartAppNativeAd = new StartAppNativeAd(this);

    @Override
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        startAppAd.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState){
        startAppAd.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_layout);
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTopToolbar.setTitle(Constant.Passing_From_title+"");
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gettype = Constant.Passing_From;
        gson = new Gson();
        this.detectorconn = new ConnectionDetector(CommonListingActivity.this);
        this.conn = null;
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        this.currentpage = 1;
        this.mIsLoadingMore = false;

        constantfile = new Constant();
        catewise_fl = findViewById(R.id.catewise_fl);

        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
        category_item_rv = findViewById(R.id.category_item_rv);
        no_data_tv = (TextView) findViewById(R.id.no_data_tv);
        no_data_tv.setVisibility(View.GONE);
        category_item_rv.setHasFixedSize(true);

        GridLayoutManager mLayoutManager = new GridLayoutManager(CommonListingActivity.this, 2);
        category_item_rv.setLayoutManager(mLayoutManager);
        category_item_rv.setItemAnimator(new DefaultItemAnimator());
        category_item_rv.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (mIsLoadingMore) {
                    currentpage = current_page;
                    Loadcatwisedata(current_page);
                }
            }
        });
        catwiseadapter = new CommonListingAdapter(CommonListingActivity.this,Constant.Passing_From);
        catwiseadapter.setClickListener(this);
        category_item_rv.setAdapter(catwiseadapter);

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (catwiseadapter.getItemViewType(position)) {
                    case CommonListingAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case CommonListingAdapter.VIEW_TYPE_LOADING:
                        return 2;
                    default:
                        return -1;
                }
            }
        });

        currentpage = 1;
        Loadcatwisedata(currentpage);

//        mStartAppNativeAd.loadAd(
//                new NativeAdPreferences()
//                        .setAdsNumber(10)
//                        .setAutoBitmapDownload(true)
//                        .setPrimaryImageSize(2),
//                mNativeAdListener);

    }

//    private AdEventListener mNativeAdListener = new AdEventListener() {
//
//        @Override
//        public void onReceiveAd(Ad ad) {
//            // Get the native ads
//            catwiseadapter.setItems(mStartAppNativeAd.getNativeAds());
//        }
//
//        @Override
//        public void onFailedToReceiveAd(Ad ad) {
//        }
//    };


    public void Loadcatwisedata(int page) {
        if (page == 1) {
            catwiseadapter.clearalldata();
        }
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            getCatData(page);
        } else {
            progressbar.setVisibility(View.GONE);
            if (currentpage == 1) {
                mIsLoadingMore = false;
                category_item_rv.setVisibility(View.GONE);
                no_data_tv.setVisibility(View.VISIBLE);
            }
            snackbarcommonrelativeLong(CommonListingActivity.this, catewise_fl, "No Internet Connection!");
        }
    }


    public void getCatData(int pageget) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put(gettype, gettype);
        params.put("app_id", Constant.APP_ID);
        params.put("page", pageget);
        client.get(Constant.GET_COMMON_LISTING, params, new AsynchronouseData(pageget));
    }


    @Override
    public void onCommonItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v) {
        Constant.Passing_item_object = new Item_collections();
        Constant.Passing_item_object = passdata;
        if (Constant.Adscountlisting == 2){
            final ProgressDialog progress = new ProgressDialog(CommonListingActivity.this, R.style.MyAlertDialogStyle);
            progress.setMessage("Loading Ad");
            progress.setCancelable(false);
            progress.show();
            startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC,new AdEventListener() {
                @Override
                public void onReceiveAd(Ad ad) {
                    Constant.Adscountlisting = 1;
                    if (progress.isShowing()){
                        progress.dismiss();
                    }
                    startAppAd.showAd(new AdDisplayListener() {
                        @Override
                        public void adHidden(Ad ad) {
                            callnewpage();
                        }
                        @Override
                        public void adDisplayed(Ad ad) { }
                        @Override
                        public void adClicked(Ad ad) { }
                        @Override
                        public void adNotDisplayed(Ad ad) {
                            callnewpage();
                        }
                    });
                }
                @Override
                public void onFailedToReceiveAd(Ad ad) {
                }
            });
        }else{
            Constant.Adscountlisting++;
            callnewpage();
        }
    }


    public void callnewpage(){
        Intent intent = new Intent(CommonListingActivity.this, DetailActivity.class);
        startActivity(intent);
    }



    class AsynchronouseData extends JsonHttpResponseHandler {

        int getpagenumber;

        AsynchronouseData(int pagenumber) {
            this.getpagenumber = pagenumber;
        }

        public void onStart() {
            super.onStart();
            if (getpagenumber == 1){
                progressbar.setVisibility(View.VISIBLE);
            }
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {

            try {
                JSONArray getdata = bytes.getJSONArray("data");
                String string = new Gson().toJson(getdata);
                Log.e("getseearchdata", " get data   :::   " + string);
                ItemData statusData = new ItemData();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                statusData.setLimit(bytes.getString("limit"));
                statusData.setData(constantfile.ConvertJSONtoModel(getdata));

                if (statusData.isStatus()) {

                    mIsLoadingMore = true;
                    if (statusData.getData().size() < Integer.parseInt(statusData.getLimit())) {
                        mIsLoadingMore = false;
                    }
                    category_item_rv.setVisibility(View.VISIBLE);
                    no_data_tv.setVisibility(View.GONE);
                    catwiseadapter.adddata(statusData.getData(), getpagenumber);
                } else {
                    progressbar.setVisibility(View.GONE);
                    if (getpagenumber == 1) {
                        mIsLoadingMore = false;
                        category_item_rv.setVisibility(View.GONE);
                        no_data_tv.setVisibility(View.VISIBLE);
                    }
                }
                progressbar.setVisibility(View.GONE);

            } catch (Exception e) {
                progressbar.setVisibility(View.GONE);
                if (getpagenumber == 1) {
                    category_item_rv.setVisibility(View.GONE);
                    no_data_tv.setVisibility(View.VISIBLE);
                }
                mIsLoadingMore = false;
            }

            if (!mIsLoadingMore) {
                catwiseadapter.setnomoredata();
            }
        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {
            if (!mIsLoadingMore) {
                catwiseadapter.setnomoredata();
            }
            progressbar.setVisibility(View.GONE);
            if (getpagenumber == 1) {
                category_item_rv.setVisibility(View.GONE);
                no_data_tv.setVisibility(View.VISIBLE);
            }

        }
    }


    public void snackbarcommonrelativeLong(Context mcontext, FrameLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loadcatwisedata(currentpage);
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
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

