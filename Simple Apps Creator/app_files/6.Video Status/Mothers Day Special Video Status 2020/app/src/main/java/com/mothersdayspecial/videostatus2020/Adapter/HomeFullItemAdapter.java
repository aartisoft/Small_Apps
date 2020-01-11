package com.mothersdayspecial.videostatus2020.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mothersdayspecial.videostatus2020.R;
import com.mothersdayspecial.videostatus2020.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.List;

public class HomeFullItemAdapter extends RecyclerView.Adapter<HomeFullItemAdapter.DataObjectHolder> {

    List<Item_collections> mRecyclerViewItems = new ArrayList<>();

    private static String LOG_TAG = "HomeFullItemAdapter";
    private static MyClickListener myClickListener;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        ImageView image_latest;
        TextView video_name;

        public DataObjectHolder(View itemView) {
            super(itemView);
            image_latest = (ImageView) itemView.findViewById(R.id.image_trending);
            video_name = (TextView) itemView.findViewById(R.id.video_name);

        }

    }



    public HomeFullItemAdapter(Context context) {
        this.context = context;
        mRecyclerViewItems = new ArrayList<>();
    }

    public void adddata(List<Item_collections> data, int pagenumber) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_trending, parent, false);
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() / 2.7);
        v.setLayoutParams(layoutParams);
        DataObjectHolder dataobject = new DataObjectHolder(v);
        return dataobject;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {


        final Item_collections itemobj = (Item_collections) mRecyclerViewItems.get(position);
        Glide.with(context)
                .load(itemobj.getVideo_image_thumb())
                .thumbnail(0.10f)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.image_latest);

        holder.video_name.setText(itemobj.getVideo_name()+"");

        holder.image_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onCatItemClick(position, itemobj,v);
            }
        });


    }



    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface MyClickListener {
        public void onCatItemClick(int position, Item_collections passdata, View v);
    }

}
