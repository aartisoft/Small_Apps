package com.techcorp.mahatmagandhi.quotes.Adapter;


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
import com.techcorp.mahatmagandhi.quotes.R;
import com.techcorp.mahatmagandhi.quotes.gettersetter.Item_images;
import com.techcorp.mahatmagandhi.quotes.widgets.CircleImageView;

import java.util.ArrayList;

import static com.techcorp.mahatmagandhi.quotes.Constant.show_app_icon;


public class MainHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Item_images> BoLitemList = new ArrayList<>();
    private static MyClickListener myClickListener;
    private Context context;

    public static final int VIEW_TYPE_ITEM = 0;


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

    public void adddata(ArrayList<Item_images> bolitemList) {
        this.BoLitemList.clear();
        this.BoLitemList.addAll(bolitemList);
        notifyDataSetChanged();
    }

    public MainHomeAdapter(Context context) {
        this.context = context;
        this.BoLitemList = new ArrayList<>();
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_row, parent, false);
            return new DataObjectHolder(view);
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
        return VIEW_TYPE_ITEM;
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position > 0 && BoLitemList.get(position).getImage().equals("ads") ? AD_TYPE : VIEW_TYPE_ITEM;
//    }


    public interface MyClickListener {
        public void onItemClick(int position, Item_images bolitemList, View v);
    }


}