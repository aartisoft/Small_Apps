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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.happymothersdayquotes.status.wishesand.Images.Activity.StatusSingleActivity;
import com.happymothersdayquotes.status.wishesand.Images.Adapter.StatusAdapter;
import com.happymothersdayquotes.status.wishesand.Images.ConnectionDetector;
import com.happymothersdayquotes.status.wishesand.Images.Constant;
import com.happymothersdayquotes.status.wishesand.Images.EndlessRecyclerOnScrollListener;
import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.ItemStatusData;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_Status;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.happymothersdayquotes.status.wishesand.Images.Constant.Pass_status_pos;
import static com.happymothersdayquotes.status.wishesand.Images.Constant.Passing_item_status;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment implements StatusAdapter.MyClickListener {
    RecyclerView status_list;
    StatusAdapter mAdapter;
    private int currentpage = 0;
    RelativeLayout content_fragment;
    private boolean mIsLoadingMore;
    Constant constantfile;
    private ConnectionDetector detectorconn;
    Boolean conn;
    TextView no_data_text;
    ProgressBar progressbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        this.conn = null;
        constantfile = new Constant();
        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        status_list = (RecyclerView) view.findViewById(R.id.status_list);
        content_fragment = view.findViewById(R.id.content_fragment);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        no_data_text = (TextView) view.findViewById(R.id.no_data_text);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        status_list.setLayoutManager(mLayoutManager);
        status_list.setItemAnimator(new DefaultItemAnimator());
        status_list.setHasFixedSize(true);
        status_list.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (mIsLoadingMore) {
                    currentpage = current_page;
                    LoadImagedata(current_page);
                }
            }
        });
        mAdapter = new StatusAdapter(getActivity());
        mAdapter.setClickListener(this);
        status_list.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentpage = 1;
        this.mIsLoadingMore = false;
        LoadImagedata(currentpage);
    }

    public void LoadImagedata(final int pagenumber) {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            status_list.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            getImagelistData(pagenumber);
        } else {
            progressbar.setVisibility(View.GONE);
            if (currentpage == 1) {
                mIsLoadingMore = false;
                status_list.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);
            } else {
                snackbarcommonrelativeLong(getActivity(), content_fragment, getActivity().getResources().getString(R.string.no_internet));
            }
        }
    }


    public void getImagelistData(int pageget) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        client.addHeader("Content-Type", "application/json; charset=utf-8");
        params.put("page", pageget);
        params.put("cat_id", Constant.APP_ID);
        client.get(Constant.GET_STATUS_LISTING, params, new AsynchronouseData(pageget));
    }

    class AsynchronouseData extends JsonHttpResponseHandler {

        int getpagenumber;

        AsynchronouseData(int pagenumber) {
            this.getpagenumber = pagenumber;
        }

        public void onStart() {
            super.onStart();
            if (getpagenumber == 1) {
                progressbar.setVisibility(View.VISIBLE);
            }

        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {

            try {
                JSONArray getdata = bytes.getJSONArray("data");
                ItemStatusData imageData = new ItemStatusData();
                imageData.setStatus(bytes.getBoolean("status"));
                imageData.setMessage(bytes.getString("message"));
                imageData.setLimit(bytes.getString("limit"));
                imageData.setData(constantfile.ConvertJSONStatus_Model(getdata));

                if (imageData.isStatus()) {
                    mIsLoadingMore = true;
                    if (imageData.getData().size() < Integer.parseInt(imageData.getLimit())) {
                        mIsLoadingMore = false;
                    }
                    mAdapter.adddata(imageData.getData(), getpagenumber);

                    status_list.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                } else {
                    progressbar.setVisibility(View.GONE);
                    mIsLoadingMore = false;
                    if (getpagenumber == 1) {
                        status_list.setVisibility(View.GONE);
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                Log.e("gettocatch", "  :::  " + e.getMessage());
                progressbar.setVisibility(View.GONE);
                if (getpagenumber == 1) {
                    status_list.setVisibility(View.GONE);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStatusItemClick(int position, Item_Status passdata, ArrayList<Item_Status> passarray, View v) {
        Passing_item_status = new ArrayList<>();
        Passing_item_status.addAll(passarray);
        Pass_status_pos = position;
        Intent intent = new Intent(getActivity(), StatusSingleActivity.class);
        getActivity().startActivity(intent);
    }
}