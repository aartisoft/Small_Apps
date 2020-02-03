package com.punjabivideostatus.latestsong.Activity;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.punjabivideostatus.latestsong.ConnectionDetector;
import com.punjabivideostatus.latestsong.Constant;
import com.punjabivideostatus.latestsong.R;
import com.punjabivideostatus.latestsong.gettersetter.ItemUpdate;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class RequestVideoActivity extends AppCompatActivity {

    EditText user_name;
    EditText user_email;
    EditText request_video_name;
    EditText request_video_suggest;
    RelativeLayout request_send_rl;

    Boolean conn;
    private ConnectionDetector detectorconn;

    Constant constantfile;
    RelativeLayout relative_request;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        constantfile = new Constant();

        this.conn = null;
        this.detectorconn = new ConnectionDetector(RequestVideoActivity.this);
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_request));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initAction();
    }

    private void initAction() {

        this.request_send_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callapicalling();
            }
        });

    }

    public void callapicalling(){
        conn = Boolean.valueOf(detectorconn.isConnectingToInternet());
        if (conn.booleanValue()) {
            if (user_name.getText().toString() != null && !user_name.getText().toString().equals("")) {
                if (user_email.getText().toString() != null && !user_email.getText().toString().equals("")) {
                    if (user_email.getText().toString().matches(emailPattern)) {
                        if (request_video_name.getText().toString() != null && !request_video_name.getText().toString().equals("")) {
                            String username = user_name.getText().toString();
                            String useremail = user_email.getText().toString();
                            String video_title = request_video_name.getText().toString();
                            String video_detail = request_video_suggest.getText().length() > 0 ? request_video_suggest.getText().toString() : "";
                            CallVideoRequest(username,useremail,video_title,video_detail);
                        } else {
                            request_video_name.setError("Video Name is Required!");
                        }
                    } else {
                        user_email.setError("Invalid Email Address!");
                    }
                } else {
                    user_email.setError("Email is Required!");
                }
            } else {
                user_name.setError("Name is Required!");
            }
        } else {
            snackbarcommonrelativeLong(RequestVideoActivity.this, relative_request, getResources().getString(R.string.no_internet));
        }
    }


    private void initView() {
        this.relative_request = (RelativeLayout) findViewById(R.id.relative_request);
        this.request_send_rl = (RelativeLayout) findViewById(R.id.request_send_rl);
        this.user_name = (EditText) findViewById(R.id.user_name);
        this.user_email = (EditText) findViewById(R.id.user_email);
        this.request_video_name = (EditText) findViewById(R.id.request_video_name);
        this.request_video_suggest = (EditText) findViewById(R.id.request_video_suggest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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


    public void CallVideoRequest(String name,String email,String title,String detail) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("user_name", name);
        params.put("user_email", email);
        params.put("video_title", title);
        params.put("video_detail", detail);
        client.get(Constant.GET_VIDEO_REQUEST, params, new AsynchronouseData());
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
                Log.e("gettingrequestsend",statusData.toString());
                if (statusData.isStatus()) {
                    Toast.makeText(RequestVideoActivity.this,statusData.getMessage()+"",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    constantfile.snackbarcommonrelative(RequestVideoActivity.this,relative_request,statusData.getMessage()+"");
                }
            } catch (Exception e) {
            }


        }

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {

        }
    }


    public void snackbarcommonrelativeLong(Context mcontext, View coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callapicalling();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        TextView textaction = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textaction.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textaction.setTextColor(Color.BLACK);
        snackbar.show();
    }
}
