package com.techcorp.bhagatsingh.quotes.Fragment;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.appnext.ads.AdsError;
import com.appnext.ads.interstitial.Interstitial;
import com.appnext.core.AppnextAdCreativeType;
import com.appnext.core.callbacks.OnAdClicked;
import com.appnext.core.callbacks.OnAdClosed;
import com.appnext.core.callbacks.OnAdError;
import com.appnext.core.callbacks.OnAdLoaded;
import com.appnext.core.callbacks.OnAdOpened;
import com.techcorp.bhagatsingh.quotes.Activity.DetailActivity;
import com.techcorp.bhagatsingh.quotes.Activity.MainActivity;
import com.techcorp.bhagatsingh.quotes.Adapter.MainItemAdapter;
import com.techcorp.bhagatsingh.quotes.ConnectionDetector;
import com.techcorp.bhagatsingh.quotes.Constant;
import com.techcorp.bhagatsingh.quotes.EndlessRecyclerOnScrollListener;
import com.techcorp.bhagatsingh.quotes.R;
import com.techcorp.bhagatsingh.quotes.gettersetter.ItemImageData;
import com.techcorp.bhagatsingh.quotes.gettersetter.Item_images;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class InsideListFragment extends Fragment implements MainItemAdapter.MyClickListener{

    public static final String TAG = "Main_list";
    private ConnectionDetector detectorconn;
    Boolean conn;
    Constant constantfile;
    private AlertDialog alert;
    RelativeLayout content_home;
    RecyclerView items_recycler;
    TextView no_data_text;
    MainItemAdapter mainAdapter;
    private SearchView searchView;

    private ProgressBar progressBar;

    private int currentpage = 1;
    private boolean mIsLoadingMore;
    String searchtext = "";

    Interstitial interstitial_Ad;

   // StartAppAd startAppAd;

    public InsideListFragment() {
    }

    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        //startAppAd.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState (Bundle savedInstanceState){
       // startAppAd.onRestoreInstanceState(savedInstanceState);
        // super.onRestoreInstanceState(savedInstanceState);
    }



    @Override
    public void onPause() {
        super.onPause();
      //  startAppAd.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.insidelistfragment, container, false);
        setHasOptionsMenu(true);
        this.conn = null;
        constantfile = new Constant();

        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        this.currentpage = 1;
        this.mIsLoadingMore = false;

       // startAppAd = new StartAppAd(getActivity());
        intializeappnextads();

        content_home = (RelativeLayout) rootView.findViewById(R.id.content_home);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        items_recycler = (RecyclerView) rootView.findViewById(R.id.items_recycler);
        no_data_text = (TextView) rootView.findViewById(R.id.no_data_text);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        items_recycler.setLayoutManager(mLayoutManager);
        items_recycler.setItemAnimator(new DefaultItemAnimator());
        items_recycler.setHasFixedSize(true);
        items_recycler.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (mIsLoadingMore) {
                    currentpage = current_page;
                    LoadImagedata(current_page, searchtext);
                }
            }
        });
        mainAdapter = new MainItemAdapter(getActivity());
        mainAdapter.setClickListener(this);
        items_recycler.setAdapter(mainAdapter);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentpage = 1;
        this.mIsLoadingMore = false;
        searchtext = "";
        LoadImagedata(currentpage, searchtext);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        View v = searchView.findViewById(R.id.search_plate);
        v.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search...");
        LinearLayout searchEditFrame = (LinearLayout) searchView.findViewById(R.id.search_edit_frame);
        ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = -25;
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (((MainActivity) getActivity()).getSupportActionBar() != null) {
                    ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (((MainActivity) getActivity()).getSupportActionBar() != null) {
                    ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorPrimary)));
                }
                mIsLoadingMore = true;
                currentpage = 1;
                searchtext = "";
                LoadImagedata(currentpage, searchtext);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mIsLoadingMore = true;
                currentpage = 1;
                if (query.length() > 0) {
                    searchtext = query;
                    LoadImagedata(currentpage, searchtext);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                TransitionManager.beginDelayedTransition((ViewGroup) getActivity().findViewById(R.id.toolbar));
                MenuItemCompat.expandActionView(item);
                return true;
            default:
                break;
        }

        return false;
    }



    @Override
    public void onResume() {
        super.onResume();
       // startAppAd.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    callingbackpressed();
                }
                return true;
            }
        });

    }

    public void callingbackpressed(){
         if (searchView.isIconified()) {
            Log.e("callbacklistner","callinginside");
            ((MainActivity) getActivity()).setfragmentpos();
            //((MainActivity) getActivity()).changetitle(getActivity().getResources().getString(R.string.app_name));
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack(Constant.Passing_From, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ((MainActivity) getActivity()).changetitle(getActivity().getResources().getString(R.string.app_name));
         }else{
             Log.e("callbacklistner","callingsearchview");
             searchView.setIconified(true);
         }
    }


    @Override
    public void onItemClick(int position, Item_images getarray, View v) {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            if (((MainActivity) getActivity()).getSupportActionBar() != null) {
                ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorPrimary)));
            }
        }
        Constant.Passing_item_id = getarray.getId();
        Constant.Passing_item_objct = new Item_images();
        Constant.Passing_item_objct = getarray;
        if (Constant.Adscountlisting >= 2){
            //callstartappads();
            callingadsonclick();
        }else{
            Constant.Adscountlisting++;
            onAdsresponce();
        }
    }

    public void intializeappnextads(){
        interstitial_Ad = new Interstitial(getActivity(), getActivity().getResources().getString(R.string.appnext_placement_id));
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
                onAdsresponce();
                loadappnextads();
            }
        });
        interstitial_Ad.setOnAdErrorCallback(new OnAdError() {
            @Override
            public void adError(String error) {
                switch (error){
                    case AdsError.NO_ADS:
                        Log.v("appnext", "no ads");
                        break;
                    case AdsError.CONNECTION_ERROR:
                        Log.v("appnext", "connection problem");
                        break;
                    default:
                        Log.v("appnext", "other error");
                }
            }
        });

        loadappnextads();
    }

    public void loadappnextads(){
        if (interstitial_Ad != null){
            interstitial_Ad.loadAd();
        }
    }

    public void callingadsonclick(){
        final ProgressDialog progress = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        if (interstitial_Ad.isAdLoaded()){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    Constant.Adscountlisting = 1;
                    interstitial_Ad.showAd();
                }
            }, 1500);

        }else{
            if (progress.isShowing()){
                progress.dismiss();
            }
            onAdsresponce();
        }

    }

    public void onAdsresponce() {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        getActivity().startActivity(intent);
    }




    public void LoadImagedata(final int pagenumber, String srchsting) {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            items_recycler.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            getImagelistData(pagenumber, srchsting);
        } else {
            progressBar.setVisibility(View.GONE);
            if (currentpage == 1) {
                mIsLoadingMore = false;
                items_recycler.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);
            } else {
                snackbarcommonrelativeLong(getActivity(), content_home, getActivity().getResources().getString(R.string.no_internet));
            }
        }
    }


    public void getImagelistData(int pageget, String search) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        client.addHeader("Content-Type", "application/json; charset=utf-8");
        params.put("page", pageget);
        params.put("cat_id", Constant.APP_ID);
        params.put("search", search);
        client.get(Constant.GET_COMMON_LISTING, params, new AsynchronouseData(pageget));
    }


    class AsynchronouseData extends JsonHttpResponseHandler {

        int getpagenumber;

        AsynchronouseData(int pagenumber) {
            this.getpagenumber = pagenumber;
        }

        public void onStart() {
            super.onStart();
            if (getpagenumber == 1) {
                progressBar.setVisibility(View.VISIBLE);
            }

        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {

            try {
                JSONArray getdata = bytes.getJSONArray("data");
                ItemImageData imageData = new ItemImageData();
                imageData.setStatus(bytes.getBoolean("status"));
                imageData.setMessage(bytes.getString("message"));
                imageData.setLimit(bytes.getString("limit"));
                imageData.setData(constantfile.ConvertJSONImage_Model(getdata));

                if (imageData.isStatus()) {
                    mIsLoadingMore = true;
                    if (imageData.getData().size() < Integer.parseInt(imageData.getLimit())) {
                        mIsLoadingMore = false;
                    }
                    mainAdapter.adddata(imageData.getData(), getpagenumber);

                    items_recycler.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    mIsLoadingMore = false;
                    if (getpagenumber == 1) {
                        items_recycler.setVisibility(View.GONE);
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                Log.e("gettocatch", "  :::  " + e.getMessage());
                progressBar.setVisibility(View.GONE);
                if (getpagenumber == 1) {
                    items_recycler.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);
                }
                mIsLoadingMore = false;
            }

            if (!mIsLoadingMore) {
                mainAdapter.setnomoredata();
            }


        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void snackbarcommonrelativeLong(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadImagedata(currentpage, searchtext);
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorbutton));
        TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        TextView textaction = (TextView) snackbarView.findViewById(R.id.snackbar_action);
        textView.setTextSize(16);
        textaction.setTextSize(18);
        textView.setTextColor(ContextCompat.getColor(mcontext, R.color.colorAccentlight));
        textaction.setTextColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        snackbar.show();
    }


   /* public void callstartappads(){
        final ProgressDialog progress = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC,new AdEventListener() {
            @Override
            public void onReceiveAd(com.startapp.android.publish.adsCommon.Ad ad) {
                Constant.Adscountlisting = 1;
                if (progress.isShowing()){
                    progress.dismiss();
                }
                startAppAd.showAd(new AdDisplayListener() {
                    @Override
                    public void adHidden(com.startapp.android.publish.adsCommon.Ad ad) {
                        onAdsresponce();
                    }
                    @Override
                    public void adDisplayed(com.startapp.android.publish.adsCommon.Ad ad) { }
                    @Override
                    public void adClicked(com.startapp.android.publish.adsCommon.Ad ad) { }
                    @Override
                    public void adNotDisplayed(com.startapp.android.publish.adsCommon.Ad ad) {
                        onAdsresponce();
                    }
                });
            }
            @Override
            public void onFailedToReceiveAd(Ad ad) {
            }
        });
    }*/
}
