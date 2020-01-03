package com.techcorp.chhatrapatishivajimaharaj.quotes;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.AudienceNetworkAds;

public class AudienceNetworkInitializeHelper
        implements AudienceNetworkAds.InitListener {

    public static void initialize(Context context) {
        if (!AudienceNetworkAds.isInAdsProcess(context)){
            AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(new AudienceNetworkInitializeHelper())
                    .initialize();
        }
    }

    @Override
    public void onInitialized(AudienceNetworkAds.InitResult result) {
        Log.d(AudienceNetworkAds.TAG, result.getMessage());
    }
}