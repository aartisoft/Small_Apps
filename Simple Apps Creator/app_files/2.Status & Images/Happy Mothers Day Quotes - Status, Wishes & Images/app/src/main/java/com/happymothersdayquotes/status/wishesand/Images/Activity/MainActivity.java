package com.happymothersdayquotes.status.wishesand.Images.Activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.crashlytics.android.Crashlytics;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.happymothersdayquotes.status.wishesand.Images.Adapter.PagerAdapter;
import com.happymothersdayquotes.status.wishesand.Images.ConnectionDetector;
import com.happymothersdayquotes.status.wishesand.Images.Constant;
import com.happymothersdayquotes.status.wishesand.Images.PrefManager;
import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.ItemUpdate;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;
import java.util.Objects;

import com.happymothersdayquotes.status.wishesand.Images.AwesomeTabBar;

import static com.crashlytics.android.Crashlytics.log;
import static com.happymothersdayquotes.status.wishesand.Images.Constant.SHARED_Token;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    AlertDialog.Builder builder;
    ViewPager pager;
    AwesomeTabBar tabBar;
    PrefManager pref;
    private FirebaseAnalytics mFirebaseAnalytics;

    AppUpdateManager appUpdateManager;
    private final static int MY_REQUEST_CODE = 111;
    com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        tabBar = (AwesomeTabBar) findViewById(R.id.tabBar);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabBar.setupWithViewPager(pager);

        if (!AudienceNetworkAds.isInAdsProcess(MainActivity.this)){
            AudienceNetworkAds.initialize(MainActivity.this);
        }

        pref = new PrefManager(getApplicationContext());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (pref.getString(SHARED_Token) != null && !pref.getString(SHARED_Token).equals("")) {
            String regId = pref.getString(SHARED_Token);
            CallAddToken(regId);
        } else {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String token = task.getResult().getToken();
                            CallAddToken(token);

                        }
                    });
            BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @SuppressLint("NewApi")
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (Objects.equals(intent.getAction(), Constant.REGISTRATION_COMPLETE)) {
                        FirebaseMessaging.getInstance().subscribeToTopic(Constant.TOPIC_GLOBAL);
                    } else if (Objects.equals(intent.getAction(), Constant.PUSH_NOTIFICATION)) {
                        String message = intent.getStringExtra("message");
                    }
                }
            };
        }

        if (Constant.Passing_from_notification){
            Constant.Passing_from_notification = false;
            if (Constant.Passing_from_notifi_type.equals("video")){
                if (pager != null){
                    pager.setCurrentItem(2);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, VideoplayerActivity.class);
                        startActivity(intent);
                    }
                }, 100);
            }else if (Constant.Passing_from_notifi_type.equals("image")){
                Constant.Passing_from_notification = false;
                if (pager != null){
                    pager.setCurrentItem(1);
                }
            }
        }



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

    @Override
    protected void onResume() {
        super.onResume();
        Boolean conn = null;
        ConnectionDetector detectorconn = new ConnectionDetector(MainActivity.this);
        conn = Boolean.valueOf(detectorconn.isConnectingToInternet());
        if (!conn.booleanValue()) {
            builder = new AlertDialog.Builder(this);

            //Setting message manually and performing action on button click
            builder.setMessage("Please enable internet connection and try again.")
                    .setCancelable(false)
                    .setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    })
                    .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            finish();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Oops!! There is no internet.");
            alert.show();
        }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    @Override
    public void onBackPressed() {
        AppExit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_rateus) {
            getRateAppCounter();
            return true;
        }else if (id == R.id.action_privacy) {
            getopenPrivacypolicy();
            return true;
        }else if (id == R.id.action_exit) {
            AppExit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getRateAppCounter() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void getopenPrivacypolicy() {
        String url = "https://simpleappscreatorpolicy.blogspot.com/2019/06/status-and-images-app-policy.html";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    public void AppExit() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(MainActivity.this);
        exitDialog.setTitle("Are you sure....?");
        exitDialog.setMessage("Your feedback is very important to us and it will huge impact.");
        exitDialog.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        exitDialog.setNegativeButton("Dismiss", null);
        exitDialog.setNeutralButton("RATE US", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
        AlertDialog errorAlert = exitDialog.create();
        errorAlert.show();
        errorAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        errorAlert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        errorAlert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
    }

    public void CallAddToken(String PassToken) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        params.put("token", PassToken);
        params.put("cat_id", Constant.APP_ID);
        client.get(Constant.GET_ADD_TOKEN, params, new AsynchronouseData(PassToken));
    }


    class AsynchronouseData extends JsonHttpResponseHandler {

        String getToken;

        AsynchronouseData(String Token) {
            getToken = Token;
        }

        public void onStart() {
            super.onStart();
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {
            try {
                ItemUpdate statusData = new ItemUpdate();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                Log.e("gettingToken", bytes.getBoolean("status")+"   ,token::    ");
                if (statusData.isStatus()) {
                    pref.setString(SHARED_Token,getToken);
                } else {

                }
            } catch (Exception e) {
            }
        }

    }
}
