package com.karelakefayde.hindi.Adapter;


import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.karelakefayde.hindi.widgets.CircleImageView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.karelakefayde.hindi.R;
import com.karelakefayde.hindi.gettersetter.Item_images;

import java.util.ArrayList;
import java.util.Collections;

import static com.karelakefayde.hindi.Constant.show_app_icon;


public class MainItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Item_images> BoLitemList = new ArrayList<>();
    private ArrayList<AdView> mAdItems = new ArrayList<>();
    private static MyClickListener myClickListener;
    private Context context;
    int[] androidColors;

    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_NULL = 5;
    public static final int VIEW_TYPE_LOADING = 1;
    private static final int AD_TYPE = 2;


    public void setnomoredata() {
        this.BoLitemList.removeAll(Collections.singleton(null));
        notifyDataSetChanged();
    }


    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        CircleImageView image_round;
        TextView item_title;
        CardView item_cardview;

        public DataObjectHolder(View itemView) {
            super(itemView);
            item_title = (TextView) itemView.findViewById(R.id.item_title);
            image_round = (CircleImageView) itemView.findViewById(R.id.image_round);
            item_cardview = (CardView) itemView.findViewById(R.id.item_cardview);
        }

    }

    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
        }
    }

    public void adddata(ArrayList<Item_images> bolitemList, int pagenumber) {
        if (pagenumber == 1) {
            this.BoLitemList.clear();
            this.BoLitemList.addAll(bolitemList);
            this.BoLitemList.add(null);
        } else {
            this.BoLitemList.removeAll(Collections.singleton(null));
            this.BoLitemList.addAll(bolitemList);
            this.BoLitemList.add(null);
        }
        notifyDataSetChanged();
    }

    public MainItemAdapter(Context context) {
        this.context = context;
        this.BoLitemList = new ArrayList<>();
        androidColors = context.getResources().getIntArray(R.array.colors);
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_row, parent, false);
            return new DataObjectHolder(view);
        } else if (viewType == VIEW_TYPE_NULL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_null, parent, false);
            return new NullVIewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }/* else if (viewType == AD_TYPE) {
            View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facebook_ads, parent, false);
            return new FacebookNativeHolder(view);
        }*/
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NullVIewHolder) {
        }
       /* if (holder instanceof FacebookNativeHolder) {

        }*/ else if (holder instanceof DataObjectHolder) {

            final DataObjectHolder userViewHolder = (DataObjectHolder) holder;

            final Item_images singleItem = BoLitemList.get(position);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                userViewHolder.item_title.setText(Html.fromHtml(singleItem.getItem_title() + "", Html.FROM_HTML_MODE_COMPACT));
            } else {
                userViewHolder.item_title.setText(Html.fromHtml(singleItem.getItem_title() + ""));
            }

            try {
                Glide.with(context).load(singleItem.getCategory_image()).placeholder(R.drawable.bg_transparant).into(userViewHolder.image_round);
                if (!show_app_icon && singleItem.getImage_thumb() != null && !singleItem.getImage_thumb().equals("")){
                    Glide.with(context).load(singleItem.getImage_thumb()).placeholder(R.drawable.bg_transparant).into(userViewHolder.image_round);
                }
            }catch (Exception e){

            }

//            int index = 0;
//            if (position != 0) {
//                index = position - (position / AD_DISPLAY_FREQUENCY);
//            }
//
//            final int finalIndex = index;
            userViewHolder.item_cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onItemClick(position, singleItem, v);
                }
            });

        } else if (holder instanceof ViewHolderLoading) {
            if (position == BoLitemList.size() - 1) {
                ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }

    }

    public class FacebookNativeHolder extends RecyclerView.ViewHolder {

        public FacebookNativeHolder(View itemView) {
            super(itemView);
            loadNativeAd(itemView);
        }

        private void loadNativeAd(final View view) {
            if (view.getHeight() < 2){
                LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.native_ad_container);
                AdView adView = new AdView(context, context.getString(R.string.facebook_native_id), AdSize.RECTANGLE_HEIGHT_250);
                adContainer.addView(adView);
                adView.loadAd();
            }

        }

    }

    public class NullVIewHolder extends RecyclerView.ViewHolder {
        public NullVIewHolder(View view) {
            super(view);
        }

    }


    @Override
    public int getItemCount() {
        return BoLitemList == null ? 0 : BoLitemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return BoLitemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    public interface MyClickListener {
        public void onItemClick(int position, Item_images bolitemList, View v);

    }


}