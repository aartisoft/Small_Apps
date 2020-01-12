package com.techcorp.babasaheb.ambedkarquotes.inhindi.Adapter;


import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techcorp.babasaheb.ambedkarquotes.inhindi.R;
import com.techcorp.babasaheb.ambedkarquotes.inhindi.gettersetter.Item_images;
import com.techcorp.babasaheb.ambedkarquotes.inhindi.widgets.CircleImageView;

import java.util.ArrayList;

import static com.techcorp.babasaheb.ambedkarquotes.inhindi.Constant.show_app_icon;

public class FavItemsAdapter extends RecyclerView.Adapter<FavItemsAdapter.DataObjectHolder> {

    private ArrayList<Item_images> itemsList = new ArrayList<>();
    private static MyClickListener myClickListener;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{

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

    public FavItemsAdapter(Context context, ArrayList<Item_images> subcategoryList) {
        this.context = context;
        itemsList.clear();
        this.itemsList.addAll(subcategoryList);
        notifyDataSetChanged();
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        final Item_images singleItem = itemsList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.item_title.setText(Html.fromHtml(singleItem.getItem_title()+"", Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.item_title.setText(Html.fromHtml(singleItem.getItem_title()+""));
        }

        Log.e("gettimagepath"," category - "+singleItem.getCategory_image());
        Log.e("gettimagepath"," item - "+singleItem.getImage_thumb());

        try {
            Glide.with(context).load(singleItem.getCategory_image()).placeholder(R.drawable.bg_transparant).into(holder.image_round);
            if (!show_app_icon && singleItem.getImage_thumb() != null && !singleItem.getImage_thumb().equals("")){
                Glide.with(context).load(singleItem.getImage_thumb()).placeholder(R.drawable.bg_transparant).into(holder.image_round);
            }
        }catch (Exception e){

        }

        holder.item_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onItemClick(position,singleItem, v);
            }
        });
    }



    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public interface MyClickListener {
        void onItemClick(int position,Item_images pass_getset, View v);
    }

}
