package com.happymothersdayquotes.status.wishesand.Images.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.happymothersdayquotes.status.wishesand.Images.Activity.VideoplayerActivity;
import com.happymothersdayquotes.status.wishesand.Images.ConnectionDetector;
import com.happymothersdayquotes.status.wishesand.Images.Constant;
import com.happymothersdayquotes.status.wishesand.Images.EndlessRecyclerOnScrollListener;
import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.Adapter.VideoListingAdapter;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.ItemVideoData;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_videos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class VideoFragment extends Fragment implements VideoListingAdapter.MyClickListener, Constant.Callingafterads{
    private ConnectionDetector detectorconn;

    Boolean conn;
    Constant constantfile;
    RelativeLayout content_latest;
    RecyclerView latest_recycler;
    TextView no_data_text;
    ProgressBar progressbar;

    VideoListingAdapter mAdapter;
    private int currentpage = 1;
    private boolean mIsLoadingMore;
    View rootView;

    public VideoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_videos, container, false);

        this.conn = null;
        constantfile = new Constant();
        this.currentpage = 1;
        this.mIsLoadingMore = false;
        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        content_latest = (RelativeLayout) rootView.findViewById(R.id.content_latest);
        latest_recycler = (RecyclerView) rootView.findViewById(R.id.latest_recycler);
        progressbar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        no_data_text = (TextView) rootView.findViewById(R.id.no_data_text);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        latest_recycler.setLayoutManager(mLayoutManager);
        latest_recycler.setItemAnimator(new DefaultItemAnimator());
        latest_recycler.setHasFixedSize(true);
        latest_recycler.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (mIsLoadingMore) {
                    currentpage = current_page;
                    LoadImagedata(current_page);
                }
            }
        });
        mAdapter = new VideoListingAdapter(getActivity());
        mAdapter.setClickListener(this);
        latest_recycler.setAdapter(mAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case VideoListingAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case VideoListingAdapter.VIEW_TYPE_LOADING:
                        return 1;
                    default:
                        return -1;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentpage = 1;
        this.mIsLoadingMore = false;
        LoadImagedata(currentpage);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    ((MainActivity) getActivity()).SelectItem(getActivity().getResources().getString(R.string.app_name), 0);
//                }
//                return true;
//            }
//        });
//
//    }


    public void LoadImagedata(final int pagenumber) {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            latest_recycler.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            getVideolistData(pagenumber);
        } else {
            progressbar.setVisibility(View.GONE);
            if (currentpage == 1) {
                mIsLoadingMore = false;
                latest_recycler.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);
            } else {
                snackbarcommonrelativeLong(getActivity(), rootView, getActivity().getResources().getString(R.string.no_internet));
            }
        }
    }


    public void getVideolistData(int pageget) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        client.addHeader("Content-Type","application/json; charset=utf-8");
        params.put("page", pageget);
        params.put("cat_id", Constant.APP_ID);
        client.get(Constant.GET_VIDEO_LISTING, params, new AsynchronouseData(pageget));
    }

    @Override
    public void onVideoItemClick(int position, Item_videos passdata, ArrayList<Item_videos> passarray, View v) {

        Constant.Passing_item_obj_video = new Item_videos();
        Constant.Passing_item_obj_video = passdata;
        if (Constant.Adscountlisting == 2){
            constantfile.loadInterstitialAd(getActivity(),this);
        }else{
            Constant.Adscountlisting++;
            Intent intent = new Intent(getActivity(), VideoplayerActivity.class);
            getActivity().startActivity(intent);
        }
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

//            String s = new String(bytes);
//            Log.e("gettodata","    try  :::  "+  s );
            try {
                JSONArray getdata = bytes.getJSONArray("data");
                ItemVideoData imageData = new ItemVideoData();
                imageData.setStatus(bytes.getBoolean("status"));
                imageData.setMessage(bytes.getString("message"));
                imageData.setLimit(bytes.getString("limit"));
                imageData.setData(constantfile.ConvertJSONtoModel(getdata));

                if (imageData.isStatus()) {
                    mIsLoadingMore = true;
                    if (imageData.getData().size() < Integer.parseInt(imageData.getLimit())){
                        mIsLoadingMore = false;
                    }

                    mAdapter.adddata(imageData.getData(), getpagenumber);

                    latest_recycler.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                } else {
                    progressbar.setVisibility(View.GONE);
                    mIsLoadingMore = false;
                    if (getpagenumber == 1) {
                        latest_recycler.setVisibility(View.GONE);
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                Log.e("gettocatch","  :::  "+e.getMessage());
                progressbar.setVisibility(View.GONE);
                if (getpagenumber == 1) {
                    latest_recycler.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);
                }
                mIsLoadingMore = false;
            }

            if (!mIsLoadingMore) {
                mAdapter.setnomoredata();
            }


        }

    }



    public void snackbarcommonrelativeLong(Context mcontext, View coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadImagedata(currentpage);
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
        Intent intent = new Intent(getActivity(), VideoplayerActivity.class);
        getActivity().startActivity(intent);
    }
}
