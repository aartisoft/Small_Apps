package com.teluguvideostatusfor.forwhatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teluguvideostatusfor.forwhatsapp.R;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.Collections;


public class CommonListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_collections> mImageLatest;
    private Context context;
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_ITEM_portrait = 2;
    public String layout_type = "landscape";
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

    public CommonListingAdapter(Context context, String layout_type) {
        this.context = context;
        this.layout_type = layout_type;
        this.mImageLatest = new ArrayList<>();
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
        } else if (viewType == VIEW_TYPE_ITEM_portrait) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.portrait_common_listing_item, parent, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int height = (int) (parent.getHeight() / 2.7);
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
            return new ViewHolderPortrait(view);
        }else if (viewType == VIEW_TYPE_LOADING) {
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


        } else if (holder instanceof ViewHolderPortrait) {
            ViewHolderPortrait Viewportrait = (ViewHolderPortrait) holder;
            final Item_collections itemobj = (Item_collections) mImageLatest.get(position);
            Glide.with(context)
                    .load(itemobj.getVideo_image_thumb())
                    .thumbnail(0.10f)
                    .placeholder(R.drawable.image_placeholder)
                    .into(Viewportrait.image_video_thumb);

            Viewportrait.video_name.setText(itemobj.getVideo_name()+"");
            Viewportrait.video_views.setText(itemobj.getTotal_views()+" Views");

            Viewportrait.image_video_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onLatestItemClick(position, itemobj, mImageLatest, v);
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
        return mImageLatest.get(position) == null ? VIEW_TYPE_LOADING : layout_type.equals("portrait")?VIEW_TYPE_ITEM_portrait  : VIEW_TYPE_ITEM;
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

    public class ViewHolderPortrait extends RecyclerView.ViewHolder {
        public ImageView image_video_thumb;
        public TextView video_name,video_views;

        public ViewHolderPortrait(View v) {
            super(v);
            image_video_thumb = (ImageView) v.findViewById(R.id.image_video_thumb);
            video_name = (TextView) v.findViewById(R.id.video_name);
            video_views = (TextView) v.findViewById(R.id.video_views);
            video_views.setVisibility(View.GONE);
        }

    }


    public interface MyClickListener {
        public void onLatestItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v);
    }



}