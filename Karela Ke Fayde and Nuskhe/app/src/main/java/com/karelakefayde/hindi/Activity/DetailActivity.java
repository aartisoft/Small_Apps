package com.karelakefayde.hindi.Activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.karelakefayde.hindi.ConnectionDetector;
import com.karelakefayde.hindi.Constant;
import com.karelakefayde.hindi.DbAdapter;
import com.karelakefayde.hindi.PrefManager;
import com.karelakefayde.hindi.R;
import com.karelakefayde.hindi.gettersetter.ItemUpdate;
import com.karelakefayde.hindi.gettersetter.Item_images;
import com.karelakefayde.hindi.widgets.CircleImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.karelakefayde.hindi.Constant.show_app_icon;


public class DetailActivity extends AppCompatActivity {

    Item_images Itemdata = new Item_images();
    private ConnectionDetector detectorconn;
    Boolean conn;
    Constant constantfile;
    String ActionTypepass = "";
    LinearLayout detail_rl;
    private Toolbar mTopToolbar;
    RelativeLayout fulllayout;
    TextView no_data_text;

    private PrefManager prf;
    CircleImageView detail_image;
    TextView details_text;
    FloatingActionButton fab_plus, fab_minus, fab_whatsappshare;

    private Menu menu;
    DbAdapter db;

    private static final String WHATSAPP_ID = "com.whatsapp";
    private static final String SHARE_ID = "com.android.all";
    private static final String COPY = "clipboard";
    int deftextsize = 50;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DbAdapter(DetailActivity.this);
        db.open();
        Itemdata = Constant.Passing_item_objct;

        this.conn = null;
        constantfile = new Constant();
        this.detectorconn = new ConnectionDetector(DetailActivity.this);
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        initView();
        initAction();
        showData();

