package com.rog3.gaming.wallpaper.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
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
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rog3.gaming.wallpaper.Adapter.ImageAdapter;
import com.rog3.gaming.wallpaper.ConnectionDetector;
import com.rog3.gaming.wallpaper.Constant;
import com.rog3.gaming.wallpaper.EndlessRecyclerOnScrollListener;
import com.rog3.gaming.wallpaper.R;
import com.rog3.gaming.wallpaper.gettersetter.ItemData;
import com.rog3.gaming.wallpaper.gettersetter.ItemUpdate;
import com.rog3.gaming.wallpaper.gettersetter.Item_collections;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

import static com.crashlytics.android.Crashlytics.log;
import static com.rog3.gaming.wallpaper.Constant.passing_object;

public class MainActivity extends AppCompatActivity implements ImageAdapter.MyClickListener {
    TextView tool_title;
    Toolbar toolbar;
    Constant constantfile;
    String title_text;
    DrawerLayout drawer_layout;
    private ConnectionDetector detectorconn;
    Boolean conn;
    private ProgressBar progressBar;
    TextView no_data_text;
    RecyclerView Image_list;
    ImageAdapter mAdapter;
    private AlertDialog alert;

    private int currentpage = 0;
    private boolean mIsLoadingMore;
    private static final int TOTAL_ITEM_EACH_LOAD = 15;
    String oldestKeyYouveSeen = null;

    SharedPreferences pref;

    private FirebaseAnalytics mFirebaseAnalytics;
    RelativeLayout content_main;

    public static String PACKAGE_NAME;
    AppUpdateManager appUpdateManager;
    private final static int MY_REQUEST_CODE = 111;
    com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title_text = getResources().getString(R.string.app_name);
        tool_title = (TextView) toolbar.findViewById(R.id.tool_title);
        drawer_layout = (DrawerLayout) toolbar.findViewById(R.id.drawer_layout);
        content_main = (RelativeLayout) findViewById(R.id.content_main);
        AudienceNetworkAds.initialize(MainActivity.this);
        this.conn = null;
        constantfile = new Constant();
        PACKAGE_NAME = getApplicationContext().getPackageName();
        this.detectorconn = new ConnectionDetector(getApplicationContext());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        no_data_text = (TextView) findViewById(R.id.no_data_text);
        no_data_text.setVisibility(View.GONE);

