package com.happymothersdayquotes.status.wishesand.Images.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//
//import com.facebook.ads.AdSize;
//import com.facebook.ads.AdView;
//import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.happymothersdayquotes.status.wishesand.Images.Constant;
import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_Status;

import java.util.ArrayList;

public class StatusSingleActivity extends AppCompatActivity {

    ArrayList<Item_Status> mData;
    int cposition;
    TextView final_status;
    String finalString;
    private AdView adViewbanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_single);



        adViewbanner = findViewById(R.id.adViews);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewbanner.loadAd(adRequest);


        mData = new ArrayList<>();
        mData = Constant.Passing_item_status;
        cposition = Constant.Pass_status_pos;

        final_status = (TextView)findViewById(R.id.final_status);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final_status.setText(Html.fromHtml(mData.get(cposition).getStatus_text(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            final_status.setText(Html.fromHtml(mData.get(cposition).getStatus_text()));
        }


        finalString = String.valueOf(Html.fromHtml(mData.get(cposition).getStatus_text()));

        LinearLayout whatsapp = (LinearLayout)findViewById(R.id.whatsapp);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = finalString+"\n\n"+"Check more best Quotes https://play.google.com/store/apps/details?id="+getPackageName();
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(StatusSingleActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayout copy = (LinearLayout)findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(StatusSingleActivity.this,finalString);
            }
        });
        LinearLayout share = (LinearLayout)findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = finalString+"\n\n"+"Check more best Quotes https://play.google.com/store/apps/details?id="+getPackageName();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Raksha Bandhan Status");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });


    }

    private void setClipboard(Context context, String text) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (adViewbanner != null) {
            adViewbanner.destroy();
        }
        super.onDestroy();
    }

}
