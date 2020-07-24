package com.simpleapps.note9wallpapers.Activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.simpleapps.note9wallpapers.ConnectionDetector;
import com.simpleapps.note9wallpapers.Constant;
import com.simpleapps.note9wallpapers.Float.FloatingActionButton;
import com.simpleapps.note9wallpapers.R;
import com.simpleapps.note9wallpapers.Utility;
import com.simpleapps.note9wallpapers.gettersetter.ItemUpdate;
import com.simpleapps.note9wallpapers.gettersetter.Item_collections;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.simpleapps.note9wallpapers.Constant.actiondownload;
import static com.simpleapps.note9wallpapers.Constant.actionsetas;

/**
 * Created by Kakadiyas on 11-07-2017.
 */

public class Home_SingleItem_Activity extends AppCompatActivity {


    ImageView image_full, back;
    ProgressBar progressBar;
    private ConnectionDetector detectorconn;
    Boolean conn;
    String image_url;
    RelativeLayout relative;
    Constant constantfile;

    String actiontype = "";
    Item_collections getdata;
    private AdView adViewbanner;

    FloatingActionButton action_download,action_setas;
     Bitmap downloadedBitmap=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.home_single_item_activity_new);

        constantfile = new Constant();

        this.conn = null;
        this.detectorconn = new ConnectionDetector(getApplicationContext());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                image_url = null;
            } else {
                image_url = extras.getString("image_url");
            }
        } else {
            image_url = (String) savedInstanceState.getSerializable("image_url");
        }
        getdata = new Item_collections();
        getdata = Constant.passing_object;


        action_download = (FloatingActionButton) findViewById(R.id.action_download);
        action_setas = (FloatingActionButton) findViewById(R.id.action_setas);
        relative = (RelativeLayout) findViewById(R.id.relative);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        image_full = (ImageView) findViewById(R.id.image_full);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        action_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                actiontype = actiondownload;
                Actionclickworking();
            }
        });

        action_setas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                actiontype = actionsetas;
                Actionclickworking();
            }
        });

        Glide.with(Home_SingleItem_Activity.this)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .load(image_url)
                .error(R.drawable.error)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        // log exception
                        progressBar.setVisibility(View.GONE);
                        return false; // important to return false so the error placeholder can be placed
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        downloadedBitmap=resource;
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                })
                .into(image_full);

        CallAddView(getdata.getId());

        adViewbanner = new AdView(this, getResources().getString(R.string.facebook_banner_id), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.ads);
        adContainer.addView(adViewbanner);
        adViewbanner.loadAd();
        adViewbanner.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("gettingerror"," get error of ads :: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

    }

    public void setdisableclick(){
        action_download.setClickable(false);
        action_setas.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                action_download.setClickable(true);
                action_setas.setClickable(true);
            }
        }, 2000);
    }

    public void Actionclickworking() {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            boolean result = Utility.checkPermission(Home_SingleItem_Activity.this);
            if (result) {
                    constantfile.showporgressdialog(Home_SingleItem_Activity.this,actiontype);
                    if (actiontype.equals(actionsetas)) {
                        constantfile.setAs_bitmapimage(Home_SingleItem_Activity.this, downloadedBitmap, relative);
                        //constantfile.share_image(Home_SingleItem_Activity.this, actionsetas, getdata.getWallpaper_image(), relative);
                    } else if (actiontype.equals(actiondownload)) {
                        CallAddDownload(getdata.getId());
                        //constantfile.share_image(Home_SingleItem_Activity.this, actiondownload, getdata.getWallpaper_image(), relative);
                    }
            } else {
                constantfile.snackbarcommonrelative(Home_SingleItem_Activity.this, relative, "Please give the permission to download Image!");
            }
        } else {
            snackbarcommonrelativeLong(Home_SingleItem_Activity.this, relative, getResources().getString(R.string.no_internet));

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Actionclickworking();
                } else {
                    constantfile.snackbarcommonrelative(Home_SingleItem_Activity.this, relative, "Permission denied to read your External storage!");
                }
                return;
            }
        }
    }


    public void snackbarcommonrelativeLong(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actionclickworking();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        TextView textaction = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
        textView.setTextSize(16);
        textaction.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textaction.setTextColor(Color.BLACK);
        snackbar.show();
    }



    @Override
    public void onBackPressed() {
        this.finish();
    }


    public void CallAddView(String PassID) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("wallpaper_view_id", PassID);
        client.get(Constant.GET_ADD_VIEW, params, new AsynchronouseData());
    }


    class AsynchronouseData extends JsonHttpResponseHandler {

        AsynchronouseData() { }

        public void onStart() {
            super.onStart();
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {
            try {
                ItemUpdate statusData = new ItemUpdate();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                if (statusData.isStatus()) {

                } else {

                }
            } catch (Exception e) {
            }
        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {

        }
    }


    public void CallAddDownload(String PassID) {

        if (downloadedBitmap != null) {
            constantfile.download_bitmapimage(Home_SingleItem_Activity.this, downloadedBitmap, relative);
        } else {
            constantfile.share_image(Home_SingleItem_Activity.this, actiondownload, getdata.getWallpaper_image(), relative);
        }

        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("wallpaper_download_id", PassID);
        client.get(Constant.GET_ADD_DOWNLOAD, params, new AsynchronouseDownloadData());
    }


    class AsynchronouseDownloadData extends JsonHttpResponseHandler {

        AsynchronouseDownloadData() { }

        public void onStart() {
            super.onStart();
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {
            try {

                ItemUpdate statusData = new ItemUpdate();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
            } catch (Exception e) {
            }


        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {

        }
    }

}
