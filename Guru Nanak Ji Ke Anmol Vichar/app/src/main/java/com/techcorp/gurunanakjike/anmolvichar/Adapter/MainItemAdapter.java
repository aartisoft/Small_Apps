package com.techcorp.gurunanakjike.anmolvichar.Adapter;


import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techcorp.gurunanakjike.anmolvichar.widgets.CircleImageView;
import com.facebook.ads.AdView;
import com.techcorp.gurunanakjike.anmolvichar.R;
import com.techcorp.gurunanakjike.anmolvichar.gettersetter.Item_images;

import java.util.ArrayList;
import java.util.Collections;

import static com.techcorp.gurunanakjike.anmolvichar.Constant.show_app_icon;


public class MainItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Item_images> BoLitemList = new ArrayList<>();
    private ArrayList<AdView> mAdItems = new ArrayList<>();
    private static MyClickListener myClickListener;
    private Context context;
    int[] androidColors;

    public static final int VIEW_TYPE_ITEM = 0;
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