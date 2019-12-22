package com.happyrepublicday.videosforstorystatus.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.happyrepublicday.videosforstorystatus.Activity.CommonListingActivity;
import com.happyrepublicday.videosforstorystatus.Activity.DetailActivity;
import com.happyrepublicday.videosforstorystatus.Adapter.HomeFullItemAdapter;
import com.happyrepublicday.videosforstorystatus.Adapter.HomeLatestAdapter;
import com.happyrepublicday.videosforstorystatus.Adapter.HomeTrendingAdapter;
import com.happyrepublicday.videosforstorystatus.ConnectionDetector;
import com.happyrepublicday.videosforstorystatus.Constant;
import com.happyrepublicday.videosforstorystatus.PullRefreshLayout;
import com.happyrepublicday.videosforstorystatus.R;
import com.happyrepublicday.videosforstorystatus.gettersetter.HomeData;
import com.happyrepublicday.videosforstorystatus.gettersetter.Item_collections;
import com.happyrepublicday.videosforstorystatus.widgets.EnchantedViewPager;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class MainFragment extends Fragment implements HomeFullItemAdapter.MyClickListener, HomeTrendingAdapter.MyClickListener, HomeLatestAdapter.MyClickListener {

    public static final String TAG = "Main_list";
    private ConnectionDetector detectorconn;
    EnchantedViewPager mViewPager;
    CircleIndicator circleIndicator;
    Boolean conn;
    private Gson gson;
    Constant constantfile;
    private AlertDialog alert;
    private RelativeLayout content_home, top_dope_rl;
    TextView no_data_text;
    NestedScrollView home_nested;
    RecyclerView trending_recycler, categories_recycler, latest_recycler;
    TextView trending_viewall, categories_viewall, latest_viewall;
    ProgressBar progressbar, progressbar_latest, progressbar_trending;
    CustomViewPagerAdapter sliderAdapter;
    ArrayList<Item_collections> slider_list = new ArrayList<>();

    String opentype = "";
    HomeFullItemAdapter homecatadapter;
    HomeTrendingAdapter hometrendingadapter;
    HomeLatestAdapter homelatestadapter;
    PullRefreshLayout swipeRefreshLayout;
    int currentCount = 0;
    View rootView;

    StartAppAd startAppAd;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        startAppAd.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState (Bundle savedInstanceState){
        startAppAd.onRestoreInstanceState(savedInstanceState);
        // super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Constant.Passing_from_notification){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    getActivity().startActivity(intent);
                    Constant.Passing_from_notification = false;
                }
            }, 200);

        }
        LoadImagedata();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mainhome_fragment, container, false);

        startAppAd = new StartAppAd(getActivity());
        gson = new Gson();
        this.conn = null;
        constantfile = new Constant();
        slider_list = new ArrayList<>();
        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        home_nested = (NestedScrollView) rootView.findViewById(R.id.home_nested);
        content_home = (RelativeLayout) rootView.findViewById(R.id.content_mainfragment);
        top_dope_rl = (RelativeLayout) rootView.findViewById(R.id.top_dope_rl);
        trending_recycler = (RecyclerView) rootView.findViewById(R.id.trending_recycler);
        categories_recycler = (RecyclerView) rootView.findViewById(R.id.categories_recycler);
        latest_recycler = (RecyclerView) rootView.findViewById(R.id.latest_recycler);
        trending_viewall = (TextView) rootView.findViewById(R.id.trending_viewall);
        categories_viewall = (TextView) rootView.findViewById(R.id.categories_viewall);
        latest_viewall = (TextView) rootView.findViewById(R.id.latest_viewall);
        mViewPager = rootView.findViewById(R.id.viewPager);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);

        progressbar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        progressbar_latest = (ProgressBar) rootView.findViewById(R.id.progressbar_latest);
        progressbar_trending = (ProgressBar) rootView.findViewById(R.id.progressbar_trending);
        progressbar_latest.setVisibility(View.GONE);
        progressbar_trending.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        ViewGroup.LayoutParams paramsslider = top_dope_rl.getLayoutParams();
        paramsslider.height = (int) (width / 1.9);
        top_dope_rl.setLayoutParams(paramsslider);


        ViewGroup.LayoutParams params = trending_recycler.getLayoutParams();
        params.height = (int) (width / 3.5);
        trending_recycler.setLayoutParams(params);

        ViewGroup.LayoutParams paramscat = categories_recycler.getLayoutParams();
        paramscat.height = (int) (width / 2.3);
        categories_recycler.setLayoutParams(paramscat);

        ViewGroup.LayoutParams paramstrend = latest_recycler.getLayoutParams();
        paramstrend.height = (int) (width / 3.5);
        latest_recycler.setLayoutParams(paramstrend);

        LinearLayoutManager horizontaltrendingManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trending_recycler.setLayoutManager(horizontaltrendingManager);
        trending_recycler.setHasFixedSize(true);
        trending_recycler.setItemAnimator(new DefaultItemAnimator());
        hometrendingadapter = new HomeTrendingAdapter(getActivity());
        hometrendingadapter.setClickListener(this);
        trending_recycler.setAdapter(hometrendingadapter);


        LinearLayoutManager horizontalCatManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categories_recycler.setLayoutManager(horizontalCatManager);
        categories_recycler.setHasFixedSize(true);
        categories_recycler.setItemAnimator(new DefaultItemAnimator());
        homecatadapter = new HomeFullItemAdapter(getActivity());
        homecatadapter.setClickListener(this);
        categories_recycler.setAdapter(homecatadapter);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        latest_recycler.setLayoutManager(mLayoutManager);
        latest_recycler.setHasFixedSize(true);
        latest_recycler.setItemAnimator(new DefaultItemAnimator());
        homelatestadapter = new HomeLatestAdapter(getActivity());
        homelatestadapter.setClickListener(this);
        latest_recycler.setAdapter(homelatestadapter);

        sliderAdapter = new CustomViewPagerAdapter();
        mViewPager.setAdapter(sliderAdapter);
        autoPlay(mViewPager);

        no_data_text = (TextView) rootView.findViewById(R.id.no_data_text);


        trending_viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.Passing_From = "trending";
                Constant.Passing_From_title = "Trending";
                opennextviewonclick("listing");
            }
        });
        categories_viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.Passing_From = "portrait";
                Constant.Passing_From_title = "Full Screen";
                opennextviewonclick("listing");
            }
        });
        latest_viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.Passing_From = "latest";
                Constant.Passing_From_title = "Latest";
                opennextviewonclick("listing");
            }
        });

        mViewPager.useScale();
        mViewPager.removeAlpha();

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCatItemClick(int position, Item_collections passdata, View v) {
        Constant.Passing_item_object = new Item_collections();
        Constant.Passing_item_object = passdata;
        opennextviewonclick("detail");

    }

    @Override
    public void onLatestItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v) {
        Constant.Passing_item_object = new Item_collections();
        Constant.Passing_item_object = passdata;
        opennextviewonclick("detail");
    }

    @Override
    public void onTrendingItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v) {
        Constant.Passing_item_object = new Item_collections();
        Constant.Passing_item_object = passdata;
        opennextviewonclick("detail");
    }

    public void opennextviewonclick(String opennext) {
        opentype = opennext;
        if (opentype.equals("listing")) {
            callingadsonclick();
        } else if (opentype.equals("detail")) {
            if (Constant.Adscount == 2) {
                callingadsonclick();
            } else {
                Constant.Adscount++;
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                getActivity().startActivity(intent);

            }
        }
    }

    public void callingadsonclick(){
        final ProgressDialog progress = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
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
                        onAdsresponce();
                    }
                    @Override
                    public void adDisplayed(Ad ad) { }
                    @Override
                    public void adClicked(Ad ad) { }
                    @Override
                    public void adNotDisplayed(Ad ad) {
                        onAdsresponce();
                    }
                });
            }
            @Override
            public void onFailedToReceiveAd(Ad ad) {
                Log.e("gettingerrorad","::    "+ad.getErrorMessage());
                if (progress.isShowing()){
                    progress.dismiss();
                }
            }
        });
    }

    public void LoadImagedata() {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            home_nested.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            getCategoriesListData();
        } else {
            progressbar_latest.setVisibility(View.GONE);
            progressbar_trending.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);
            home_nested.setVisibility(View.GONE);
            snackbarcommonrelativeLong(getActivity(), rootView, getActivity().getResources().getString(R.string.no_internet));

        }

    }


    public void getCategoriesListData() {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("app_id", Constant.APP_ID);
        params.put("home", "home");
        client.get(Constant.GET_HOME_LISTING, params, new AsynchronouseData());
    }


    class AsynchronouseData extends JsonHttpResponseHandler {

        AsynchronouseData() {
        }

        public void onStart() {
            super.onStart();
            progressbar_latest.setVisibility(View.VISIBLE);
            progressbar_trending.setVisibility(View.VISIBLE);
            //progressbar.setVisibility(View.VISIBLE);
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {

            try {

                JSONArray getfullscreen = bytes.getJSONArray("fullscreen_list");
                JSONArray gettrending = bytes.getJSONArray("trending_list");
                JSONArray getlatest = bytes.getJSONArray("latest_list");
                JSONArray getslider = bytes.getJSONArray("slider_list");
                HomeData statusData = new HomeData();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                if (getfullscreen.length() > 0) {
                    statusData.setFullscreen_list(constantfile.ConvertJSONtoModel(getfullscreen));
                }
                if (gettrending.length() > 0) {
                    statusData.setTrending_list(constantfile.ConvertJSONtoModel(gettrending));
                }
                if (getlatest.length() > 0) {
                    statusData.setLatest_list(constantfile.ConvertJSONtoModel(getlatest));
                }

                if (getslider.length() > 0) {
                    statusData.setSlider_list(constantfile.ConvertJSONtoModel(getslider));
                }

                ArrayList<Item_collections> temp_trending = new ArrayList<>();
                ArrayList<Item_collections> temp_latest = new ArrayList<>();
                ArrayList<Item_collections> temp_cat = new ArrayList<>();

                slider_list = new ArrayList<>();
                if (statusData.isStatus()) {
                    if (getfullscreen.length() > 0) {
                        temp_cat.addAll(statusData.getFullscreen_list());
                    }
                    if (getlatest.length() > 0) {
                        temp_latest.addAll(statusData.getLatest_list());
                    }
                    if (gettrending.length() > 0) {
                        temp_trending.addAll(statusData.getTrending_list());
                    }

                    if (getslider.length() > 0) {
                        slider_list.addAll(statusData.getSlider_list());
                        sliderAdapter.notifyDataSetChanged();
                        circleIndicator.setViewPager(mViewPager);
                    }

                    if (temp_cat.size() == 0 && temp_latest.size() == 0 && temp_trending.size() == 0) {
                        home_nested.setVisibility(View.GONE);
                        no_data_text.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (getfullscreen.length() > 0) {
                        homecatadapter.adddata(temp_cat, 1);
                    }
                    if (getlatest.length() > 0) {
                        homelatestadapter.adddata(temp_latest, 1);
                    }
                    if (gettrending.length() > 0) {
                        hometrendingadapter.adddata(temp_trending, 1);
                    }
                    home_nested.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    progressbar_latest.setVisibility(View.GONE);
                    progressbar_trending.setVisibility(View.GONE);
                } else {

                    progressbar_latest.setVisibility(View.GONE);
                    progressbar_trending.setVisibility(View.GONE);
                    home_nested.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {
                progressbar_latest.setVisibility(View.GONE);
                progressbar_trending.setVisibility(View.GONE);
                home_nested.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);
            }


        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {
            progressbar_latest.setVisibility(View.GONE);
            progressbar_trending.setVisibility(View.GONE);
            home_nested.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);

        }
    }


    public void snackbarcommonrelativeLong(Context mcontext, View coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadImagedata();
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


    public void onAdsresponce() {
        if (opentype.equals("detail")) {
            Constant.Adscount = 1;
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            getActivity().startActivity(intent);
        } else if (opentype.equals("listing")) {
            Intent callcommon = new Intent(getActivity(), CommonListingActivity.class);
            startActivity(callcommon);
        }
    }


    private class CustomViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        private CustomViewPagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return slider_list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View detailLayout = inflater.inflate(R.layout.fullscreenitem_layout, container, false);
            assert detailLayout != null;
            final ProgressBar progressBar = (ProgressBar) detailLayout.findViewById(R.id.itemProgressbar);
            progressBar.setVisibility(View.VISIBLE);
            ImageView imageView = (ImageView) detailLayout.findViewById(R.id.image_full);
            Glide.with(getActivity())
                    .load(slider_list.get(position).getVideo_image_thumb())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.Passing_item_object = new Item_collections();
                    Constant.Passing_item_object = slider_list.get(position);
                    opennextviewonclick("detail");
                }
            });

            container.addView(detailLayout, 0);
            return detailLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }

    private void autoPlay(final ViewPager viewPager) {

        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sliderAdapter != null && viewPager.getAdapter().getCount() > 0) {
                        int position = currentCount % sliderAdapter.getCount();
                        currentCount++;
                        viewPager.setCurrentItem(position);
                        autoPlay(viewPager);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "auto scroll pager error.", e);
                }
            }
        }, 4000);
    }

}
