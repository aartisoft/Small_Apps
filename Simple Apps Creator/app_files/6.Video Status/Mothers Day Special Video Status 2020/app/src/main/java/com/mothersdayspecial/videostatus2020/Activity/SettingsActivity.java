package com.mothersdayspecial.videostatus2020.Activity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.mothersdayspecial.videostatus2020.Constant;
import com.mothersdayspecial.videostatus2020.PrefManager;
import com.mothersdayspecial.videostatus2020.R;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import static com.mothersdayspecial.videostatus2020.Constant.SHARED_AUTOPLAY_SET;
import static com.mothersdayspecial.videostatus2020.Constant.SHARED_NOTIFICATION_SET;


public class SettingsActivity extends AppCompatActivity {

    private LinearLayout linear_layout_clea_cache;
    private TextView text_view_cache_value;
    private PrefManager prf;
    private Switch switch_button_notification;
    private Switch switch_button_autoplay;
    private TextView text_view_version;
    private LinearLayout linearLayout_policy_privacy;
    private LinearLayout linearLayout_contact_us;
    private LinearLayout linear_layout_hash;
    Constant constantfile;
    RelativeLayout autoplay_rl,notification_rl;
    RelativeLayout relative_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        constantfile = new Constant();
        prf= new PrefManager(getApplicationContext());

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_settings));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        setValues();
        initAction();
        initializeCache();
    }

    private void initAction() {
        this.linear_layout_clea_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCache(getApplicationContext());
                initializeCache();
            }
        });
        this.switch_button_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b==true){
                    prf.setBoolean(SHARED_NOTIFICATION_SET,true);
                }else{
                    prf.setBoolean(SHARED_NOTIFICATION_SET,false);
                }
            }
        });
        this.switch_button_autoplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b==true){
                    prf.setBoolean(SHARED_AUTOPLAY_SET,true);
                }else{
                    prf.setBoolean(SHARED_AUTOPLAY_SET,false);
                }
            }
        });

        this.linearLayout_policy_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getopenPrivacypolicy();

            }
        });

        this.linearLayout_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    send_feedback();

            }
        });
        this.linear_layout_hash.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                printHashKey();
                return false;
            }
        });

        this.notification_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!prf.getBooleannotification(SHARED_NOTIFICATION_SET)){
                    prf.setBoolean(SHARED_NOTIFICATION_SET,true);
                    switch_button_notification.setChecked(true);
                }else{
                    prf.setBoolean(SHARED_NOTIFICATION_SET,false);
                    switch_button_notification.setChecked(false);
                }
            }
        });
        this.autoplay_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!prf.getBoolean(SHARED_AUTOPLAY_SET)){
                    prf.setBoolean(SHARED_AUTOPLAY_SET,true);
                    switch_button_autoplay.setChecked(true);
                }else{
                    prf.setBoolean(SHARED_AUTOPLAY_SET,false);
                    switch_button_autoplay.setChecked(false);
                }
            }
        });
    }

    public void getopenPrivacypolicy() {
        String url = "https://simpleappscreatorpolicy.blogspot.com/2019/06/status-and-images-app-policy.html";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void send_feedback() {

        constantfile.snackbarcommonrelative(SettingsActivity.this, relative_setting, "Processing");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"tinyappscollection@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        i.putExtra(Intent.EXTRA_TEXT   , "");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            constantfile.snackbarcommonrelative(SettingsActivity.this, relative_setting, "There are no email clients installed.");
        }



    }

    public  void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("HASKEY", "printHashKey() Hash Key: " + hashKey);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("haskey", hashKey);
                clipboard.setPrimaryClip(clip);

            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("HASKEY", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("HASKEY", "printHashKey()", e);
        }
    }
    private void initView() {
        this.relative_setting= (RelativeLayout) findViewById(R.id.relative_setting);
        this.notification_rl= (RelativeLayout) findViewById(R.id.notification_rl);
        this.autoplay_rl= (RelativeLayout) findViewById(R.id.autoplay_rl);
        this.linear_layout_clea_cache= (LinearLayout) findViewById(R.id.linear_layout_clea_cache);
        this.text_view_cache_value=(TextView) findViewById(R.id.text_view_cache_value);
        this.switch_button_notification=(Switch) findViewById(R.id.switch_button_notification);
        this.switch_button_autoplay=(Switch) findViewById(R.id.switch_button_autoplay);
        this.text_view_version=(TextView) findViewById(R.id.text_view_version);
        this.linearLayout_policy_privacy=(LinearLayout) findViewById(R.id.linearLayout_policy_privacy);
        this.linearLayout_contact_us=(LinearLayout) findViewById(R.id.linearLayout_contact_us);
        this.linear_layout_hash=(LinearLayout) findViewById(R.id.linear_layout_hash);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    private void initializeCache() {
        long size = 0;
        try {
            size += getDirSize(this.getCacheDir());
            size += getDirSize(this.getExternalCacheDir());
        }catch (Exception  e){

        }
        this.text_view_cache_value.setText(readableFileSize(size));

    }

    public long getDirSize(File dir){
        long size = 0;

        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }



    private void setValues() {
        if (!prf.getBooleannotification(SHARED_NOTIFICATION_SET)){
            this.switch_button_notification.setChecked(false);
        }else{
            this.switch_button_notification.setChecked(true);
        }
        if (prf.getBoolean(SHARED_AUTOPLAY_SET)){
            this.switch_button_autoplay.setChecked(true);
        }else{
            this.switch_button_autoplay.setChecked(false);
        }
        try {
            PackageInfo pInfo =getPackageManager().getPackageInfo(getPackageName(),0);
            String version = pInfo.versionName;
            text_view_version.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
