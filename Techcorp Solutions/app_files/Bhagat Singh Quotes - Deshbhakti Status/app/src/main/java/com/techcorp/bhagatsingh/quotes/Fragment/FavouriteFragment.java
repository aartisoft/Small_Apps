package com.techcorp.bhagatsingh.quotes.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appnext.ads.AdsError;
import com.appnext.ads.interstitial.Interstitial;
import com.appnext.core.AppnextAdCreativeType;
import com.appnext.core.callbacks.OnAdClicked;
import com.appnext.core.callbacks.OnAdClosed;
import com.appnext.core.callbacks.OnAdError;
import com.appnext.core.callbacks.OnAdLoaded;
import com.appnext.core.callbacks.OnAdOpened;
import com.techcorp.bhagatsingh.quotes.Activity.FavDetailActivity;
import com.techcorp.bhagatsingh.quotes.Activity.MainActivity;
import com.techcorp.bhagatsingh.quotes.Adapter.FavItemsAdapter;
import com.techcorp.bhagatsingh.quotes.ConnectionDetector;
import com.techcorp.bhagatsingh.quotes.Constant;
import com.techcorp.bhagatsingh.quotes.DbAdapter;
import com.techcorp.bhagatsingh.quotes.R;
import com.techcorp.bhagatsingh.quotes.gettersetter.Item_images;

import java.util.ArrayList;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class FavouriteFragment extends Fragment implements FavItemsAdapter.MyClickListener{

    public static final String TAG = "Main_list";
    private ConnectionDetector detectorconn;
    Boolean conn;
    Constant constantfile;
    private AlertDialog alert;
    RelativeLayout content_favorite;
    ArrayList<Item_images> fav_item_list;
    RecyclerView items_recycler;
    TextView no_data_text;
    FavItemsAdapter mainAdapter;
    private SearchView searchView;
    DbAdapter db;
    String PassingTitle = "";

    Interstitial interstitial_Ad;

  //  StartAppAd startAppAd;


    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
       // startAppAd.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState (Bundle savedInstanceState){
      //  startAppAd.onRestoreInstanceState(savedInstanceState);
        // super.onRestoreInstanceState(savedInstanceState);
    }



    @Override
    public void onPause() {
        super.onPause();
      //  startAppAd.onPause();
    }

    public FavouriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.favorite_fragment, container, false);
        fav_item_list = new ArrayList<>();

        this.conn = null;
        constantfile = new Constant();
        db = new DbAdapter(getActivity());
        db.open();

        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

      //  startAppAd = new StartAppAd(getActivity());
        intializeappnextads();

        content_favorite = (RelativeLayout) rootView.findViewById(R.id.content_favorite);
        items_recycler = (RecyclerView) rootView.findViewById(R.id.items_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        items_recycler.setLayoutManager(mLayoutManager);
        items_recycler.setItemAnimator(new DefaultItemAnimator());
        items_recycler.setHasFixedSize(true);
        no_data_text = (TextView) rootView.findViewById(R.id.no_data_text);


        return rootView;
    }

    private void showData() {
        db = new DbAdapter(getActivity());
        db.open();
        fav_item_list = new ArrayList<Item_images>();
        Cursor row = db.fetchAllData();
        for (row.moveToFirst(); !row.isAfterLast(); row.moveToNext()) {
            Item_images temp_item = new Item_images();
            temp_item.setId(row.getString(row.getColumnIndex("item_id")));
            temp_item.setItem_title(row.getString(row.getColumnIndex("item_name")));
            temp_item.setItem_description(row.getString(row.getColumnIndex("item_details")));
            temp_item.setImage(row.getString(row.getColumnIndex("image")));
            temp_item.setImage_thumb(row.getString(row.getColumnIndex("image_thumb")));
            temp_item.setTotal_views(row.getString(row.getColumnIndex("total_views")));
            temp_item.setDate(row.getString(row.getColumnIndex("date")));
            temp_item.setCat_id(row.getString(row.getColumnIndex("cat_id")));
            temp_item.setCategory_name(row.getString(row.getColumnIndex("cat_name")));
            temp_item.setCategory_image(row.getString(row.getColumnIndex("cat_img")));

            fav_item_list.add(temp_item);
        }

        int ii = fav_item_list.size();
        if (ii <= 0) {
            no_data_text.setVisibility(View.VISIBLE);
            items_recycler.setVisibility(View.GONE);
        } else {
            items_recycler.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            setAdapterToListview();
        }
    }

    public void setAdapterToListview() {
        mainAdapter = new FavItemsAdapter(getActivity(), fav_item_list);
        mainAdapter.setClickListener(this);
        items_recycler.setAdapter(mainAdapter);
    }



    @Override
    public void onResume() {
        super.onResume();

        showData();
      //  startAppAd.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity)getActivity()).SelectItem(getActivity().getResources().getString(R.string.app_name),0);
                }
                return true;
            }
        });

    }

    @Override
    public void onItemClick(int position, Item_images pass_getset, View v) {
        Constant.Passing_item_id = pass_getset.getId();
        Constant.Passing_item_objct = new Item_images();
        Constant.Passing_item_objct = pass_getset;
        if (Constant.Adscountlisting >= 2){
           // callstartappads();
            callingadsonclick();
        }else{
            Constant.Adscountlisting++;
            Intent detailact = new Intent(getActivity(), FavDetailActivity.class);
            startActivity(detailact);
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
        Intent intent = new Intent(getActivity(), FavDetailActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    public void callstartappads(){
//        final ProgressDialog progress = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
//        progress.setMessage("Loading Ad");
//        progress.setCancelable(false);
//        progress.show();
//        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC,new AdEventListener() {
//            @Override
//            public void onReceiveAd(com.startapp.android.publish.adsCommon.Ad ad) {
//                Constant.Adscountlisting = 1;
//                if (progress.isShowing()){
//                    progress.dismiss();
//                }
//                startAppAd.showAd(new AdDisplayListener() {
//                    @Override
//                    public void adHidden(com.startapp.android.publish.adsCommon.Ad ad) {
//                        onAdsresponce();
//                    }
//                    @Override
//                    public void adDisplayed(com.startapp.android.publish.adsCommon.Ad ad) { }
//                    @Override
//                    public void adClicked(com.startapp.android.publish.adsCommon.Ad ad) { }
//                    @Override
//                    public void adNotDisplayed(com.startapp.android.publish.adsCommon.Ad ad) {
//                        onAdsresponce();
//                    }
//                });
//            }
//            @Override
//            public void onFailedToReceiveAd(Ad ad) {
//            }
//        });
//    }

}
