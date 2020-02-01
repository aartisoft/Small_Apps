package com.teluguvideostatusfor.forwhatsapp.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.teluguvideostatusfor.forwhatsapp.Activity.MainActivity;
import com.teluguvideostatusfor.forwhatsapp.Adapter.CategoriesListingAdapter;
import com.teluguvideostatusfor.forwhatsapp.ConnectionDetector;
import com.teluguvideostatusfor.forwhatsapp.Constant;
import com.teluguvideostatusfor.forwhatsapp.R;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.CategoryData;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.Item_category;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class CategoriesFragment extends Fragment implements CategoriesListingAdapter.MyClickListener, Constant.Callingafterads {
    private ConnectionDetector detectorconn;

    private Gson gson;
    Boolean conn;
    Constant constantfile;
    RelativeLayout content_category;
    RecyclerView category_recycler;
    TextView no_data_text;
    ProgressBar progressbar;
    CategoriesListingAdapter mAdapter;
    View rootView;

    public CategoriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.categories_fragment, container, false);
        gson = new Gson();
        this.conn = null;
        constantfile = new Constant();
        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        content_category = (RelativeLayout) rootView.findViewById(R.id.content_category);
        category_recycler = (RecyclerView) rootView.findViewById(R.id.category_recycler);
        progressbar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        no_data_text = (TextView) rootView.findViewById(R.id.no_data_text);


        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        category_recycler.setLayoutManager(mLayoutManager);
        category_recycler.setItemAnimator(new DefaultItemAnimator());
        category_recycler.setHasFixedSize(true);

        mAdapter = new CategoriesListingAdapter(getActivity());
        mAdapter.setClickListener(this);
        category_recycler.setAdapter(mAdapter);


//        ArrayList<Item_collections> image_array = new ArrayList<>();
//        for (int i = 0; i < 39; i++) {
//            image_array.add(new Item_collections("" + i, "abc" + i, imageArray[i % imageArray.length],imageArray[i % imageArray.length], "100", "0", "50", "14-08-2019", "share detail","1","nature"));
//        }
//        mAdapter.adddata(image_array);
//        mAdapter.setnomoredata();
//        progressbar.setVisibility(View.GONE);



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoadImagedata();
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
                    ((MainActivity) getActivity()).SelectItem(getActivity().getResources().getString(R.string.app_name), 0);
                }
                return true;
            }
        });

    }


    public void LoadImagedata() {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            category_recycler.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            getCategoriesListData();
        } else {
            progressbar.setVisibility(View.GONE);
            category_recycler.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);
            snackbarcommonrelativeLong(getActivity(), rootView, getActivity().getResources().getString(R.string.no_internet));

        }

    }


    public void getCategoriesListData() {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        params.put("cat_list", "get");
        params.put("lang_id", Constant.LANGUAGE_ID);
        client.get(Constant.GET_CATEGORY_LISTING, params, new AsynchronouseData());
    }

    @Override
    public void onCategoriesItemClick(int position, Item_category passdata, View v) {
        Constant.Passing_cat_id = passdata.getCid();
        Constant.Passing_cat_name = passdata.getCategory_name();
        Constant.Passing_type = passdata.getType_layout();
        Constant.Passing_From_Previous = "CategoriesFragment";
        constantfile.loadInterstitialAd(getActivity(),this);
    }

    @Override
    public void onAdsresponce(Boolean showing) {
        ((MainActivity) getActivity()).changetitle(Constant.Passing_cat_name+"");
        Fragment fragment = new CategoryWiseListingFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fragment_container, fragment);
        ft.addToBackStack(Constant.Passing_From_Previous);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    class AsynchronouseData extends JsonHttpResponseHandler {

        AsynchronouseData() { }

        public void onStart() {
            super.onStart();
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject bytes) {
            try {
                JSONArray getdata = bytes.getJSONArray("data");
                CategoryData catData = new CategoryData();
                catData.setStatus(bytes.getBoolean("status"));
                catData.setMessage(bytes.getString("message"));
                catData.setData(constantfile.ConvertJSONCategory(getdata));

                if (catData.isStatus()) {
                    if (catData.getData().size() == 0) {
                        category_recycler.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);
                        no_data_text.setVisibility(View.VISIBLE);
                        return;
                    }
                    mAdapter.adddata(catData.getData());
                    category_recycler.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                } else {
                    progressbar.setVisibility(View.GONE);
                    category_recycler.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {
                progressbar.setVisibility(View.GONE);
                category_recycler.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);
            }
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

}