        CallAddView(Itemdata.getId());

    }


    public void setdisableclick() {
        fab_minus.setClickable(false);
        fab_plus.setClickable(false);
        fab_whatsappshare.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_minus.setClickable(true);
                fab_plus.setClickable(true);
                fab_whatsappshare.setClickable(true);
            }
        }, 200);
    }

    private void initView() {
        prf = new PrefManager(getApplicationContext());
        detail_rl = findViewById(R.id.detail_rl);
        fulllayout = findViewById(R.id.fulllayout);
        no_data_text = findViewById(R.id.no_data_text);
        detail_image = findViewById(R.id.detail_image);
        details_text = findViewById(R.id.details_text);
        fab_plus = findViewById(R.id.fab_plus);
        fab_minus = findViewById(R.id.fab_minus);
        fab_whatsappshare = findViewById(R.id.fab_whatsappshare);
    }

    public void ActionShareclick() {
        constantfile.snackbarcommonview(DetailActivity.this, detail_rl, "Processing");
        String finalString = "";
        String ShareString = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            finalString = String.valueOf(Html.fromHtml("" + Itemdata.getItem_description(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            finalString = String.valueOf((Html.fromHtml("" + Itemdata.getItem_description())));
        }
        if (finalString.length() > 1000) {
            ShareString = finalString.substring(0, 1000);
        } else {
            ShareString = finalString;
        }

        if (null != ShareString && ShareString.length() > 0) {
            int endIndex = ShareString.lastIndexOf(".");
            if (endIndex != -1) {
                ShareString = ShareString.substring(0, endIndex);
            }
        }

        String shareBody = ShareString + "...\n\n" + getResources().getString(R.string.share_message) + "\n\n https://play.google.com/store/apps/details?id=" + getPackageName();

        if (ActionTypepass.equals(WHATSAPP_ID)) {
            sharewhatsapp(shareBody);
        } else if (ActionTypepass.equals(SHARE_ID)) {
            sharenormal(shareBody);
        } else if (ActionTypepass.equals(COPY)) {
            copytext(shareBody);
        } else {
            constantfile.snackbarcommonview(DetailActivity.this, detail_rl, "Something Went Wrong! Try Again");
        }

    }


    private void initAction() {

        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                setfontsize(1);
            }
        });
        fab_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                setfontsize(0);
            }
        });
        fab_whatsappshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                ActionTypepass = WHATSAPP_ID;
                ActionShareclick();
            }
        });

    }

    public void setfontsize(int type){
            if (type == 0){
                if (deftextsize >= 40){
                    deftextsize = deftextsize - 5 ;
                    details_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,deftextsize);
                }
            }else if (type == 1){
                if (deftextsize <= 150){
                    deftextsize = deftextsize + 5 ;
                    details_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,deftextsize);
                }
            }else{
                deftextsize = 50;
                details_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,deftextsize);
            }
    }


    private void showData() {

        if (Itemdata == null) {
            onBackPressed();
            return;
        }

        String gettext = Itemdata.getItem_description();
        if (gettext == null && gettext.equals("")) {
            no_data_text.setVisibility(View.VISIBLE);
            fulllayout.setVisibility(View.GONE);
            return;
        }
        fulllayout.setVisibility(View.VISIBLE);
        setfontsize(-1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            details_text.setText(Html.fromHtml(Itemdata.getItem_description(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            details_text.setText(Html.fromHtml(Itemdata.getItem_description()));
        }

        try {
            Glide.with(DetailActivity.this).load(Itemdata.getCategory_image()).placeholder(R.drawable.bg_transparant).into(detail_image);
            if (!show_app_icon && Itemdata.getImage_thumb() != null && !Itemdata.getImage_thumb().equals("")){
                Glide.with(DetailActivity.this).load(Itemdata.getImage_thumb()).placeholder(R.drawable.bg_transparant).into(detail_image);
            }
            detail_image.setVisibility(View.VISIBLE);
        }catch (Exception e){
            detail_image.setVisibility(View.GONE);
        }
    }

    public void sharewhatsapp(String finaldata) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage(WHATSAPP_ID);
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, finaldata);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            constantfile.snackbarcommonview(DetailActivity.this, detail_rl, "Whatsapp have not been installed");
        }
    }

    public void sharenormal(String finaldata) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, finaldata);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void copytext(String finaldata) {
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getResources().getString(R.string.app_name), finaldata);
            clipboard.setPrimaryClip(clip);
            constantfile.snackbarcommonview(DetailActivity.this, detail_rl, "Text Copied Successfully!");
        } catch (Exception e) {
            constantfile.snackbarcommonview(DetailActivity.this, detail_rl, "Something Went Wrong! Try Again");
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
    public void onStop() {
        super.onStop();
    }


    public void snackbarcommonrelativeLong(Context mcontext, LinearLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionShareclick();
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


    public void CallAddView(String PassID) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("item_id", PassID);
        client.get(Constant.GET_ADD_VIEW, params, new AsynchronouseData());
    }


    class AsynchronouseData extends JsonHttpResponseHandler {

        AsynchronouseData() {
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

                } else {

                }
            } catch (Exception e) {
            }


        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_main, menu);
        this.menu = menu;
        setfavorite();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_copy:
                setdisableclick();
                ActionTypepass = COPY;
                ActionShareclick();
                return true;
            case R.id.action_share:
                setdisableclick();
                ActionTypepass = SHARE_ID;
                ActionShareclick();
                return true;
            case R.id.action_exit:
                AppExit();
                return true;
            case R.id.action_favorite:
                actionfavourite();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void actionfavourite() {
        if (db.isExist(Itemdata.getId())) {
            db.delete(Itemdata.getId());
            constantfile.snackbarcommonview(DetailActivity.this, detail_rl, "Remove from Favourite Successfully!");
        } else {
            String name = Itemdata.getItem_title();
            String details = Itemdata.getItem_description();
            String image = Itemdata.getImage();
            String image_thumb = Itemdata.getImage_thumb();
            String total_views = Itemdata.getTotal_views();
            String date = Itemdata.getDate();
            String cat_id = Itemdata.getCat_id();
            String cat_name = Itemdata.getCategory_name();
            String cat_img = Itemdata.getCategory_image();
            db.insert(Itemdata.getId(), name, details, image, image_thumb, total_views, date, cat_id, cat_name, cat_img);
            constantfile.snackbarcommonview(DetailActivity.this, detail_rl, "Add to Favourite Successfully!");
        }
        setfavorite();
    }


    public void setfavorite() {
        if (menu != null) {
            if (db.isExist(Itemdata.getId())) {
                Log.e("gettingfav",Itemdata.getId()+"  set full");
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_done));
            } else {
                Log.e("gettingfav",Itemdata.getId()+"  set line");
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
            }
        }
    }

    public void AppExit() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(DetailActivity.this);
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


}
