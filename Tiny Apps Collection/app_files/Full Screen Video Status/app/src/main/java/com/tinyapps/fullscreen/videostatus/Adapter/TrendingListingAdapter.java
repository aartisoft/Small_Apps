package com.tinyapps.fullscreen.videostatus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tinyapps.fullscreen.videostatus.R;
import com.tinyapps.fullscreen.videostatus.gettersetter.Item_collections;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class TrendingListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_collections> mImageLatest;
    private Context context;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date c;
    String formattedDate;
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;


    private static MyClickListener myClickListener;


    public void setnomoredata() {
        this.mImageLatest.removeAll(Collections.singleton(null));
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

    public TrendingListingAdapter(Context context) {
        this.context = context;
        this.mImageLatest = new ArrayList<>();
        c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.landscape_common_listing_item, parent, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int height = (int) (parent.getHeight() / 2.7);
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
            return new ViewHolderRow(view);
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

            userViewHolder.video_name.setText(itemobj.getVideo_name()+"");
            userViewHolder.video_views.setText(itemobj.getTotal_views()+" Views");

            userViewHolder.image_video_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onLatestItemClick(position, itemobj, mImageLatest, v);
                }
            });



        }  else if (holder instanceof ViewHolderLoading) {
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
        return mImageLatest.get(position) == null ? VIEW_TYPE_LOADING :VIEW_TYPE_ITEM;
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
            video_name = (TextView) v.findViewById(R.id.video_name);
            video_views = (TextView) v.findViewById(R.id.video_views);
        }

    }


    public interface MyClickListener {
        public void onLatestItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v);
    }

}