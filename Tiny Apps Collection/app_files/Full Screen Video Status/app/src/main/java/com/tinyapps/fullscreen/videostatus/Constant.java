package com.tinyapps.fullscreen.videostatus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.tinyapps.fullscreen.videostatus.gettersetter.HomeCatData;
import com.tinyapps.fullscreen.videostatus.gettersetter.Item_category;
import com.tinyapps.fullscreen.videostatus.gettersetter.Item_collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Constant {

    public static final String LANGUAGE_ID = "8";



    public static final String BASIC_URL = "http://simpleappscreator.com/video_status/api/";



    public static final String GET_ADD_TOKEN = BASIC_URL+ "api_add_token_common.php";
    public static final String GET_ADD_VIEW = BASIC_URL+ "api_add_view.php";
    public static final String GET_CATEGORY_LISTING = BASIC_URL+ "api_cat_list.php";
    public static final String GET_COMMON_LISTING = BASIC_URL+ "api_common_listing.php";
    public static final String GET_foreceupdate = BASIC_URL+ "api_force_update.php";
    public static final String GET_HOME_LISTING = BASIC_URL+ "api_home.php";
    public static final String GET_SEARCH_LISTING = BASIC_URL+ "api_search.php";
    public static final String GET_SUGGETION_LISTING = BASIC_URL+ "api_suggestion.php";
    public static final String GET_VIDEO_REQUEST = BASIC_URL+ "api_add_request.php";





    public static final String SHARED_PREF = "ah_firebase_FullScreenVS";
    public static final String SHARED_Token = "regId";
    public static final String SHARED_NOTIFICATION_SET = "notification_set";
    public static final String SHARED_AUTOPLAY_SET = "autoplay_set";
    public static final String TOPIC_GLOBAL = "global";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";


    private Context context;
    public static int Adscount = 2;
    public static int Adscountlisting = 2;


    public static String Passing_type = "Landscape";
    public static String Passing_cat_id = "";
    public static String Passing_cat_name = "";
    public static String Passing_From_Previous = "MainFragment";

    public static int Passing_selected_fragment = 0;

    public static String Passing_From_title = "";
    public static String Passing_From_text = "";


    public static Item_collections Passing_item_object = new Item_collections();
    public static Boolean Passing_from_notification = false;

    private String downloadUrl = "", downloadFileName = "";
    String action = "share";
    LinearLayout relativelayout;
   // RewardedVideoAd rewardedVideoAd = null;


    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    public void snackbarcommonrelative(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }
    public void snackbarcommonview(Context mcontext, View coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public void snackbarcommonlinear(Context mcontext, LinearLayout coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public void snackbarcommoncoordinatorLayout(Context mcontext, CoordinatorLayout coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public void snackbarcommondrawerLayout(Context mcontext, DrawerLayout coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }



    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight()+150,0);
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight()+150);
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 200);

    }

    public void slideUpforTop(View view){
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0,-view.getHeight()-25);
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
        //final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 200);
    }

    public void slideDownforTop(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0,0,-view.getHeight()-25,0);
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }



    private void scanFile(Context context,String path) {

        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("Tag", "Scan finished. You can view the image in the gallery now.");
                    }
                });
    }



    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", new File(imageUrl));
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri , "image/*");
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Bitmap bitmap = BitmapFactory.decodeFile((new File(imageUrl)).getAbsolutePath());
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notif = new Notification.Builder(mContext)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .build();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "FullScreenVS_notification", importance);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(1, notif);


          /*  String groupId = "my_group_01";
            CharSequence groupName = mContext.getString(R.string.group_name);
            mNotificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId, groupName));*/
        }
    }


    public ArrayList<Item_collections> ConvertJSONtoModel(JSONArray getarray) throws JSONException {
        ArrayList<Item_collections> temp_array = new ArrayList<>();

        for (int i = 0; i< getarray.length();i++){
            JSONObject pass = getarray.getJSONObject(i);
            Item_collections temp_item = new Item_collections();

            temp_item.setId(pass.getString("id"));
            temp_item.setVideo_name(pass.getString("video_name"));
            temp_item.setVideo_url(pass.getString("video_url"));
            temp_item.setVideo_image_thumb(pass.getString("video_image_thumb"));
            temp_item.setTotal_views(pass.getString("total_views"));
            temp_item.setIs_type(pass.getString("is_layout"));
            temp_item.setDate(pass.getString("date"));
            temp_item.setCat_id(pass.getString("cat_id"));
            temp_item.setCategory_name(pass.getString("category_name"));
            temp_array.add(temp_item);
        }
        return temp_array;
    }


    public ArrayList<HomeCatData> ConvertCategoryWithData(JSONArray getarray) throws JSONException {
        ArrayList<HomeCatData> temp_array = new ArrayList<>();

        for (int i = 0; i< getarray.length();i++){
            JSONObject pass = getarray.getJSONObject(i);
            JSONArray catdata = pass.getJSONArray("data");
            HomeCatData temp_item = new HomeCatData();
                temp_item.setCid(pass.getString("cid"));
                temp_item.setCategory_name(pass.getString("category_name"));
                temp_item.setCategory_image_thumb(pass.getString("category_image_thumb"));
                temp_item.setLanguage(pass.getString("language"));
                temp_item.setType_layout(pass.getString("type_layout"));
                temp_item.setData(ConvertJSONtoModel(catdata));
            temp_array.add(temp_item);
        }

        return temp_array;
    }

    public ArrayList<Item_category> ConvertJSONCategory(JSONArray getarray) throws JSONException {
        ArrayList<Item_category> temp_array = new ArrayList<>();

        for (int i = 0; i< getarray.length();i++){
            JSONObject pass = getarray.getJSONObject(i);
            Item_category temp_item = new Item_category();
            temp_item.setCid(pass.getString("cid"));
            temp_item.setCategory_name(pass.getString("category_name"));
            temp_item.setCategory_image_thumb(pass.getString("category_image_thumb"));
            temp_item.setLanguage(pass.getString("language"));
            temp_item.setType_layout(pass.getString("type_layout"));
            temp_array.add(temp_item);
        }

        return temp_array;
    }

    Callingafterads callingafter;

    public void loadInterstitialAd(Context mContext, final Callingafterads callingafter) {

        this.callingafter = callingafter;

        final ProgressDialog progress = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        final InterstitialAd interstitialAd = new InterstitialAd(mContext);
        interstitialAd.setAdUnitId(mContext.getResources().getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                callingafter.onAdsresponce(true);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                callingafter.onAdsresponce(true);
            }
        });
//        interstitialAd.setAdListener(new InterstitialAdListener() {
//            @Override
//            public void onInterstitialDisplayed(Ad ad) {
//            }
//
//            @Override
//            public void onInterstitialDismissed(Ad ad) {
//                if (progress.isShowing()) {
//                    progress.dismiss();
//                }
//                if (interstitialAd != null) {
//                    interstitialAd.destroy();
//                }
//                callingafter.onAdsresponce(true);
//            }
//
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                if (progress.isShowing()) {
//                    progress.dismiss();
//                }
//                if (interstitialAd != null) {
//                    interstitialAd.destroy();
//                }
//                callingafter.onAdsresponce(true);
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                if (progress.isShowing()) {
//                    progress.dismiss();
//                }
//                if (interstitialAd.isAdLoaded()) {
//                    interstitialAd.show();
//                }
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//            }
//        });

    }

    public interface Callingafterads {
        void onAdsresponce(Boolean showing);
    }

}
