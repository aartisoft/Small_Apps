package com.tinyapps.mothersdayphotoframe.Adapter;


import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.tinyapps.mothersdayphotoframe.R;

import java.util.ArrayList;

public class FrameItemAdapter extends RecyclerView.Adapter<FrameItemAdapter.DataObjectHolder> {
    private ArrayList<String> recipeList;
    private static MyClickListener myClickListener;
    private Context context;


    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        ProgressBar progressBar;


        public DataObjectHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image_item);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public FrameItemAdapter(Context context, ArrayList<String> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_frame_item, parent, false);
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        layoutParams.height = (int) (parent.getWidth() / 2);
//        view.setLayoutParams(layoutParams);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        Glide.with(context)
                .load(Uri.parse("file:///android_asset/showimage/"+recipeList.get(position)))
                .into(holder.image);

    }



    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);

    }


}
