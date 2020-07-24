package com.techcorp.teluguvideostatus.Adapter;


import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.techcorp.teluguvideostatus.R;
import com.techcorp.teluguvideostatus.gettersetter.HomeCatData;

import java.util.ArrayList;
import java.util.List;

public class HomeCatlistAdapter extends RecyclerView.Adapter<HomeCatlistAdapter.DataObjectHolder> {

    List<HomeCatData> mRecyclerViewItems = new ArrayList<>();

    private static String LOG_TAG = "HomeCatlistAdapter";
    private static MyClickListener myClickListener;
    private Context context;
    TypedArray androidColors;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        ImageView category_back;
        TextView category_name;

        public DataObjectHolder(View itemView) {
            super(itemView);
            category_back = (ImageView) itemView.findViewById(R.id.category_back);
            category_name = (TextView) itemView.findViewById(R.id.category_name);

        }

    }

    public HomeCatlistAdapter(Context context) {
        this.context = context;
        mRecyclerViewItems = new ArrayList<>();
        androidColors = context.getResources().obtainTypedArray(R.array.random_imgs);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_category_list, parent, false);
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        int width = (int) (parent.getWidth() / 4.9);
        layoutParams.width = width;
        v.setLayoutParams(layoutParams);
        DataObjectHolder dataobject = new DataObjectHolder(v);
        return dataobject;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {


        final HomeCatData itemobj = (HomeCatData) mRecyclerViewItems.get(position);

        holder.category_name.setText(itemobj.getCategory_name());
        holder.category_back.setImageResource(androidColors.getResourceId(position % androidColors.length(), 0));
        //holder.category_back.setColorFilter(randomAndroidColor, android.graphics.PorterDuff.Mode.MULTIPLY);

        holder.category_back.setOnClickListener(new View.OnClickListener() {
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
        public void onCatItemClick(int position, HomeCatData passdata, View v);
    }

}
