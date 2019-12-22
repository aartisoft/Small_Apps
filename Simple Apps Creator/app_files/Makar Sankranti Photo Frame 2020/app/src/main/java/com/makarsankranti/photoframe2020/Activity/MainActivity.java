package com.makarsankranti.photoframe2020.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.makarsankranti.photoframe2020.Adapter.FrameItemAdapter;
import com.makarsankranti.photoframe2020.ConnectionDetector;
import com.makarsankranti.photoframe2020.Constant;
import com.makarsankranti.photoframe2020.R;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.makarsankranti.photoframe2020.Constant.arrayofimages;
import static com.makarsankranti.photoframe2020.Constant.arrayofshowimages;

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

    AppUpdateManager appUpdateManager;
    private final static int MY_REQUEST_CODE = 111;
    com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;

    StartAppAd startAppAd = new StartAppAd(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, getResources().getString(R.string.startapp_id), true);
        StartAppSDK.setUserConsent (this, "pas", System.currentTimeMillis(), false);
        startAppAd.disableSplash();
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



        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    updatestart(appUpdateInfo);
                }
            }
        });
    }

    public void updatestart(AppUpdateInfo appUpdateInfo){
        InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                    // After the update is downloaded, show a notification
                    // and request user confirmation to restart the app.
                    popupSnackbarForCompleteUpdate();
                }
            }
        };
        appUpdateManager.registerListener(listener);
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    MY_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.content_main), "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        TextView textaction = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textaction.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textaction.setTextColor(Color.BLACK);
        snackbar.show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("Update flow failed" ,""+ resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
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
        final ProgressDialog progress = new ProgressDialog(MainActivity.this, R.style.MyAlertDialogStyle);
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
                        callnewpage(position);
                    }
                    @Override
                    public void adDisplayed(Ad ad) { }
                    @Override
                    public void adClicked(Ad ad) { }
                    @Override
                    public void adNotDisplayed(Ad ad) {
                        callnewpage(position);
                    }
                });
            }
            @Override
            public void onFailedToReceiveAd(Ad ad) {
            }
        });
    }


    private void callnewpage(final int pos) {
        Constant.selectedframe = pos;
        Intent selectimage = new Intent(MainActivity.this, Select_Image_Activity.class);
        startActivity(selectimage);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startAppAd.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                }
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    updatestart(appUpdateInfo);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }
}