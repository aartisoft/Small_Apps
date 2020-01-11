package com.karelakefayde.hindi.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.snackbar.Snackbar;
import com.karelakefayde.hindi.Activity.DetailActivity;
import com.karelakefayde.hindi.Activity.MainActivity;
import com.karelakefayde.hindi.Adapter.MainHomeAdapter;
import com.karelakefayde.hindi.ConnectionDetector;
import com.karelakefayde.hindi.Constant;
import com.karelakefayde.hindi.R;
import com.karelakefayde.hindi.gettersetter.ItemImageHome;
import com.karelakefayde.hindi.gettersetter.Item_images;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.karelakefayde.hindi.Constant.show_app_icon;

/**
 * Created by Kakadiyas on 12-03-2017.
 */
public class HomeFragment extends Fragment implements MainHomeAdapter.MyClickListener {

    public static final String TAG = "Main_list";
    private ConnectionDetector detectorconn;
    Boolean conn;
    Constant constantfile;
    RelativeLayout content_home, text_back_rl;
    LinearLayout main_linear, intro_ll;
    RecyclerView items_recycler;
    TextView no_data_text, text_intro_title, text_intro_text;
    ImageView intro_image;
    CardView seeall_cardview;
    private ProgressBar progressBar;
    AdView adViewbanner;
    InterstitialAd minterstitialAd;

