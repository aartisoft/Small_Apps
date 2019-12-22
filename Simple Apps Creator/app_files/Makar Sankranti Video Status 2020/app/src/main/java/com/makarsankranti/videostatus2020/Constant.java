package com.makarsankranti.videostatus2020;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.google.android.material.snackbar.Snackbar;
import com.makarsankranti.videostatus2020.gettersetter.Item_collections;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Constant {
    public static final String APP_ID = "3";


    public static final String BASIC_URL = "http://simpleappscreator.com/commonvideo_status/api_common/";

    public static final String GET_HOME_LISTING = BASIC_URL+ "api_home.php";
    public static final String GET_COMMON_LISTING = BASIC_URL+ "api_common_listing.php";
    public static final String GET_ADD_VIEW = BASIC_URL+ "api_add_view.php";
    public static final String GET_ADD_TOKEN = BASIC_URL+ "api_add_token.php";


    public static Boolean Passing_from_notification = false;
    public static final String SHARED_PREF = "ah_firebase_MakarSankranti";
    public static final String SHARED_Token = "regId";
    public static final String SHARED_NOTIFICATION_SET = "notification_set";
    public static final String SHARED_AUTOPLAY_SET = "autoplay_set";
    public static final String TOPIC_GLOBAL = "global";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static int Adscount = 2;
    public static int Adscountlisting = 2;

    public static final int NUMBER_OF_ADS = 5;

    public static String Passing_From = "trending";
    public static String Passing_From_title = "";

    public static Item_collections Passing_item_object = new Item_collections();

    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public void snackbarcommonrelative(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public void snackbarcommonlinear(Context mcontext, LinearLayout coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public void snackbarcommondrawerLayout(Context mcontext, DrawerLayout coordinatorLayout, String snackmsg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg+"", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }


    public ArrayList<Item_collections> ConvertJSONtoModel(JSONArray getarray) throws JSONException {
        ArrayList<Item_collections> temp_array = new ArrayList<>();

        for (int i = 0; i< getarray.length();i++){
            JSONObject pass = getarray.getJSONObject(i);
            Item_collections temp_item = new Item_collections();

            temp_item.setId(pass.getString("id"));
            temp_item.setVideo_name(pass.getString("video_name"));
            temp_item.setVideo_url(pass.getString("video_url"));
            temp_item.setVideo_image_thumb(pass.getString("video_image_thumb"));
            temp_item.setTotal_views(pass.getString("total_views"));
            temp_item.setIs_type(pass.getString("is_layout"));
            temp_item.setDate(pass.getString("date"));
            temp_item.setCat_id(pass.getString("cat_id"));
            temp_item.setCategory_name(pass.getString("category_name"));
            temp_array.add(temp_item);
        }

        return temp_array;
    }

}
