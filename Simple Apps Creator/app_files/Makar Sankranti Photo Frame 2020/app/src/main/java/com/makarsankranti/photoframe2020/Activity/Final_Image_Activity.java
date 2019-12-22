package com.makarsankranti.photoframe2020.Activity;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.makarsankranti.photoframe2020.ConnectionDetector;
import com.makarsankranti.photoframe2020.Constant;
import com.makarsankranti.photoframe2020.R;
import com.makarsankranti.photoframe2020.Utility;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

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

    StartAppAd startAppAd = new StartAppAd(Final_Image_Activity.this);


    @Override
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        startAppAd.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState){
        startAppAd.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

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
                openingadsoncall();
            }
        });

        share_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actiontype = actionshare;
                openingadsoncall();
            }
        });

    }

    public void openingadsoncall(){
        final ProgressDialog progress = new ProgressDialog(Final_Image_Activity.this, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC,new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                if (progress.isShowing()){
                    progress.dismiss();
                }
                startAppAd.showAd(new AdDisplayListener() {
                    @Override
                    public void adHidden(Ad ad) {
                        callingactiontype();
                    }
                    @Override
                    public void adDisplayed(Ad ad) { }
                    @Override
                    public void adClicked(Ad ad) { }
                    @Override
                    public void adNotDisplayed(Ad ad) {
                        callingactiontype();
                    }
                });
            }
            @Override
            public void onFailedToReceiveAd(Ad ad) {
                if (progress.isShowing()){
                    progress.dismiss();
                }
                callingactiontype();
            }
        });
    }

    public void callingactiontype(){
        if (Actiontype.equals(actiondownload)) {
            constantfile.download_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
        } else if (Actiontype.equals(actionshare)) {
            constantfile.share_image(Final_Image_Activity.this, Constant.getfinalimage, relaivelayout);
        }
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