    MainHomeAdapter mainAdapter;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.homefragment, container, false);
        setHasOptionsMenu(true);
        this.conn = null;
        constantfile = new Constant();

        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        adViewbanner = new AdView(getActivity(), getResources().getString(R.string.facebook_banner_id), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) rootView.findViewById(R.id.ads);
        adContainer.addView(adViewbanner);
        adViewbanner.loadAd();


        initInterstitialAdPrepare();

        text_intro_title = rootView.findViewById(R.id.text_intro_title);
        text_intro_text = rootView.findViewById(R.id.text_intro_text);
        intro_image = rootView.findViewById(R.id.intro_image);
        seeall_cardview = rootView.findViewById(R.id.seeall_cardview);
        content_home = rootView.findViewById(R.id.content_home);
        text_back_rl = rootView.findViewById(R.id.text_back_rl);
        main_linear = rootView.findViewById(R.id.main_linear);
        intro_ll = rootView.findViewById(R.id.intro_ll);
        intro_ll.setVisibility(View.GONE);
        progressBar = rootView.findViewById(R.id.progressBar);
        items_recycler = rootView.findViewById(R.id.items_recycler);
        no_data_text = rootView.findViewById(R.id.no_data_text);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        items_recycler.setLayoutManager(mLayoutManager);
        items_recycler.setItemAnimator(new DefaultItemAnimator());
        items_recycler.setHasFixedSize(true);
        mainAdapter = new MainHomeAdapter(getActivity());
        mainAdapter.setClickListener(this);
        items_recycler.setAdapter(mainAdapter);

        seeall_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).SelectItem(getActivity().getResources().getString(R.string.app_name), 3);
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoadImagedata();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onItemClick(int position, Item_images getarray, View v) {
        Constant.Passing_item_id = getarray.getId();
        Constant.Passing_item_objct = new Item_images();
        Constant.Passing_item_objct = getarray;
        if (Constant.Adscountlisting >= 2) {
            if (minterstitialAd.isAdLoaded()) {
                Constant.Adscountlisting = 1;
                minterstitialAd.show();
            } else {
                onAdsresponce();
            }
        } else {
            Constant.Adscountlisting++;
            onAdsresponce();
        }

    }

    private void requestNewInterstitial() {
        if (minterstitialAd != null) {
            minterstitialAd.loadAd();
        }
    }

    private void initInterstitialAdPrepare() {
        minterstitialAd = new InterstitialAd(getActivity(), getResources().getString(R.string.facebook_interstitial_id));
        minterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Constant.Adscountlisting = 1;
                onAdsresponce();
                requestNewInterstitial();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("gettingadserror"," error  : "+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        requestNewInterstitial();
    }

    public void onAdsresponce() {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        getActivity().startActivity(intent);
    }


    public void LoadImagedata() {
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        if (this.conn.booleanValue()) {
            items_recycler.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            getImagelistData();
        } else {
            progressBar.setVisibility(View.GONE);
            items_recycler.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);
            snackbarcommonrelativeLong(getActivity(), content_home, getActivity().getResources().getString(R.string.no_internet));

        }
    }


    public void getImagelistData() {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        client.addHeader("Content-Type", "application/json; charset=utf-8");
        params.put("cat_id", Constant.APP_ID);
        client.get(Constant.GET_HOME_LISTING, params, new AsynchronouseData());
    }


    class AsynchronouseData extends JsonHttpResponseHandler {

        AsynchronouseData() {
        }

        public void onStart() {
            super.onStart();
            progressBar.setVisibility(View.VISIBLE);
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {

            try {
                JSONArray getdata = bytes.getJSONArray("data");
                ItemImageHome imageData = new ItemImageHome();
                imageData.setStatus(bytes.getBoolean("status"));
                imageData.setMessage(bytes.getString("message"));
                imageData.setShow_intro(bytes.getString("show_intro"));
                imageData.setShow_app_icon(bytes.getString("show_app_icon"));
                imageData.setIntro_title(bytes.getString("intro_title"));
                imageData.setIntro_description(bytes.getString("intro_description"));
                imageData.setIntro_backcolor(bytes.getString("intro_backcolor"));
                imageData.setIntro_image(bytes.getString("intro_image"));
                imageData.setData(constantfile.ConvertJSONImage_Model(getdata));

                if (imageData.isStatus()) {

                    if (imageData.getShow_app_icon().equals("1")) {
                        show_app_icon = true;
                    }
                    if (imageData.getShow_intro().equals("1")) {
                        intro_ll.setVisibility(View.VISIBLE);
                        text_intro_title.setText(imageData.getIntro_title() + "");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            text_intro_text.setText(Html.fromHtml(imageData.getIntro_description() + "", Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            text_intro_text.setText(Html.fromHtml(imageData.getIntro_description() + ""));
                        }
                        if (imageData.getIntro_image() != null && !imageData.getIntro_image().equals("")) {
                            Glide.with(getActivity()).load(imageData.getIntro_image()).placeholder(R.drawable.bg_transparant).into(intro_image);
                            intro_image.setVisibility(View.VISIBLE);
                        } else {
                            intro_image.setVisibility(View.GONE);
                        }
                        if (imageData.getIntro_backcolor() != null && !imageData.getIntro_backcolor().equals("")) {
                            text_back_rl.setBackgroundColor(Color.parseColor("#" + imageData.getIntro_backcolor()));
                        }
                    } else {
                        intro_ll.setVisibility(View.GONE);
                    }

                    items_recycler.setVisibility(View.VISIBLE);
                    mainAdapter.adddata(imageData.getData());
                    no_data_text.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    items_recycler.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("gettocatch", "  :::  " + e.getMessage());
                progressBar.setVisibility(View.GONE);
                items_recycler.setVisibility(View.GONE);
                no_data_text.setVisibility(View.VISIBLE);

            }

        }

    }

    @Override
    public void onDestroy() {
        if (adViewbanner != null) {
            adViewbanner.destroy();
        }
        if (minterstitialAd != null) {
            minterstitialAd.destroy();
        }
        super.onDestroy();
    }


    public void snackbarcommonrelativeLong(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadImagedata();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorbutton));
        TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        TextView textaction = (TextView) snackbarView.findViewById(R.id.snackbar_action);
        textView.setTextSize(16);
        textaction.setTextSize(18);
        textView.setTextColor(ContextCompat.getColor(mcontext, R.color.colorAccentlight));
        textaction.setTextColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        snackbar.show();
    }

}
