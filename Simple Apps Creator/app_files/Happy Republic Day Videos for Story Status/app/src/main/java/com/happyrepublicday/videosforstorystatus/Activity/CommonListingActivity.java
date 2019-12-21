package com.happyrepublicday.videosforstorystatus.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.happyrepublicday.videosforstorystatus.Adapter.CommonListingAdapter;
import com.happyrepublicday.videosforstorystatus.ConnectionDetector;
import com.happyrepublicday.videosforstorystatus.Constant;
import com.happyrepublicday.videosforstorystatus.EndlessRecyclerOnScrollListener;
import com.happyrepublicday.videosforstorystatus.R;
import com.happyrepublicday.videosforstorystatus.gettersetter.ItemData;
import com.happyrepublicday.videosforstorystatus.gettersetter.Item_collections;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.happyrepublicday.videosforstorystatus.Constant.NUMBER_OF_ADS;


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
    private InterstitialAd mInterstitialAd;

    private AdLoader adLoader;

    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();


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

        initInterstitialAdPrepare();

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
                    case CommonListingAdapter.AD_TYPE:
                        return 2;
                    case CommonListingAdapter.VIEW_TYPE_NULL:
                        return 2;
                    default:
                        return -1;
                }
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().setStatusBarColor(Color.TRANSPARENT);
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            } else {
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            }
//        }

        currentpage = 1;
        Loadcatwisedata(currentpage);
    }


    public void Loadcatwisedata(int page) {
        if (page == 1) {
            loadNativeAds(page);
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
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progress.isShowing()){
                        progress.dismiss();
                    }
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }else{
                        callnewpage();
                    }
                }
            }, 1500);

        }else{
            Constant.Adscountlisting++;
            callnewpage();
        }
    }

    public void callnewpage(){
        Intent intent = new Intent(CommonListingActivity.this, DetailActivity.class);
        startActivity(intent);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void initInterstitialAdPrepare() {
        mInterstitialAd = new InterstitialAd(CommonListingActivity.this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Constant.Adscountlisting = 1;
                callnewpage();
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
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
                    if (getpagenumber != 1){
                        //if (!adLoader.isLoading()) {
                            catwiseadapter.insertAdsInMenuItems(mNativeAds,getpagenumber);
                       // }
                    }
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


    private void loadNativeAds(final int page) {

        AdLoader.Builder builder = new AdLoader.Builder(CommonListingActivity.this, getResources().getString(R.string.admob_native_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        mNativeAds.add(unifiedNativeAd);
                        if (page == 1){
                            if (!adLoader.isLoading()) {
                                catwiseadapter.insertAdsInMenuItems(mNativeAds,page);
                            }
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        if (page == 1){
                            if (!adLoader.isLoading()) {
                                catwiseadapter.insertAdsInMenuItems(mNativeAds,page);
                            }
                        }
                    }
                }).build();

        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }
}


