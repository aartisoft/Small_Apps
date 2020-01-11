package com.happymothersdayquotes.status.wishesand.Images.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_videos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class VideoListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_videos> mImageLatest;
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


    public void adddata(ArrayList<Item_videos> mImageThum, int pagenumber) {
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

    public VideoListingAdapter(Context context) {
        this.context = context;
        this.mImageLatest = new ArrayList<>();
        c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_listing_video_item, parent, false);
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
            final Item_videos itemobj = (Item_videos) mImageLatest.get(position);

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
                    ArrayList<Item_videos> mListtemp = new ArrayList<>();
                    mListtemp.addAll(mImageLatest);
                    mListtemp.removeAll(Collections.singleton(null));
                    myClickListener.onVideoItemClick(position, itemobj, mListtemp, v);
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
        public void onVideoItemClick(int position, Item_videos passdata, ArrayList<Item_videos> passarray, View v);
    }

}