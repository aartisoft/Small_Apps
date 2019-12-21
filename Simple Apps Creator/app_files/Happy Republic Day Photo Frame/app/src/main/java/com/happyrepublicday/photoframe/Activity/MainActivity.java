package com.happyrepublicday.photoframe.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happyrepublicday.photoframe.Adapter.FrameItemAdapter;
import com.happyrepublicday.photoframe.ConnectionDetector;
import com.happyrepublicday.photoframe.Constant;
import com.happyrepublicday.photoframe.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.happyrepublicday.photoframe.Constant.arrayofimages;
import static com.happyrepublicday.photoframe.Constant.arrayofshowimages;

public class MainActivity extends AppCompatActivity implements FrameItemAdapter.MyClickListener {
    private ConnectionDetector detectorconn;
    Boolean conn;
    Constant constantfile;
    RelativeLayout content_main;
    RecyclerView Image_list;
    private ProgressBar progressBar;
    TextView no_data_text;
    FrameItemAdapter mAdapter;
    private AlertDialog alert;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar action = getSupportActionBar();
        action.setTitle(getResources().getString(R.string.app_name));

        this.conn = null;
        constantfile = new Constant();
        content_main = (RelativeLayout) findViewById(R.id.content_main);

        this.detectorconn = new ConnectionDetector(getApplicationContext());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        no_data_text = (TextView) findViewById(R.id.no_data_text);
        no_data_text.setVisibility(View.GONE);


        Image_list = (RecyclerView) findViewById(R.id.Image_list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        Image_list.setLayoutManager(mLayoutManager);
        Image_list.setItemAnimator(new DefaultItemAnimator());
        arrayofimages = new ArrayList<String>();
        arrayofshowimages = new ArrayList<String>();


        mAdapter = new FrameItemAdapter(MainActivity.this, arrayofshowimages);
        mAdapter.setClickListener(this);
        Image_list.setAdapter(mAdapter);


        try {
            arrayofimages.addAll(getImage(MainActivity.this));
            arrayofshowimages.addAll(getShowImage(MainActivity.this));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mAdapter.notifyDataSetChanged();


    }

    private List<String> getImage(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] files = assetManager.list("image");
        List<String> it = Arrays.asList(files);
        return it;
    }

    private List<String> getShowImage(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] files = assetManager.list("showimage");
        List<String> it = Arrays.asList(files);
        return it;
    }


    @Override
    public void onBackPressed() {
        AppExit();
    }


    public void AppExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.exit_main_title));
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alert.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        System.exit(0);

                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert = alertDialogBuilder.create();
        alert.show();

    }

    @Override
    public void onItemClick(int position, View v) {
        loadInterstitialAd(position);
    }


    private void loadInterstitialAd(final int pos) {
        final ProgressDialog progress = new ProgressDialog(MainActivity.this, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        mInterstitialAd = new InterstitialAd(MainActivity.this);
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
                Constant.selectedframe = pos;
                Intent selectimage = new Intent(MainActivity.this, Select_Image_Activity.class);
                startActivity(selectimage);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (progress.isShowing()){
                    progress.dismiss();
                }
                Constant.selectedframe = pos;
                Intent selectimage = new Intent(MainActivity.this, Select_Image_Activity.class);
                startActivity(selectimage);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
}