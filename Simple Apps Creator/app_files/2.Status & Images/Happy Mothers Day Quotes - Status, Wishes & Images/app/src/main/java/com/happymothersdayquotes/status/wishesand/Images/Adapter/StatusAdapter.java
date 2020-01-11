package com.happymothersdayquotes.status.wishesand.Images.Adapter;

import android.content.Context;
import android.os.Build;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_Status;

import java.util.ArrayList;
import java.util.Collections;

public class StatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_Status> statusList;
    Context context;
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    private static MyClickListener myClickListener;


    public StatusAdapter(Context context) {
        this.statusList = new ArrayList<>();
        this.context = context;
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void setnomoredata() {
        this.statusList.removeAll(Collections.singleton(null));
        notifyDataSetChanged();
    }


    public void adddata(ArrayList<Item_Status> mStatus, int pagenumber) {
        if (pagenumber == 0) {
            this.statusList.clear();
            this.statusList.addAll(mStatus);
            this.statusList.add(null);
        } else {
            this.statusList.removeAll(Collections.singleton(null));
            this.statusList.addAll(mStatus);
            this.statusList.add(null);
        }
        notifyDataSetChanged();
    }

    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
        }
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public TextView status_item;
        CardView status_view;

        public ViewHolderRow(View v) {
            super(v);
            status_item = (TextView) v.findViewById(R.id.status_item);
            status_view = (CardView)v.findViewById(R.id.status_view);
        }

        public void bind(final int pos) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Item_Status> mListtemp = new ArrayList<>();
                    mListtemp.addAll(statusList);
                    mListtemp.removeAll(Collections.singleton(null));
                    myClickListener.onStatusItemClick(pos, statusList.get(pos), mListtemp, v);
                }
            });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_row, parent, false);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                userViewHolder.status_item.setText(Html.fromHtml(statusList.get(position).getStatus_text(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                userViewHolder.status_item.setText(Html.fromHtml(statusList.get(position).getStatus_text()));
            }

            userViewHolder.bind(position);
        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return statusList == null ? 0 : statusList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return statusList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    public interface MyClickListener {
        public void onStatusItemClick(int position, Item_Status passdata, ArrayList<Item_Status> passarray, View v);
    }
}