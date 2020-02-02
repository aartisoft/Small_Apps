package com.tinyapps.fullscreen.videostatus.Fragment;

import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tinyapps.fullscreen.videostatus.Activity.DetailActivity;
import com.tinyapps.fullscreen.videostatus.Activity.MainActivity;
import com.tinyapps.fullscreen.videostatus.Adapter.CommonListingAdapter;
import com.tinyapps.fullscreen.videostatus.ConnectionDetector;
import com.tinyapps.fullscreen.videostatus.Constant;
import com.tinyapps.fullscreen.videostatus.EndlessRecyclerOnScrollListener;
import com.tinyapps.fullscreen.videostatus.R;
import com.tinyapps.fullscreen.videostatus.gettersetter.ItemData;
import com.tinyapps.fullscreen.videostatus.gettersetter.Item_collections;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class CategoryWiseListingFragment extends Fragment implements CommonListingAdapter.MyClickListener,Constant.Callingafterads  {
    private ConnectionDetector detectorconn;

    private Gson gson;
    Boolean conn;
    Constant constantfile;
    RelativeLayout content_latest;
    RecyclerView latest_recycler;
    TextView no_data_text;
    ProgressBar progressbar;
    View rootView;
    CommonListingAdapter mAdapter;
    private int currentpage = 1;
    private boolean mIsLoadingMore;

    public CategoryWiseListingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.latest_fragment, container, false);

        gson = new Gson();
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


        GridLayoutManager mLayoutManager;
        if (Constant.Passing_type.toLowerCase().equals("portrait")){
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }else{
            mLayoutManager = new GridLayoutManager(getActivity(), 1);
        }

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
        mAdapter = new CommonListingAdapter(getActivity(),Constant.Passing_type.toLowerCase());
        mAdapter.setClickListener(this);
        latest_recycler.setAdapter(mAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case CommonListingAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case CommonListingAdapter.VIEW_TYPE_ITEM_portrait:
                        return 1;
                    case CommonListingAdapter.VIEW_TYPE_LOADING:
                        if (Constant.Passing_type.toLowerCase().equals("portrait")){
                            return 1;
                        }else{
                            return 1;
                        }

                    default:
                        return -1;
                }
            }
        });

        currentpage = 1;
        LoadImagedata(currentpage);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                    ((MainActivity) getActivity()).changetitle(getActivity().getResources().getString(R.string.app_name));
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack(Constant.Passing_From_Previous, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    ((MainActivity) getActivity()).changetitle(getActivity().getResources().getString(R.string.app_name));

                }
                return true;
            }
        });

    }


    public void LoadImagedata(final int pagenumber) {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            latest_recycler.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            getLatestImagesData(pagenumber);
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


    public void getLatestImagesData(int pageget) {
        String upperString = Constant.Passing_type.substring(0,1).toUpperCase() + Constant.Passing_type.substring(1);
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("type", "category");
        params.put("page", pageget);
        params.put("lang_id", Constant.LANGUAGE_ID);
        params.put("cat_id", Constant.Passing_cat_id);
        params.put("layout", upperString);
        Log.e("passingparam","  ::  "+params.toString());
        client.get(Constant.GET_COMMON_LISTING, params, new AsynchronouseData(pageget));
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
                ItemData imageData = new ItemData();
                imageData.setStatus(bytes.getBoolean("status"));
                imageData.setMessage(bytes.getString("message"));
                imageData.setLimit(bytes.getString("limit"));

                if (imageData.isStatus()) {
                    imageData.setData(constantfile.ConvertJSONtoModel(getdata));
                    mIsLoadingMore = true;
                    if (imageData.getData().size() < Integer.parseInt(imageData.getLimit())) {
                        mIsLoadingMore = false;
                    }
                    mAdapter.adddata(imageData.getData(), getpagenumber);
                    latest_recycler.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                } else {
                    progressbar.setVisibility(View.GONE);
                    if (getpagenumber == 1) {
                        latest_recycler.setVisibility(View.GONE);
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                    mIsLoadingMore = false;
                }
            } catch (Exception e) {
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

    @Override
    public void onLatestItemClick(int position, Item_collections passdata,ArrayList<Item_collections> passarray, View v) {
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
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        getActivity().startActivity(intent);
    }



}