package com.happyrepublicday.photoframe.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.happyrepublicday.photoframe.ConnectionDetector;
import com.happyrepublicday.photoframe.Constant;
import com.happyrepublicday.photoframe.R;
import com.happyrepublicday.photoframe.Utility;

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
    String Actiontype = "";
    private static final String actiondownload = "download";
    private static final String actionshare = "share";
    InterstitialAd mInterstitialAd;


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
                loadInterstitialAd();
            }
        });

        share_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actiontype = actionshare;
                loadInterstitialAd();
            }
        });

    }

    private void loadInterstitialAd() {
        final ProgressDialog progress = new ProgressDialog(Final_Image_Activity.this, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        mInterstitialAd = new InterstitialAd(Final_Image_Activity.this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstial_id));
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                progress.dismiss();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (progress.isShowing()){
                    progress.dismiss();
                }
                if (Actiontype.equals(actiondownload)) {
                    constantfile.download_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
                } else if (Actiontype.equals(actionshare)) {
                    constantfile.share_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (progress.isShowing()){
                    progress.dismiss();
                }
                if (Actiontype.equals(actiondownload)) {
                    constantfile.download_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
                } else if (Actiontype.equals(actionshare)) {
                    constantfile.share_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
                }
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Constant.getfinalimage.recycle();
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


}
