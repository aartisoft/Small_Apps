package com.makarsankranti.videostatus2020.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.makarsankranti.videostatus2020.R;
import com.makarsankranti.videostatus2020.UnifiedNativeAdViewHolder;
import com.makarsankranti.videostatus2020.gettersetter.Item_collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.makarsankranti.videostatus2020.Constant.NUMBER_OF_ADS;

public class CommonListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_collections> mImageLatest;
    private List<Object> mRecyclerViewItems = new ArrayList<>();
    private Context context;
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;
    public static final int AD_TYPE = 2;
    public static final int VIEW_TYPE_NULL = 5;
    private String screentype = "";
    private static final int AD_DISPLAY_FREQUENCY = 10;
    int index = 0;


    private static MyClickListener myClickListener;


    public void setnomoredata() {
        this.mImageLatest.removeAll(Collections.singleton(null));
        this.mRecyclerViewItems.removeAll(Collections.singleton(null));
        notifyDataSetChanged();
    }

    public void clearalldata() {
        this.mImageLatest.clear();
        this.mRecyclerViewItems.clear();
        notifyDataSetChanged();
    }

    public void setClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    public void adddata(ArrayList<Item_collections> mImageThum, int pagenumber) {
        if (pagenumber == 1) {
            index = AD_DISPLAY_FREQUENCY;
            this.mImageLatest.clear();
            this.mRecyclerViewItems.clear();
            this.mImageLatest.addAll(mImageThum);
            this.mRecyclerViewItems.addAll(mImageThum);
            this.mRecyclerViewItems.add(null);
        } else {
            this.mRecyclerViewItems.removeAll(Collections.singleton(null));
            this.mImageLatest.addAll(mImageThum);
            this.mRecyclerViewItems.addAll(mImageThum);
            this.mRecyclerViewItems.add(null);
        }
        notifyDataSetChanged();
    }

    public void insertAdsInMenuItems(List<UnifiedNativeAd> mNativeAds, int page) {
        try {
            if (mNativeAds.size() <= 0) {
                return;
            }
            if (page == 1) {
                index = AD_DISPLAY_FREQUENCY;
            }

            for (UnifiedNativeAd ad : mNativeAds) {
                if (index < mRecyclerViewItems.size()) {
                    mRecyclerViewItems.add(index, mNativeAds.get(index % NUMBER_OF_ADS));
                    index++;
                    index = index + AD_DISPLAY_FREQUENCY;
                } else {
                    return;
                }

            }
            notifyDataSetChanged();
        } catch (Exception e) {

        }
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
        } else if (viewType == AD_TYPE) {
            View unifiedNativeLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_unified, parent, false);
            return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
//            View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facebook_ads, parent, false);
//            return new FacebookNativeHolder(view);
        } else if (viewType == VIEW_TYPE_NULL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_null, parent, false);
            return new NullVIewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof UnifiedNativeAdViewHolder) {
            UnifiedNativeAd nativeAd = (UnifiedNativeAd) mRecyclerViewItems.get(position);
            populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
        } else if (holder instanceof ViewHolderRow) {
            ViewHolderRow userViewHolder = (ViewHolderRow) holder;

            final Item_collections itemobj = (Item_collections) mRecyclerViewItems.get(position);

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
            if (position == mRecyclerViewItems.size() - 1) {
                ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems == null ? 0 : mRecyclerViewItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = mRecyclerViewItems.get(position);
        if (recyclerViewItem == null) {
            return VIEW_TYPE_LOADING;
        } else if (recyclerViewItem instanceof UnifiedNativeAd) {
            return AD_TYPE;
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

   /* public  class FacebookNativeHolder extends  RecyclerView.ViewHolder {
        private final String TAG = "MainItemAdapter";
        private LinearLayout nativeAdContainer;
        private LinearLayout adView;
        private NativeAd nativeAd;
        public FacebookNativeHolder(View view) {
            super(view);
            loadNativeAd(view);
        }

        private void loadNativeAd(final View view) {

            LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.native_ad_container);
            AdView adView = new AdView(context, context.getString(R.string.facebook_scroll_id), AdSize.RECTANGLE_HEIGHT_250);
            adContainer.addView(adView);
            adView.loadAd();

//            nativeAd = new NativeAd(context,context.getString(R.string.facebook_native_id));
//            nativeAd.setAdListener(new NativeAdListener() {
//                @Override
//                public void onMediaDownloaded(Ad ad) {
//                    Log.e(TAG, "Native ad finished downloading all assets.");
//                }
//
//                @Override
//                public void onError(Ad ad, AdError adError) {
//                    Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
//                }
//
//                @Override
//                public void onAdLoaded(Ad ad) {
//                    Log.d(TAG, "Native ad is loaded and ready to be displayed!");
//                    if (nativeAd == null || nativeAd != ad) {
//                        return;
//                    }
//                    inflateAd(nativeAd,view);
//                }
//
//                @Override
//                public void onAdClicked(Ad ad) {
//                    Log.d(TAG, "Native ad clicked!");
//                }
//
//                @Override
//                public void onLoggingImpression(Ad ad) {
//                    Log.d(TAG, "Native ad impression logged!");
//                }
//            });
//            nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
        }

//        private void inflateAd(NativeAd nativeAd, View view) {
//
//            nativeAd.unregisterView();
//
//            nativeAdContainer = view.findViewById(R.id.native_ad_container);
//            LayoutInflater inflater = LayoutInflater.from(context);
//            adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdContainer, false);
//            nativeAdContainer.addView(adView);
//
//
//            LinearLayout adChoicesContainer = view.findViewById(R.id.ad_choices_container);
//            AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);
//            adChoicesContainer.addView(adChoicesView, 0);
//
//            AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
//            TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
//            MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
//            TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
//            TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
//            TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
//            Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
//
//            nativeAdTitle.setText(nativeAd.getAdvertiserName());
//            nativeAdBody.setText(nativeAd.getAdBodyText());
//            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
//            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
//            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//            sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
//
//            List<View> clickableViews = new ArrayList<>();
//            clickableViews.add(nativeAdTitle);
//            clickableViews.add(nativeAdCallToAction);
//
//            nativeAd.registerViewForInteraction(
//                    adView,
//                    nativeAdMedia,
//                    nativeAdIcon,
//                    clickableViews);
//        }

    }*/

    private void populateNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    public class NullVIewHolder extends RecyclerView.ViewHolder {
        public NullVIewHolder(View view) {
            super(view);
        }

    }


    public interface MyClickListener {
        public void onCommonItemClick(int position, Item_collections passdata, ArrayList<Item_collections> passarray, View v);
    }

}