        Image_list = (RecyclerView) findViewById(R.id.Image_list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this,3);
        Image_list.setLayoutManager(mLayoutManager);
        Image_list.setItemAnimator(new DefaultItemAnimator());
        Image_list.setHasFixedSize(true);
        Image_list.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (mIsLoadingMore) {
                    currentpage = current_page;
                    Loadcatwisedata(current_page);
                }
            }
        });
        mAdapter = new ImageAdapter(MainActivity.this);
        mAdapter.setClickListener(this);
        Image_list.setAdapter(mAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mAdapter.getItemViewType(position)){
                    case ImageAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case ImageAdapter.VIEW_TYPE_LOADING:
                        return 3;
                    default:
                        return -1;
                }
            }
        });

        currentpage = 1;
        Loadcatwisedata(currentpage);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        pref = getApplicationContext().getSharedPreferences(Constant.SHARED_PREF, 0);

        if (pref.getString("regId", null) != null  && !pref.getString("regId",null).equals("")) {
            String regId = pref.getString("regId", null);
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
                            Log.e("gettingfirebasetoken", token + "      ::::");
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

    @Override
    protected void onResume() {
        super.onResume();

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


    public void Loadcatwisedata(int page) {
        if (page == 1) {
            mAdapter.clearalldata();
        }
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            getImageData(page);
        } else {
            progressBar.setVisibility(View.GONE);
            if (currentpage == 1) {
                mIsLoadingMore = false;
                Image_list.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);
            }
            snackbarcommonrelativeLong(MainActivity.this, content_main, "No Internet Connection!");
        }
    }


    public void getImageData(int pageget) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("page", pageget);
        client.get(Constant.GET_IMAGE_LISTING, params, new AsynchronouseData(pageget));
    }

    class AsynchronouseData extends JsonHttpResponseHandler {

        int getpagenumber;

        AsynchronouseData(int pagenumber) {
            this.getpagenumber = pagenumber;
        }

        public void onStart() {
            super.onStart();
            if (getpagenumber == 1){
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {

            try {
                JSONArray getdata = bytes.getJSONArray("data");
                String string = new Gson().toJson(getdata);
                ItemData statusData = new ItemData();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                statusData.setLimit(bytes.getString("limit"));
                statusData.setData(constantfile.ConvertJSONtoModel(getdata));

                if (statusData.isStatus()) {

                    mIsLoadingMore = true;
                    if (statusData.getData().size() < Integer.parseInt(statusData.getLimit())) {
                        mIsLoadingMore = false;
                    }
                    Image_list.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    mAdapter.adddata(statusData.getData(), getpagenumber);

                } else {
                    progressBar.setVisibility(View.GONE);
                    if (getpagenumber == 1) {
                        mIsLoadingMore = false;
                        Image_list.setVisibility(View.GONE);
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                }
                progressBar.setVisibility(View.GONE);

            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                if (getpagenumber == 1) {
                    Image_list.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);
                }
                mIsLoadingMore = false;
            }

            if (!mIsLoadingMore) {
                mAdapter.setnomoredata();
            }
        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {
            if (!mIsLoadingMore) {
                mAdapter.setnomoredata();
            }
            progressBar.setVisibility(View.GONE);
            if (getpagenumber == 1) {
                Image_list.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);
            }

        }
    }




    public void snackbarcommonrelativeLong(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loadcatwisedata(currentpage);
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

    public void getShareCounter() {
        constantfile.snackbarcommondrawerLayout(MainActivity.this, drawer_layout, "Processing");
        String whatsAppMessage = getResources().getString(R.string.app_share_status)+"\n\n";
        whatsAppMessage = whatsAppMessage + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "\n";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }


    public void AppExit() {
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(MainActivity.this);
        errorDialog.setTitle("Are you sure....?");
        errorDialog.setMessage("Your feedback is very important to us and it will huge impact. Please Give 5 STAR Rate.");
        errorDialog.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        errorDialog.setNegativeButton("Dismiss", null);
        errorDialog.setNeutralButton("RATE US", new DialogInterface.OnClickListener() {
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
        AlertDialog errorAlert = errorDialog.create();
        errorAlert.show();
        errorAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        errorAlert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        errorAlert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
    }


    @Override
    public void onItemClick(int position, String ImgUrl, ArrayList<Item_collections> passarray, View v) {
        passing_object = new Item_collections();
        passing_object = passarray.get(position);
        loadInterstitialAd(ImgUrl);
    }


    public void CallAddToken(String PassToken) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("token", PassToken);
        params.put("device", PACKAGE_NAME);
        client.get(Constant.GET_ADD_TOKEN, params, new AsynchronouseTokenData(PassToken));
    }


    class AsynchronouseTokenData extends JsonHttpResponseHandler {

        String getToken;

        AsynchronouseTokenData(String Token) {
            getToken = Token;
        }

        public void onStart() {
            super.onStart();
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {
            Log.e("getdatacall","::::    "+ bytes.toString());
            try {
                ItemUpdate statusData = new ItemUpdate();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                if (statusData.isStatus()) {
                    pref.edit().putString("regId", getToken).commit();
                } else {

                }
            } catch (Exception e) {
            }
        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {

        }
    }








    private void loadInterstitialAd(final String imgURL) {
        final ProgressDialog progress = new ProgressDialog(MainActivity.this, R.style.MyAlertDialogStyle);
        progress.setMessage("Loading Ad");
        progress.setCancelable(false);
        progress.show();
        final InterstitialAd interstitialAd = new InterstitialAd(MainActivity.this, getResources().getString(R.string.facebook_interstitial_id));
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
                callnextscreen(imgURL);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (interstitialAd != null) {
                    interstitialAd.destroy();
                }
                callnextscreen(imgURL);
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


    public void callnextscreen(String imgURL){
        Intent catwise = new Intent(MainActivity.this, Home_SingleItem_Activity.class);
        catwise.putExtra("image_url", imgURL + "");
        startActivity(catwise);
    }
}