package com.makarsankranti.videostatus2020.Activity;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.makarsankranti.videostatus2020.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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
import com.makarsankranti.videostatus2020.Constant;
import com.makarsankranti.videostatus2020.Fragment.MainFragment;
import com.makarsankranti.videostatus2020.Fragment.PrivacyPolicyFragment;
import com.makarsankranti.videostatus2020.R;
import com.makarsankranti.videostatus2020.gettersetter.ItemUpdate;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

import static com.crashlytics.android.Crashlytics.log;
import static com.makarsankranti.videostatus2020.Constant.SHARED_Token;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //TextView tool_title;
    ImageView drawer_back;
    Toolbar toolbar;
    private int fragmentposition = 0;
    Constant constantfile;
    String title_text = "";
    DrawerLayout drawer_layout;
    private AlertDialog alert;
    NavigationView navigationView;
    private FirebaseAnalytics mFirebaseAnalytics;
    PrefManager pref;
    AppUpdateManager appUpdateManager;
    private final static int MY_REQUEST_CODE = 111;
    com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;
    StartAppAd startAppAd = new StartAppAd(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "211774923", true);
        StartAppSDK.setUserConsent (this, "pas", System.currentTimeMillis(), false);
        startAppAd.disableSplash();


        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title_text = getResources().getString(R.string.app_name);
        toolbar.setTitle(title_text);

        constantfile = new Constant();
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer_back = (ImageView) navigationView.findViewById(R.id.drawer_back);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu, getApplicationContext().getTheme()));
        } else {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
        }

//        try {
//            Callforeupdate();
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        navigationView.setNavigationItemSelectedListener(this);

        SelectItem(title_text, fragmentposition);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        pref = new PrefManager(getApplicationContext());

        if (pref.getString(SHARED_Token) != null  && !pref.getString(SHARED_Token).equals("")) {
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
    public void onResume() {
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




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AppExit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean changefragment = false;
        if (id == R.id.nav_home) {
            changefragment = true;
            fragmentposition = 0;
            title_text = getResources().getString(R.string.app_name);
        } else if (id == R.id.nav_rate_us) {
            getRateAppCounter();
        } else if (id == R.id.nav_share_app) {
            getShareCounter();
        } else if (id == R.id.nav_setting) {
            getopenSettingPage();
        } else if (id == R.id.nav_policy) {
            getopenPrivacypolicy();
        } else if (id == R.id.nav_exit_app) {
            AppExit();
        }
        if (changefragment) {
            SelectItem(title_text, fragmentposition);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SelectItem(String title, int fragmentpos) {
        fragmentposition = fragmentpos;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            toolbar.setTitle(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT));
        } else {
            toolbar.setTitle(Html.fromHtml(title));
        }
        Fragment fragment = null;
        if (fragmentpos < 0) {
            return;
        } else {
            if (fragmentpos == 0) {
                fragment = new MainFragment();
            } else if (fragmentpos == 2) {
                fragment = new PrivacyPolicyFragment();
            }
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
            if (fragmentposition == 0) {
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }


    public void changetitle(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            toolbar.setTitle(Html.fromHtml(title + "", Html.FROM_HTML_MODE_COMPACT));
        } else {
            toolbar.setTitle(Html.fromHtml(title + ""));
        }
    }

    public void getopenSettingPage() {
        Intent setting = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(setting);
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
        String whatsAppMessage = getResources().getString(R.string.share_message) + "\n\n";
        whatsAppMessage = whatsAppMessage + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "\n";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }


    public void AppExit() {

        AlertDialog.Builder exitDialog = new AlertDialog.Builder(MainActivity.this);
        String titleText = "Are you sure....?";
        String messageText = "Your feedback is very important to us and it will huge impact.";
        exitDialog.setTitle(titleText);
        exitDialog.setMessage(messageText);
        exitDialog.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                System.exit(0);
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
    }



    public void CallAddToken(String PassToken) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        params.put("app_id", Constant.APP_ID);
        params.put("token", PassToken);
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
                if (statusData.isStatus()) {
                    pref.setString(SHARED_Token,getToken);
                }
                Log.e("callingtokenappi","data  ::   "+ statusData.isStatus());
            } catch (Exception e) {
            }

        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {
        }
    }
}
