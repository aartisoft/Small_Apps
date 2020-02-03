package com.punjabivideostatus.latestsong.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.punjabivideostatus.latestsong.R;
import com.punjabivideostatus.latestsong.gettersetter.HomeCatData;
import com.punjabivideostatus.latestsong.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.List;

public class HomeCatWiseAdapter extends RecyclerView.Adapter<HomeCatWiseAdapter.DataObjectHolder> implements HomecatwiseItemAdapter.MyClickListener{

    ArrayList<HomeCatData> mRecyclerViewItems = new ArrayList<>();
    HomecatwiseItemAdapter homecatitemtadapter;
    int width;
    private static String LOG_TAG = "HomeFulImdapter";
    private static MyClickListener myClickListener;
    public static final int VIEW_TYPE_PORT = 0;
    public static final int VIEW_TYPE_LAND = 1;


    private Context context;


    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        TextView catwise_viewall,catwise_title;
        RecyclerView catitem_recycler;

        public DataObjectHolder(View itemView) {
            super(itemView);
            catitem_recycler = (RecyclerView) itemView.findViewById(R.id.catitem_recycler);
            catwise_viewall = (TextView) itemView.findViewById(R.id.catwise_viewall);
            catwise_title = (TextView) itemView.findViewById(R.id.catwise_title);
        }

    }

    public HomeCatWiseAdapter(Context context,int width) {
        this.context = context;
        this.width = width;
        mRecyclerViewItems = new ArrayList<>();

    }


    public void adddata(List<HomeCatData> data, int pagenumber) {
        if (pagenumber == 1) {
            mRecyclerViewItems.clear();
            mRecyclerViewItems.addAll(data);
        } else {
            mRecyclerViewItems.addAll(data);
        }
        notifyDataSetChanged();
    }


    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PORT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_category, parent, false);
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = (int) (width / 1.2);
            v.setLayoutParams(layoutParams);
            DataObjectHolder dataobject = new DataObjectHolder(v);
            return dataobject;
        }else if (viewType == VIEW_TYPE_LAND){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_category, parent, false);
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = (int) (width / 1.9);
            v.setLayoutParams(layoutParams);
            DataObjectHolder dataobject = new DataObjectHolder(v);
            return dataobject;
        }
        return null;

    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {


        final HomeCatData itemobj = (HomeCatData) mRecyclerViewItems.get(position);

        if (itemobj.getData().size() > 0){
            homecatitemtadapter = new HomecatwiseItemAdapter(context);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.catitem_recycler.setLayoutManager(mLayoutManager);
            holder.catitem_recycler.setHasFixedSize(true);
            holder.catitem_recycler.setItemAnimator(new DefaultItemAnimator());
            homecatitemtadapter = new HomecatwiseItemAdapter(context);
            homecatitemtadapter.setClickListener(this);
            holder.catitem_recycler.setAdapter(homecatitemtadapter);
            homecatitemtadapter.adddata(itemobj.getData(), 1);

            holder.catwise_title.setText(itemobj.getCategory_name()+"");



            holder.catwise_viewall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.oncatwiseviewallClick(position, itemobj,v);
                }
            });
        }



    }



    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        return mRecyclerViewItems.get(position).getType_layout().toLowerCase().equals("portrait") ? VIEW_TYPE_PORT : VIEW_TYPE_LAND;
    }

    @Override
    public void onhomecatwiseItemClick(int position, Item_collections passdata, View v) {
        myClickListener.oncatwiseitemClickPass(position,passdata,v);
    }

    public interface MyClickListener {
        public void oncatwiseviewallClick(int position, HomeCatData passdata, View v);
        public void oncatwiseitemClickPass(int position, Item_collections passdata, View v);
    }



}
