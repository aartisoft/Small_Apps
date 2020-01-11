package com.mia3.wallpaperandbackgrounds;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class ConnectionDetector {
    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) this._context.getSystemService("connectivity");
        if (connectivity != null) {
            Log.d("Network", "connected");
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == State.CONNECTED) {
                        Log.d("Network", "NETWORKnAME: " + info[i].getTypeName());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
