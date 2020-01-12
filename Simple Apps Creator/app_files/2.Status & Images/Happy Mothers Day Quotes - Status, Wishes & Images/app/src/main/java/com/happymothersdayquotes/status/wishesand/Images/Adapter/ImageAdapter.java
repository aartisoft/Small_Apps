package com.happymothersdayquotes.status.wishesand.Images.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_images;

import java.util.ArrayList;
import java.util.Collections;


public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_images> mImageList;
    private Context context;
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    private static MyClickListener myClickListener;


    public void setnomoredata() {
        this.mImageList.removeAll(Collections.singleton(null));
        notifyDataSetChanged();
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    public void adddata(ArrayList<Item_images> mImageThum, int pagenumber) {
        if (pagenumber == 0) {
            this.mImageList.clear();
            this.mImageList.addAll(mImageThum);
            this.mImageList.add(null);
        } else {
            this.mImageList.removeAll(Collections.singleton(null));
            this.mImageList.addAll(mImageThum);
            this.mImageList.add(null);
        }
        notifyDataSetChanged();
    }

    public ImageAdapter(Context context) {
        this.context = context;
        this.mImageList = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_row, parent, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int height = (int) (parent.getHeight() / 3);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderRow) {

            ViewHolderRow userViewHolder = (ViewHolderRow) holder;
            Glide.with(context)
                    .load(mImageList.get(position).getWallpaper_image_thumb())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // log exception
                            Log.e("TAG", "Error loading image", e);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(userViewHolder.image_item);

            userViewHolder.bind(position);
        } else if (holder instanceof ViewHolderLoading) {
            if (position == mImageList.size()-1){
                ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mImageList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
        }
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public ImageView image_item;

        public ViewHolderRow(View v) {
            super(v);
            image_item = (ImageView) v.findViewById(R.id.image_item);
        }

        public void bind(final int pos) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Item_images> mListtemp = new ArrayList<>();
                    mListtemp.addAll(mImageList);
                    mListtemp.removeAll(Collections.singleton(null));
                    myClickListener.onImageItemClick(pos, mImageList.get(pos), mListtemp, v);
                }
            });
        }
    }


    public interface MyClickListener {
        public void onImageItemClick(int position, Item_images passdata, ArrayList<Item_images> passarray, View v);
    }

}