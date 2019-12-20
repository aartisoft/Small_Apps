package com.techcorp.ramayanaquotes.inhindi;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.techcorp.ramayanaquotes.inhindi.R;
import com.techcorp.ramayanaquotes.inhindi.gettersetter.Item_images;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Constant {
    public static final String APP_ID = "6";

    public static final String BASIC_URL = "http://simpleappscreator.com/small_apps/api/";
    //public static final String BASIC_URL = "http://192.168.0.104/small_apps/api/";

    public static final String GET_ADD_TOKEN = BASIC_URL+ "api_add_token.php";
    public static final String GET_ADD_VIEW = BASIC_URL+ "api_add_view.php";
    public static final String GET_COMMON_LISTING = BASIC_URL+ "api_appcommon_listing.php";
    public static final String GET_HOME_LISTING = BASIC_URL+ "api_home_listing.php";


    public static final int AD_DISPLAY_FREQUENCY = 10;
    public static int Adscountlisting = 1;
    public static boolean show_app_icon = false;

    public static String Passing_From = "HomeFragment";
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String notification_name = "RamayanaQuotes_notification";
    public static final String NOTIFICATION_CHANNEL_ID = "10001_RamayanaQuotes";


    public static final String SHARED_PREF = "ah_firebase_RamayanaQuotes";
    public static final String SHARED_Token = "regId";
    public static final String TOPIC_GLOBAL = "global";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    public static String Passing_item_id = "";
    public static ArrayList<Item_images> Passing_item_array = new ArrayList<>();
    public static Item_images Passing_item_objct = new Item_images();


    public void snackbarcommonview(Context mcontext, View coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public ArrayList<Item_images> ConvertJSONImage_Model(JSONArray getarray) throws JSONException {
        ArrayList<Item_images> temp_array = new ArrayList<>();

        for (int i = 0; i < getarray.length();i++){
            JSONObject pass = getarray.getJSONObject(i);
            Item_images temp_item = new Item_images();

            temp_item.setId(pass.getString("id"));
            temp_item.setItem_title(pass.getString("item_title"));
            temp_item.setItem_description(pass.getString("item_description"));
            temp_item.setImage(pass.getString("image"));
            temp_item.setImage_thumb(pass.getString("image_thumb"));
            temp_item.setTotal_views(pass.getString("total_views"));
            temp_item.setDate(pass.getString("date"));
            temp_item.setCat_id(pass.getString("cat_id"));
            temp_item.setCategory_name(pass.getString("category_name"));
            temp_item.setCategory_image(pass.getString("category_image"));
            temp_array.add(temp_item);
        }
//        for (int j = 1; j <= temp_array.size();j++){
//            if (j  % AD_DISPLAY_FREQUENCY == 0){
//                Item_images ads_item = new Item_images();
//                ads_item.setImage("ads");
//                temp_array.add(j,ads_item);
//            }
//        }
        return temp_array;
    }


    Callingafterads callingafter;

    public void loadInterstitialAd(Context mContext, final Callingafterads callingafter) {

        this.callingafter = callingafter;

        final ProgressDialog progress = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        final InterstitialAd interstitialAd = new InterstitialAd(mContext, mContext.getResources().getString(R.string.facebook_interstitial_id));
        interstitialAd.loadAd();
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (interstitialAd != null) {
                    interstitialAd.destroy();
                }
                callingafter.onAdsresponce(true);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (interstitialAd != null) {
                    interstitialAd.destroy();
                }
                callingafter.onAdsresponce(true);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });

    }

    private void loadgoogleInterstitialAd(Context mContext, final Callingafterads callingafter) {
        final ProgressDialog progress = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        com.google.android.gms.ads.InterstitialAd mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(mContext.getResources().getString(R.string.interstial_id));
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                progress.dismiss();

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (progress.isShowing()){
                    progress.dismiss();
                }
                callingafter.onAdsresponce(true);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (progress.isShowing()){
                    progress.dismiss();
                }
                callingafter.onAdsresponce(true);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    public interface Callingafterads {
        void onAdsresponce(Boolean showing);
    }

}

