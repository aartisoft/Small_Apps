package com.punjabivideostatus.latestsong.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.punjabivideostatus.latestsong.R;
import com.punjabivideostatus.latestsong.gettersetter.Item_category;

import java.util.ArrayList;

public class CategoriesListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_category> mImageLatest;
    private Context context;
    private static MyClickListener myClickListener;

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    public void adddata(ArrayList<Item_category> mImageThum) {
        this.mImageLatest.clear();
        this.mImageLatest.addAll(mImageThum);
        notifyDataSetChanged();
    }

    public CategoriesListingAdapter(Context context) {
        this.context = context;
        this.mImageLatest = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_listingitem, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int height = (int) (parent.getWidth() / 2);
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        return new ViewHolderRow(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolderRow getHolder = (ViewHolderRow) holder;
        final Item_category itemobj = (Item_category) mImageLatest.get(position);
        Glide.with(context).load(itemobj.getCategory_image_thumb()).into(getHolder.image_categories);
        getHolder.categories_name.setText(itemobj.getCategory_name()+"");

        getHolder.image_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onCategoriesItemClick(position, itemobj, v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageLatest == null ? 0 : mImageLatest.size();
    }


    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public ImageView image_categories;
        public TextView categories_name;

        public ViewHolderRow(View v) {
            super(v);
            image_categories = (ImageView) v.findViewById(R.id.image_categories);
            categories_name = (TextView) v.findViewById(R.id.categories_name);
        }

    }

    public interface MyClickListener {
        public void onCategoriesItemClick(int position, Item_category passdata, View v);
    }


}