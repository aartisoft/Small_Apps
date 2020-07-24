package com.teluguvideostatusfor.forwhatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.teluguvideostatusfor.forwhatsapp.R;
import com.teluguvideostatusfor.forwhatsapp.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> implements HomecatwiseItemAdapter.MyClickListener {
    private Context context;
    private List<Item_collections> mSliderItems = new ArrayList<>();
    HomecatwiseItemAdapter homecatitemtadapter;
    private static SliderAdapterExample.MyClickListener myClickListener;

    public SliderAdapterExample(Context context) {
        this.context = context;
    }

    public void renewItems(List<Item_collections> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Item_collections sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_category, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH holder, final int position) {

       /* HomeCatData sliderItem = mSliderItems.get(position);


        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.textViewDescription.setTextColor(Color.WHITE);
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImageUrl())
                .fitCenter()
                .into(viewHolder.imageViewBackground);*/

        final Item_collections itemobj = (Item_collections) mSliderItems.get(position);

        if (itemobj!=null){
            homecatitemtadapter = new HomecatwiseItemAdapter(context);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.catitem_recycler.setLayoutManager(mLayoutManager);
            holder.catitem_recycler.setHasFixedSize(true);
            holder.catitem_recycler.setItemAnimator(new DefaultItemAnimator());

            holder.catwise_title.setText(itemobj.getCategory_name()+"");



            holder.catwise_viewall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.oncatwiseviewallClick(position, itemobj,v);
                }
            });
        }

        /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    @Override
    public void onhomecatwiseItemClick(int position, Item_collections passdata, View v) {

    }

    public void addItem(ArrayList<Item_collections> catdatalisting, int i) {
        if (i == 1) {
            mSliderItems.clear();
            mSliderItems.addAll(catdatalisting);
        } else {
            mSliderItems.addAll(catdatalisting);
        }
        notifyDataSetChanged();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        TextView catwise_viewall,catwise_title;
        RecyclerView catitem_recycler;

        public SliderAdapterVH(View itemView) {
            super(itemView);

            catitem_recycler = (RecyclerView) itemView.findViewById(R.id.catitem_recycler);
            catwise_viewall = (TextView) itemView.findViewById(R.id.catwise_viewall);
            catwise_title = (TextView) itemView.findViewById(R.id.catwise_title);
        }
    }

    public interface MyClickListener {
        public void oncatwiseviewallClick(int position, Item_collections passdata, View v);
        public void oncatwiseitemClickPass(int position, Item_collections passdata, View v);
    }
}
