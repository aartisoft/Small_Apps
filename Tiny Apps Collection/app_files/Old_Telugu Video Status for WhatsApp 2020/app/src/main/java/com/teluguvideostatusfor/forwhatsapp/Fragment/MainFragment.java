package com.teluguvideostatusfor.forwhatsapp.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.teluguvideostatusfor.forwhatsapp.Activity.DetailActivity;
import com.teluguvideostatusfor.forwhatsapp.Activity.MainActivity;
import com.teluguvideostatusfor.forwhatsapp.Activity.RequestVideoActivity;
import com.teluguvideostatusfor.forwhatsapp.Adapter.HomeCatWiseAdapter;
import com.teluguvideostatusfor.forwhatsapp.Adapter.HomeCatlistAdapter;
import com.teluguvideostatusfor.forwhatsapp.Adapter.HomeLatestAdapter;
import com.teluguvideostatusfor.forwhatsapp.Adapter.HomeTrendingAdapter;
import com.teluguvideostatusfor.forwhatsapp.Adapter.SliderAdapterExample;
import com.teluguvideostatusfor.forwhatsapp.ConnectionDetector;
import com.teluguvideostatusfor.forwhatsapp.Constant;
import com.teluguvideostatusfor.forwhatsapp.PullRefreshLayout;
import com.teluguvideostatusfor.forwhatsapp.R;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.HomeCatData;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.HomeData;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.Item_collections;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class MainFragment extends Fragment implements HomeCatlistAdapter.MyClickListener, HomeCatWiseAdapter.MyClickListener, SliderAdapterExample.MyClickListener, Constant.Callingafterads, HomeTrendingAdapter.MyClickListener, HomeLatestAdapter.MyClickListener {

    public static final String TAG = "Main_list";
    private ConnectionDetector detectorconn;
    //HorizontalInfiniteCycleViewPager mViewPager;

    Boolean conn;
    private Gson gson;
    Constant constantfile;
    private AlertDialog alert;
    private RelativeLayout content_home, top_dope_rl;
    TextView no_data_text;
    NestedScrollView home_nested;
    RecyclerView trending_recycler, categories_recycler, latest_recycler, item_cat_wise_recycler;
    TextView trending_viewall, latest_viewall;
    ProgressBar progressbar, progressbar_latest, progressbar_trending;
    HorizontalPagerAdapter sliderAdapter;
    ArrayList<Item_collections> slider_list = new ArrayList<>();

    CardView banner_view;
    ImageView banner_image;
    String banner_link = "";
    String opentype = "";
    HomeCatlistAdapter homecatadapter;
    HomeTrendingAdapter hometrendingadapter;
    HomeLatestAdapter homelatestadapter;
    HomeCatWiseAdapter homecatwisetadapter;
    RelativeLayout request_rl,rate_rl;
    int width;
    PullRefreshLayout swipeRefreshLayout;
    View rootView;
    SliderView mViewPagerX;
    SliderAdapterExample adapterExample;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Constant.Passing_from_notification){
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            getActivity().startActivity(intent);
            Constant.Passing_from_notification = false;
        }
        LoadImagedata();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mainhome_fragment, container, false);

        gson = new Gson();
        this.conn = null;
        constantfile = new Constant();
        slider_list = new ArrayList<>();
        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        home_nested = (NestedScrollView) rootView.findViewById(R.id.home_nested);
        content_home = (RelativeLayout) rootView.findViewById(R.id.content_mainfragment);
        banner_view = (CardView) rootView.findViewById(R.id.banner_view);
        banner_view.setVisibility(View.GONE);
        banner_image = (ImageView) rootView.findViewById(R.id.banner_image);
        top_dope_rl = (RelativeLayout) rootView.findViewById(R.id.top_dope_rl);
        trending_recycler = (RecyclerView) rootView.findViewById(R.id.trending_recycler);
        categories_recycler = (RecyclerView) rootView.findViewById(R.id.categories_recycler);
        latest_recycler = (RecyclerView) rootView.findViewById(R.id.latest_recycler);
        item_cat_wise_recycler = (RecyclerView) rootView.findViewById(R.id.item_cat_wise_recycler);
        trending_viewall = (TextView) rootView.findViewById(R.id.trending_viewall);
        latest_viewall = (TextView) rootView.findViewById(R.id.latest_viewall);
        request_rl = (RelativeLayout) rootView.findViewById(R.id.request_rl);
        rate_rl = (RelativeLayout) rootView.findViewById(R.id.rate_rl);
        //mViewPager = rootView.findViewById(R.id.viewPager);
        mViewPagerX=rootView.findViewById(R.id.imageSlider);

        progressbar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        progressbar_latest = (ProgressBar) rootView.findViewById(R.id.progressbar_latest);
        progressbar_trending = (ProgressBar) rootView.findViewById(R.id.progressbar_trending);
        progressbar_latest.setVisibility(View.GONE);
        progressbar_trending.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        ViewGroup.LayoutParams paramsslider = top_dope_rl.getLayoutParams();
        paramsslider.height = (int) (width / 1.9);
        top_dope_rl.setLayoutParams(paramsslider);

        ViewGroup.LayoutParams params = trending_recycler.getLayoutParams();
        params.height = (int) (width / 2.3);
        trending_recycler.setLayoutParams(params);

        ViewGroup.LayoutParams paramscat = categories_recycler.getLayoutParams();
        paramscat.height = (int) (width / 5);
        categories_recycler.setLayoutParams(paramscat);

        ViewGroup.LayoutParams paramstrend = latest_recycler.getLayoutParams();
        paramstrend.height = (int) (width / 2.5);
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
        homecatadapter = new HomeCatlistAdapter(getActivity());
        homecatadapter.setClickListener(this);
        categories_recycler.setAdapter(homecatadapter);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        latest_recycler.setLayoutManager(mLayoutManager);
        latest_recycler.setHasFixedSize(true);
        latest_recycler.setItemAnimator(new DefaultItemAnimator());
        homelatestadapter = new HomeLatestAdapter(getActivity());
        homelatestadapter.setClickListener(this);
        latest_recycler.setAdapter(homelatestadapter);


        LinearLayoutManager CATWiseManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        item_cat_wise_recycler.setLayoutManager(CATWiseManager);
        item_cat_wise_recycler.setHasFixedSize(true);
        item_cat_wise_recycler.setItemAnimator(new DefaultItemAnimator());
        homecatwisetadapter = new HomeCatWiseAdapter(getActivity(), width);
        homecatwisetadapter.setClickListener(this);
        item_cat_wise_recycler.setAdapter(homecatwisetadapter);

        sliderAdapter = new HorizontalPagerAdapter(getActivity());
        //mViewPager.setAdapter(sliderAdapter);

        adapterExample = new SliderAdapterExample(getActivity());

        mViewPagerX.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        mViewPagerX.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mViewPagerX.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        mViewPagerX.setIndicatorSelectedColor(Color.WHITE);
        mViewPagerX.setIndicatorUnselectedColor(Color.GRAY);
        mViewPagerX.setScrollTimeInSec(4); //set scroll delay in seconds :
        mViewPagerX.startAutoCycle();

        mViewPagerX.setSliderAdapter(adapterExample);


        no_data_text = (TextView) rootView.findViewById(R.id.no_data_text);


        trending_viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.Passing_From_title = "Trending";
                Constant.Passing_From_text = "trending";
                Constant.Passing_type = "landscape";
                opennextviewonclick("listing");
            }
        });


        latest_viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.Passing_From_title = "Latest";
                Constant.Passing_From_text = "latest";
                Constant.Passing_type = "landscape";
                opennextviewonclick("listing");
            }
        });

        banner_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banner_view.getVisibility() == View.VISIBLE){
                   getopenBannerUrl(banner_link);
                }
            }
        });

        request_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reqeust = new Intent(getActivity(), RequestVideoActivity.class);
                startActivity(reqeust);
            }
        });

        rate_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRateAppCounter();
            }
        });



        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public void getRateAppCounter() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }


    @Override
    public void oncatwiseviewallClick(int position, Item_collections passdata, View v) {
//        Constant.Passing_cat_id = passdata.getCid();
//        Constant.Passing_cat_name = passdata.getCategory_name();
//        Constant.Passing_type = passdata.getType_layout();
//        constantfile.loadInterstitialAd(getActivity(), this);
//        opennextviewonclick("category");
    }

    @Override
    public void oncatwiseviewallClick(int position, HomeCatData passdata, View v) {
        Constant.Passing_cat_id = passdata.getCid();
        Constant.Passing_cat_name = passdata.getCategory_name();
        Constant.Passing_type = passdata.getType_layout();
        constantfile.loadInterstitialAd(getActivity(), this);
        opennextviewonclick("category");
    }

    @Override
    public void oncatwiseitemClickPass(int position, Item_collections passdata, View v) {
        Constant.Passing_item_object = new Item_collections();
        Constant.Passing_item_object = passdata;
        opennextviewonclick("detail");
    }

    @Override
    public void onCatItemClick(int position, HomeCatData passdata, View v) {
        Constant.Passing_cat_id = passdata.getCid();
        Constant.Passing_cat_name = passdata.getCategory_name();
        Constant.Passing_type = passdata.getType_layout();
        constantfile.loadInterstitialAd(getActivity(), this);
        opennextviewonclick("category");
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
        if (opentype.equals("listing") || opentype.equals("category")) {
            constantfile.loadInterstitialAd(getActivity(), this);
        } else if (opentype.equals("detail")) {
            if (Constant.Adscount == 2) {
                constantfile.loadInterstitialAd(getActivity(), this);
            } else {
                Constant.Adscount++;
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                getActivity().startActivity(intent);

            }
        }

    }

    public void LoadImagedata() {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            home_nested.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            getHomeListData();
        } else {
            progressbar_latest.setVisibility(View.GONE);
            progressbar_trending.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);
            home_nested.setVisibility(View.GONE);
            snackbarcommonrelativeLong(getActivity(), rootView, getActivity().getResources().getString(R.string.no_internet));

        }

    }


    public void getHomeListData() {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("Content-Type", "application/json; charset=utf-8");
        client.setTimeout(60000);
        params.put("home", "home");
        params.put("lang_id", Constant.LANGUAGE_ID);
        client.get(Constant.GET_HOME_LISTING, params, new AsynchronouseData());
    }


    class AsynchronouseData extends JsonHttpResponseHandler {

        AsynchronouseData() {
        }

        public void onStart() {
            super.onStart();
            progressbar_latest.setVisibility(View.VISIBLE);
            progressbar_trending.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject bytes) {
            try {


                JSONArray getcategory = bytes.getJSONArray("category_list");
                JSONArray gettrending = bytes.getJSONArray("trending_list");
                JSONArray getlatest = bytes.getJSONArray("latest_list");
                JSONArray getslider = bytes.getJSONArray("slider_list");

                HomeData statusData = new HomeData();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                statusData.setIs_banner_shown(bytes.getString("is_banner_shown"));
                statusData.setBanner_link(bytes.getString("banner_link"));
                statusData.setBanner_background(bytes.getString("banner_background"));


                slider_list = new ArrayList<>();
                if (statusData.isStatus()) {
                    if (statusData.getIs_banner_shown().equals("true")) {
                        banner_link = statusData.getBanner_link()+"";
                        ViewGroup.LayoutParams paramsslider = banner_view.getLayoutParams();
                        paramsslider.height = (int) (width / 3);
                        banner_view.setLayoutParams(paramsslider);
                        banner_view.setVisibility(View.VISIBLE);
                        Glide.with(getActivity()).load(statusData.getBanner_background()).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        banner_view.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(banner_image);
                    }


                    if (getcategory.length() > 0) {
                        statusData.setCategory_list(constantfile.ConvertCategoryWithData(getcategory));
                        homecatadapter.adddata(statusData.getCategory_list(), 1);
                        ArrayList<HomeCatData> catdatalisting = new ArrayList<>();
                        for (int i = 0; i < statusData.getCategory_list().size() ;  i++){
                            if (statusData.getCategory_list().get(i).getData().size() > 0){
                                catdatalisting.add(statusData.getCategory_list().get(i));
                            }
                        }
                        homecatwisetadapter.adddata(catdatalisting, 1); // data Filled List
                    }
                    if (gettrending.length() > 0) {
                        statusData.setTrending_list(constantfile.ConvertJSONtoModel(gettrending));
                        hometrendingadapter.adddata(statusData.getTrending_list(), 1);
                    }
                    if (getlatest.length() > 0) {
                        statusData.setLatest_list(constantfile.ConvertJSONtoModel(getlatest));
                        homelatestadapter.adddata(statusData.getLatest_list(), 1);
                    }
                    if (getslider.length() > 0) {
                        statusData.setSlider_list(constantfile.ConvertJSONtoModel(getslider));
                        slider_list.addAll(statusData.getSlider_list());
                        adapterExample.addItem(statusData.getSlider_list(), 1);
                       // adapterExample.notifyDataSetChanged();
                        top_dope_rl.setVisibility(View.VISIBLE);
                        //mViewPager.notifyDataSetChanged();
                        //mViewPagerX.notifyAll();
                    }else{
                        top_dope_rl.setVisibility(View.GONE);
                    }

                    home_nested.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    progressbar_latest.setVisibility(View.GONE);
                    progressbar_trending.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getActivity(), "Data Is", Toast.LENGTH_SHORT).show();
                    progressbar_latest.setVisibility(View.GONE);
                    progressbar_trending.setVisibility(View.GONE);
                    home_nested.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Data Not "+e, Toast.LENGTH_LONG).show();
                Log.e(TAG, "onSuccess:XXX "+e);
                Log.e(TAG, "onSuccess:XXX "+e);
                Log.e(TAG, "onSuccess:XXX "+e);
                Log.e(TAG, "onSuccess:XXX "+e);
                progressbar_latest.setVisibility(View.GONE);
                progressbar_trending.setVisibility(View.GONE);
                home_nested.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);

            }
        }

        public void onFailure(int statusCode, Header[] headers, JSONObject responseBody, Throwable error) {
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


    @Override
    public void onAdsresponce(Boolean showing) {
        if (opentype.equals("detail")) {
            Constant.Adscount = 1;
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            getActivity().startActivity(intent);
        } else if (opentype.equals("listing")) {
            Constant.Passing_From_Previous = "MainFragment";
            ((MainActivity) getActivity()).changetitle(Constant.Passing_From_title + "");
            Fragment fragment = new CommonListingFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fragment_container, fragment);
            ft.addToBackStack(Constant.Passing_From_Previous);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        } else if (opentype.equals("category")) {
            Constant.Passing_From_Previous = "MainFragment";
            ((MainActivity) getActivity()).changetitle(Constant.Passing_cat_name + "");
            Fragment fragment = new CategoryWiseListingFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fragment_container, fragment);
            ft.addToBackStack(Constant.Passing_From_Previous);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }


    public class HorizontalPagerAdapter extends PagerAdapter {

        private Context mContext;
        private LayoutInflater mLayoutInflater;


        public HorizontalPagerAdapter(final Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return slider_list.size();
        }

        @Override
        public int getItemPosition(final Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final View view;

            view = mLayoutInflater.inflate(R.layout.fullscreenitem_layout, container, false);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
            progressBar.setVisibility(View.VISIBLE);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_full);
            TextView slider_video_name = (TextView) view.findViewById(R.id.slider_video_name);
            slider_video_name.setText(slider_list.get(position).getVideo_name()+"");
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


            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }
    }


    public void getopenBannerUrl(String Url) {
        if (Url != null && !Url.equals("") && !Url.equals("http://simpleappscreator.com")){
            String passurl = Url;
            if (!Url.startsWith("http://") && !Url.startsWith("https://")) {
                passurl = "http://" + Url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(passurl));
            try {
                startActivity(browserIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                constantfile.snackbarcommonview(getActivity(), rootView, "There are no any link Found");
            }

        }else{
            constantfile.snackbarcommonview(getActivity(),rootView,getActivity().getResources().getString(R.string.no_link_found));
        }
    }


}
