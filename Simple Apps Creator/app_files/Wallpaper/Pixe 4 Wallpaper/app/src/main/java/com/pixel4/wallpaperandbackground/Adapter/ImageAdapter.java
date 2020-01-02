package com.pixel4.wallpaperandbackground.Adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pixel4.wallpaperandbackground.R;
import com.pixel4.wallpaperandbackground.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.Collections;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;
    private ArrayList<Item_collections> imageList;
    Activity main;
    private static MyClickListener myClickListener;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ProgressBar progressBar;


        public DataObjectHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image_item);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }


    }

    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
        }
    }

    public ImageAdapter(Context context) {
        this.context = context;
        this.imageList =new ArrayList<>();
    }

    public void setnomoredata() {
        this.imageList.removeAll(Collections.singleton(null));
        notifyDataSetChanged();
    }

    public void clearalldata() {
        this.imageList.clear();
        notifyDataSetChanged();
    }

    public void adddata(ArrayList<Item_collections> mImageThum, int pagenumber) {
        if (pagenumber == 0) {
            this.imageList.clear();
            this.imageList.addAll(mImageThum);
            this.imageList.add(null);
        } else {
            this.imageList.removeAll(Collections.singleton(null));
            this.imageList.addAll(mImageThum);
            this.imageList.add(null);
        }
        notifyDataSetChanged();
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_single_gallery_row, parent, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) (parent.getHeight() / 3);
            view.setLayoutParams(layoutParams);
            return new DataObjectHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof DataObjectHolder) {

            final DataObjectHolder userViewHolder = (DataObjectHolder) holder;

            Glide.with(context)
                    .load(imageList.get(position).getWallpaper_image_thumb())
                    .error(R.drawable.error)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // log exception
                            Log.e("geterrorimage",e+"");
                            userViewHolder.progressBar.setVisibility(View.GONE);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            userViewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(userViewHolder.image);

            userViewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onItemClick(position,imageList.get(position).getWallpaper_image(),imageList, v);
                }
            });

        } else if (holder instanceof ViewHolderLoading) {
            if (position == imageList.size()-1){
                ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }



    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return imageList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    public interface MyClickListener {
        public void onItemClick(int position, String ImgUrl, ArrayList<Item_collections> passarray, View v);

    }


}
