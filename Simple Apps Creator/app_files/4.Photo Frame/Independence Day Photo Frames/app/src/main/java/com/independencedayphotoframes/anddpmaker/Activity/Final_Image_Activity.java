package com.independencedayphotoframes.anddpmaker.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appnext.ads.AdsError;
import com.appnext.ads.fullscreen.FullScreenVideo;
import com.appnext.ads.interstitial.Interstitial;
import com.appnext.core.AppnextAdCreativeType;
import com.appnext.core.callbacks.OnAdClicked;
import com.appnext.core.callbacks.OnAdClosed;
import com.appnext.core.callbacks.OnAdError;
import com.appnext.core.callbacks.OnAdLoaded;
import com.appnext.core.callbacks.OnAdOpened;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.independencedayphotoframes.anddpmaker.ConnectionDetector;
import com.independencedayphotoframes.anddpmaker.Constant;
import com.independencedayphotoframes.anddpmaker.R;
import com.independencedayphotoframes.anddpmaker.Utility;

/**
 * Created by Kakadiyas on 11-07-2017.
 */

public class Final_Image_Activity extends AppCompatActivity {


    private ConnectionDetector detectorconn;
    Boolean conn;
    Constant constantfile;
    RelativeLayout relaivelayout;
    LinearLayout download_ll, share_ll;
    ImageView final_image;
    AlertDialog alertDialog;
   // private RewardedVideoAd mRewardedVideoAd;
    String Actiontype = "";
    private static final String actiondownload = "download";
    private static final String actionshare = "share";
    //InterstitialAd mInterstitialAd;
    FullScreenVideo fullscreen_ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalimage);

        constantfile = new Constant();
        this.conn = null;
        this.detectorconn = new ConnectionDetector(getApplicationContext());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        ActionBar action = getSupportActionBar();
        action.setTitle(getResources().getString(R.string.download_image_title));
        action.setDisplayHomeAsUpEnabled(true);
        action.setHomeButtonEnabled(true);

        relaivelayout = (RelativeLayout) findViewById(R.id.relaivelayout);
        final_image = (ImageView) findViewById(R.id.final_image);
        Bitmap bm = Bitmap.createBitmap(Constant.getfinalimage, 0, 0, (int) Constant.getfinalimage.getWidth(), (int) Constant.getfinalimage.getHeight());
        final_image.setImageBitmap(bm);
        download_ll = (LinearLayout) findViewById(R.id.download_ll);
        share_ll = (LinearLayout) findViewById(R.id.share_ll);

        download_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actiontype = actiondownload;
                callingadsonclick();
              //  showdownloaddialog();
            }
        });

        share_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadInterstitialAd();
                Actiontype = actionshare;
                callingadsonclick();
               // showdownloaddialog();
            }
        });

        intializeappnextads();
//        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
//        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
//
//            @Override
//            public void onRewarded(RewardItem rewardItem) {
//            }
//
//            @Override
//            public void onRewardedVideoAdLeftApplication() {
//            }
//
//            @Override
//            public void onRewardedVideoAdClosed() {
//                if (Actiontype.equals(actiondownload)) {
//                    constantfile.download_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
//                } else if (Actiontype.equals(actionshare)) {
//                    constantfile.share_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
//                }
//                loadRewardedVideoAd();
//            }
//
//            @Override
//            public void onRewardedVideoAdFailedToLoad(int errorCode) {
//                Toast.makeText(Final_Image_Activity.this, "Please try again!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onRewardedVideoCompleted() {
//                if (Actiontype.equals(actiondownload)) {
//                    constantfile.download_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
//                } else if (Actiontype.equals(actionshare)) {
//                    constantfile.share_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
//                }
//                loadRewardedVideoAd();
//            }
//
//            @Override
//            public void onRewardedVideoAdLoaded() {
//            }
//
//            @Override
//            public void onRewardedVideoAdOpened() {
//            }
//
//            @Override
//            public void onRewardedVideoStarted() {
//            }
//        });
//
//        loadRewardedVideoAd();


    }

//    public void showdownloaddialog() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage(getResources().getString(R.string.see_alert_title));
//        alertDialogBuilder.setPositiveButton("Yes",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        alertDialog.dismiss();
//                        showRewardedVideo();
//                    }
//                });
//
//        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                alertDialog.dismiss();
//            }
//        });
//        alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//
//    }


//    private void loadRewardedVideoAd() {
//        mRewardedVideoAd.loadAd(getString(R.string.rewarded_video), new AdRequest.Builder().build());
//    }


//    private void showRewardedVideo() {
//        if (mRewardedVideoAd.isLoaded()) {
//            mRewardedVideoAd.show();
//        }
//    }


    @Override
    public void onResume() {
     //   mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
      //  mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Constant.getfinalimage.recycle();
     //   mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //code for deny
                }
                break;
        }
    }

    public void intializeappnextads(){
        fullscreen_ad = new FullScreenVideo(this, getResources().getString(R.string.appnext_placement_id));
        fullscreen_ad.setOnAdLoadedCallback(new OnAdLoaded() {
            @Override
            public void adLoaded(String bannerId, AppnextAdCreativeType creativeType) {

            }
        });
        fullscreen_ad.setOnAdOpenedCallback(new OnAdOpened() {
            @Override
            public void adOpened() {

            }
        });
        fullscreen_ad.setOnAdClickedCallback(new OnAdClicked() {
            @Override
            public void adClicked() {

            }
        });
        fullscreen_ad.setOnAdClosedCallback(new OnAdClosed() {
            @Override
            public void onAdClosed() {
                onAdsresponce();
                loadappnextads();
            }
        });
        fullscreen_ad.setOnAdErrorCallback(new OnAdError() {
            @Override
            public void adError(String error) {
                switch (error){
                    case AdsError.NO_ADS:
                        Log.v("appnext", "no ads");
                        break;
                    case AdsError.CONNECTION_ERROR:
                        Log.v("appnext", "connection problem");
                        break;
                    default:
                        Log.v("appnext", "other error");
                }
            }
        });

        loadappnextads();
    }




    public void loadappnextads(){
        if (fullscreen_ad != null){
            fullscreen_ad.loadAd();
        }
    }

    public void callingadsonclick(){
        final ProgressDialog progress = new ProgressDialog(Final_Image_Activity.this, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        if (fullscreen_ad.isAdLoaded()){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    fullscreen_ad.showAd();
                }
            }, 1500);

        }else{
            if (progress.isShowing()){
                progress.dismiss();
            }
            onAdsresponce();
        }
    }


    public void onAdsresponce() {
        if (Actiontype.equals(actiondownload)) {
            constantfile.download_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
        } else if (Actiontype.equals(actionshare)) {
            constantfile.share_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
        }
    }

}
