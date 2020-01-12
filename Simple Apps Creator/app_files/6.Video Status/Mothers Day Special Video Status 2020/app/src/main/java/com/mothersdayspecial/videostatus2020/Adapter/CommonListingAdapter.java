package com.mothersdayspecial.videostatus2020.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mothersdayspecial.videostatus2020.R;
import com.mothersdayspecial.videostatus2020.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.Collections;

public class CommonListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_collections> mImageLatest;
    private Context context;
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;
    private String screentype = "";
    private static final int AD_DISPLAY_FREQUENCY = 10;
    int index = 0;


    private static MyClickListener myClickListener;


    public void setnomoredata() {
        this.mImageLatest.removeAll(Collections.singleton(null));
        this.mImageLatest.removeAll(Collections.singleton(null));
        notifyDataSetChanged();
    }

    public void clearalldata() {
        this.mImageLatest.clear();
        this.mImageLatest.clear();
        notifyDataSetChanged();
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    public void adddata(ArrayList<Item_collections> mImageThum, int pagenumber) {
        if (pagenumber == 1) {
            index = AD_DISPLAY_FREQUENCY;
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


    public CommonListingAdapter(Context context, String screentype) {
        this.context = context;
        this.mImageLatest = new ArrayList<>();
        this.screentype = screentype;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_common_list, parent, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int height = (int) (parent.getWidth() / 3);
            if (screentype.equals("portrait")) {
                height = (int) (parent.getHeight() / 3);
            }
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

            final Item_collections itemobj = mImageLatest.get(position);

            Glide.with(context)
                    .load(itemobj.getVideo_image_thumb())
                    .thumbnail(0.10f)
                    .placeholder(R.drawable.image_placeholder)
                    .into(userViewHolder.image_latest);

            userViewHolder.video_name.setText(itemobj.getVideo_name() + "");

            userViewHolder.image_latest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onCommonItemClick(position, itemobj, mImageLatest, v);
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
        Object recyclerViewItem = mImageLatest.get(position);
        if (recyclerViewItem == null) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }


//    @Override
//    public int getItemViewType(int position) {
//        return mImageLatest.get(position) == null ? VIEW_TYPE_LOADING : position > 0 && (position % AD_DISPLAY_FREQUENCY == 0) ? AD_TYPE : (position == 0) && (mImageLatest.size() > 1) ? VIEW_TYPE_NULL : VIEW_TYPE_ITEM;
//    }


    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
        }
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public ImageView image_latest;
        TextView video_name;

        public ViewHolderRow(View v) {
            super(v);
            image_latest = (ImageView) v.findViewById(R.id.image_latest);
            video_name = (TextView) itemView.findViewById(R.id.video_name);
        }

    }


//    private static class NativeAdHolder extends RecyclerView.ViewHolder {
//
//        private LinearLayout mContainer;
//        private ImageView mIcon;
//        private TextView mTitle;
//        private TextView mDescription;
//        private TextView mImageUrl;
//
//        private NativeAdHolder(View view) {
//            super(view);
//
//            mContainer = view.findViewById(R.id.container);
//            mIcon = view.findViewById(R.id.ivIcon);
//            mTitle = view.findViewById(R.id.tvTitle);
//            mDescription = view.findViewById(R.id.tvDescription);
//            mImageUrl = view.findViewById(R.id.tvImageUrl);
//        }
//
//        private void bindView(@NonNull NativeAdDetails ad) {
//            mIcon.setImageBitmap(ad.getImageBitmap());
//            mTitle.setText(ad.getTitle());
//            mDescription.setText(ad.getDescription());
//            mImageUrl.setText(ad.getImageUrl());
//            //ad.registerViewForInteraction(mContainer);
//        }
//    }

    public interface MyClickListener {
        public void onCommonItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v);
    }

}