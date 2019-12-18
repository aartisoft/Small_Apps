package com.karelakefayde.hindi.Activity;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import androidx.fragment.app.FragmentTransaction;

import com.karelakefayde.hindi.Fragment.FavouriteFragment;
import com.karelakefayde.hindi.Fragment.HomeFragment;
import com.karelakefayde.hindi.Fragment.InsideListFragment;
import com.karelakefayde.hindi.Fragment.PrivacyPolicyFragment;

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
import com.karelakefayde.hindi.ConnectionDetector;
import com.karelakefayde.hindi.Constant;
import com.karelakefayde.hindi.DbAdapter;
import com.karelakefayde.hindi.PrefManager;
import com.karelakefayde.hindi.R;
import com.karelakefayde.hindi.gettersetter.ItemUpdate;

import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

import static com.crashlytics.android.Crashlytics.log;
import static com.karelakefayde.hindi.Constant.SHARED_Token;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //TextView tool_title;
    ImageView drawer_back;
    Toolbar toolbar;
    private ConnectionDetector detectorconn;
    Boolean conn;
    private int fragmentposition = 0;
    Constant constantfile;
    String title_text = "";
    DrawerLayout drawer_layout;
    private AlertDialog alert;
    NavigationView navigationView;
    private FirebaseAnalytics mFirebaseAnalytics;
    DbAdapter db;

    PrefManager pref;
    AppUpdateManager appUpdateManager;
    private final static int MY_REQUEST_CODE = 111;
    com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //tool_title = (TextView) toolbar.findViewById(R.id.tool_title);

        title_text = getResources().getString(R.string.app_name);
        toolbar.setTitle(title_text);
        db = new DbAdapter(this);
        db.open();

        this.conn = null;
        constantfile = new Constant();
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer_back = (ImageView) navigationView.findViewById(R.id.drawer_back);

        this.detectorconn = new ConnectionDetector(getApplicationContext());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu, getApplicationContext().getTheme()));
        } else {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
        }

        navigationView.setNavigationItemSelectedListener(this);


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

        SelectItem(title_text, fragmentposition);

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

    public void updatestart(AppUpdateInfo appUpdateInfo) {
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentposition != 0){
                SelectItem(getResources().getString(R.string.app_name),0);
            }else{
                AppExit();
            }

        }
    }

    public void setfragmentpos(){
        fragmentposition = 0;
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
        } else if (id == R.id.nav_favourite) {
            changefragment = true;
            fragmentposition = 1;
            title_text = getResources().getString(R.string.favoritlist_title);
        } else if (id == R.id.nav_rate_us) {
            getRateAppCounter();
        } else if (id == R.id.nav_share_app) {
            getShareCounter();
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
            toolbar.setTitle(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT)+"");
        } else {
            toolbar.setTitle(Html.fromHtml(title)+"");
        }
        Fragment fragment = null;
        if (fragmentpos < 0) {
            return;
        } else {
            if (fragmentpos == 0) {
                fragment = new HomeFragment();
            } else if (fragmentpos == 1) {
                fragment = new FavouriteFragment();
            } else if (fragmentpos == 2) {
                fragment = new PrivacyPolicyFragment();
            } else if (fragmentpos == 3) {
                fragment = new InsideListFragment();
            }
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentposition == 3){
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.add(R.id.content_main, fragment);
                ft.addToBackStack(Constant.Passing_From);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }else{
                fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
            }
            if (fragmentposition == 0) {
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }

    public void changetitle(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            toolbar.setTitle(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT));
        } else {
            toolbar.setTitle(Html.fromHtml(title));
        }
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
        String url = "https://techcorpsolutions.blogspot.com/2018/03/privacy-policy.html";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void getShareCounter() {
        constantfile.snackbarcommonview(MainActivity.this, drawer_layout, "Processing");
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
        exitDialog.setTitle("Are you sure....?");
        exitDialog.setMessage("If you like this app please give us positive feedback.");
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
        errorAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        errorAlert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        errorAlert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
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
