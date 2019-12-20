package com.techcorp.mahatmagandhi.quotes.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.techcorp.mahatmagandhi.quotes.Activity.MainActivity;
import com.techcorp.mahatmagandhi.quotes.ConnectionDetector;
import com.techcorp.mahatmagandhi.quotes.R;

/**
 * Created by Kakadiyas on 12-03-2017.
 */

public class PrivacyPolicyFragment extends Fragment {

    private ConnectionDetector detectorconn;
    Boolean conn;
    private ProgressBar progressBar;
    RelativeLayout relaivelayout;

    public PrivacyPolicyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.privacypolicyfragment, container, false);
        this.conn = null;

        relaivelayout = (RelativeLayout) rootView.findViewById(R.id.relaivelayout);
        this.detectorconn = new ConnectionDetector(getActivity());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        if (conn.booleanValue()) {
            WebView webView =(WebView)rootView.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl("https://simpleappscreatorpolicy.blogspot.com/2019/07/status-policy.html");
        } else {
            noInternetConnectionDialog();
        }

        return rootView;
    }

    public void noInternetConnectionDialog() {
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(getActivity());
        errorDialog.setTitle((CharSequence) "Error");
        errorDialog.setMessage((CharSequence) "No internet connection.");
        errorDialog.setNeutralButton((CharSequence) "OK", new dialogclicklistner());
        AlertDialog errorAlert = errorDialog.create();
        errorAlert.show();
    }

    class dialogclicklistner implements DialogInterface.OnClickListener {
        dialogclicklistner() {
        }

        public void onClick(DialogInterface dialog, int id) {
            PrivacyPolicyFragment.this.detectorconn = new ConnectionDetector(PrivacyPolicyFragment.this.getActivity());
            PrivacyPolicyFragment.this.conn = Boolean.valueOf(PrivacyPolicyFragment.this.detectorconn.isConnectingToInternet());
            dialog.dismiss();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity)getActivity()).SelectItem(getActivity().getResources().getString(R.string.app_name),0);

                }
                return true;
            }
        });

    }

}


