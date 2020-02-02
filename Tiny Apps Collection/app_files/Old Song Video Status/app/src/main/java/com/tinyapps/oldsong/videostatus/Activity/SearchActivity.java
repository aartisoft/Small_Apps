package com.tinyapps.oldsong.videostatus.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.tinyapps.oldsong.videostatus.Adapter.SearchListingAdapter;
import com.tinyapps.oldsong.videostatus.ConnectionDetector;
import com.tinyapps.oldsong.videostatus.Constant;
import com.tinyapps.oldsong.videostatus.EndlessRecyclerOnStraggered;
import com.tinyapps.oldsong.videostatus.R;
import com.tinyapps.oldsong.videostatus.gettersetter.ItemData;
import com.tinyapps.oldsong.videostatus.gettersetter.Item_collections;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;


public class SearchActivity extends AppCompatActivity implements SearchListingAdapter.MyClickListener, Constant.Callingafterads {


    public static final String SEARCH_RL_TRANSITION_NAME = "searchrelativelayout";

    Constant constantfile;
    private TextView no_data_tv, first_time_tv;
    RecyclerView category_item_rv;
    LinearLayout search_ll;
    ProgressBar progressbar;
    private Boolean conn;
    private ConnectionDetector detectorconn;
    private Gson gson;
    private boolean mIsLoadingMore;
    SearchListingAdapter catwiseadapter;
    private int currentpage = 1;
    String searchstring = "";
    SearchView searchView;
    private Toolbar mTopToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_layout);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mTopToolbar);

        gson = new Gson();
        this.detectorconn = new ConnectionDetector(SearchActivity.this);
        this.conn = null;
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        this.currentpage = 1;
        this.mIsLoadingMore = false;


        constantfile = new Constant();
        search_ll = findViewById(R.id.search_ll);

        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
        category_item_rv = findViewById(R.id.category_item_rv);
        no_data_tv = (TextView) findViewById(R.id.no_data_tv);
        no_data_tv.setVisibility(View.GONE);
        first_time_tv = (TextView) findViewById(R.id.first_time_tv);
        first_time_tv.setVisibility(View.VISIBLE);
        category_item_rv.setHasFixedSize(true);



        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        category_item_rv.setLayoutManager(mLayoutManager);
        category_item_rv.setItemAnimator(new DefaultItemAnimator());
        category_item_rv.setOnScrollListener(new EndlessRecyclerOnStraggered(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (mIsLoadingMore) {
                    currentpage = current_page;
                    Loadcatwisedata(current_page);
                }
            }
        });
        catwiseadapter = new SearchListingAdapter(SearchActivity.this);
        catwiseadapter.setClickListener(this);
        category_item_rv.setAdapter(catwiseadapter);

//        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                switch (catwiseadapter.getItemViewType(position)) {
//                    case SearchListingAdapter.VIEW_TYPE_PORT:
//                        return 1;
//                    case SearchListingAdapter.VIEW_TYPE_LAND:
//                        return 1;
//                    case SearchListingAdapter.VIEW_TYPE_LOADING:
//                        return 2;
//                    default:
//                        return -1;
//                }
//            }
//        });

        currentpage = 1;
    }


    public void Loadcatwisedata(int page) {
        if (page == 1) {
            catwiseadapter.clearalldata();
        }
        first_time_tv.setVisibility(View.GONE);
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
            snackbarcommonrelativeLong(SearchActivity.this, search_ll, "No Internet Connection!");
        }
    }

    public void getCatData(int pageget) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("search", searchstring);
        params.put("page", pageget);
        params.put("lang_id", Constant.LANGUAGE_ID);
        client.get(Constant.GET_SEARCH_LISTING, params, new AsynchronouseData(pageget));
    }


    @Override
    public void onSearchItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v) {
        passarray.removeAll(Collections.singleton(null));
        Constant.Passing_item_object = new Item_collections();
        Constant.Passing_item_object = passdata;
        if (Constant.Adscountlisting == 2){
            constantfile.loadInterstitialAd(SearchActivity.this,this);
        }else{
            Constant.Adscountlisting++;
            Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onAdsresponce(Boolean showing) {
        Constant.Adscountlisting = 1;
        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
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
                    mIsLoadingMore = false;
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

    }


    public void snackbarcommonrelativeLong(Context mcontext, View coordinatorLayout, String snackmsg) {
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
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
//        }
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.act_search).getActionView();
        View v = searchView.findViewById(com.google.android.material.R.id.search_plate);
        v.setBackgroundColor(getResources().getColor(R.color.white));
        ImageView searchClose = searchView.findViewById(com.google.android.material.R.id.search_close_btn);
        searchClose.setColorFilter(ContextCompat.getColor(SearchActivity.this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(com.google.android.material.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.black));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.black));


        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search...");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mIsLoadingMore = true;
                currentpage = 1;
                searchstring = query + "";
                if (searchstring.length() > 0) {
                    Loadcatwisedata(currentpage);
                } else {
                    constantfile.snackbarcommonview(SearchActivity.this, search_ll, "Please Write Anything for Search!");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
//                if (query.length() <= 0) {
//                    mIsLoadingMore = true;
//                    currentpage = 1;
//                    searchstring = query + "";
//                    Loadcatwisedata(currentpage);
//                }
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.act_search), new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return true;
            }
        });

        MenuItem item = menu.findItem(R.id.act_search);
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.toolbar));
        MenuItemCompat.expandActionView(item);


        return true;
    }


}


