package com.karelakefayde.hindi.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.karelakefayde.hindi.Activity.FavDetailActivity;
import com.karelakefayde.hindi.Activity.MainActivity;
import com.karelakefayde.hindi.Adapter.FavItemsAdapter;
import com.karelakefayde.hindi.ConnectionDetector;
import com.karelakefayde.hindi.Constant;
import com.karelakefayde.hindi.DbAdapter;
import com.karelakefayde.hindi.R;
import com.karelakefayde.hindi.gettersetter.Item_images;

import java.util.ArrayList;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class FavouriteFragment extends Fragment implements FavItemsAdapter.MyClickListener,Constant.Callingafterads{

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
        Intent detailact = new Intent(getActivity(), FavDetailActivity.class);
        startActivity(detailact);
//        if (Constant.Adscountlisting == 2){
//
//            constantfile.loadInterstitialAd(getActivity(),this);
//        }else{
//            Constant.Adscountlisting++;
//            ((MainActivity) getActivity()).SelectItem(PassingTitle, 4);
//        }
    }

    @Override
    public void onAdsresponce(Boolean showing) {
        Constant.Adscountlisting = 1;
        Intent detailact = new Intent(getActivity(), FavDetailActivity.class);
        startActivity(detailact);
    }

}
