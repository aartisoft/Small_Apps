package com.techcorp.teluguvideostatus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techcorp.teluguvideostatus.R;
import com.techcorp.teluguvideostatus.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.Collections;

public class SearchListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_collections> mImageLatest;
    private Context context;
    public static final int VIEW_TYPE_PORT = 0;
    public static final int VIEW_TYPE_LAND = 1;
    public static final int VIEW_TYPE_LOADING = 2;


    private static MyClickListener myClickListener;


    public void setnomoredata() {
        this.mImageLatest.removeAll(Collections.singleton(null));
        notifyDataSetChanged();
    }

    public void clearalldata() {
        this.mImageLatest.clear();
        notifyDataSetChanged();
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    public void adddata(ArrayList<Item_collections> mImageThum, int pagenumber) {
        if (pagenumber == 1) {
            this.mImageLatest.clear();
            this.mImageLatest.addAll(mImageThum);
            this.mImageLatest.add(null);
        } else {
            this.mImageLatest.removeAll(Collections.singleton(null));
            this.mImageLatest.addAll(mImageThum);
            this.mImageLatest.add(null);
        }
        notifyDataSetChanged();
    }

    public SearchListingAdapter(Context context) {
        this.context = context;
        this.mImageLatest = new ArrayList<>();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PORT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.straggered_listing_item, parent, false);
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = (int) (parent.getWidth() / 1.5);
            v.setLayoutParams(layoutParams);
            ViewHolderRow dataobject = new ViewHolderRow(v);
            return dataobject;
        }else if (viewType == VIEW_TYPE_LAND){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.straggered_listing_item, parent, false);
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = (int) (parent.getWidth() / 2.5);
            v.setLayoutParams(layoutParams);
            ViewHolderRow dataobject = new ViewHolderRow(v);
            return dataobject;
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderRow) {

            ViewHolderRow userViewHolder = (ViewHolderRow) holder;
            final Item_collections itemobj = (Item_collections) mImageLatest.get(position);
            Glide.with(context)
                    .load(itemobj.getVideo_image_thumb())
                    .thumbnail(0.10f)
                    .placeholder(R.drawable.image_placeholder)
                    .into(userViewHolder.image_video_thumb);

            userViewHolder.video_name.setText(itemobj.getVideo_name() + "");
            userViewHolder.video_views.setText(itemobj.getTotal_views() + " Views");

            userViewHolder.image_video_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onSearchItemClick(position, itemobj, mImageLatest, v);
                }
            });


        } else if (holder instanceof ViewHolderLoading) {
            if (position == mImageLatest.size() - 1) {
                ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mImageLatest == null ? 0 : mImageLatest.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mImageLatest.get(position) == null ? VIEW_TYPE_LOADING :mImageLatest.get(position).getIs_type().toLowerCase().equals("portrait") ? VIEW_TYPE_PORT : VIEW_TYPE_LAND ;
    }


    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
        }
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public ImageView image_video_thumb;
        public TextView video_name,video_views;

        public ViewHolderRow(View v) {
            super(v);
            image_video_thumb = (ImageView) v.findViewById(R.id.image_video_thumb);
            video_name = (TextView) itemView.findViewById(R.id.video_name);
            video_views = (TextView) itemView.findViewById(R.id.video_views);
            video_views.setVisibility(View.VISIBLE);
        }

    }

    public interface MyClickListener {
        public void onSearchItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v);
    }



}