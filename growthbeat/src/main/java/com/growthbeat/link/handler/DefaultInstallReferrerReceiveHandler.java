package com.growthbeat.link.handler;

import android.content.Context;
import android.content.Intent;

import com.growthbeat.link.GrowthLink;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DefaultInstallReferrerReceiveHandler implements InstallReferrerReceiveHandler {

    @Override
    public void onReceive(Context context, Intent intent) {

        String encodedInstallReferrer = intent.getStringExtra("referrer");
        String installReferrer = "";

        if (encodedInstallReferrer != null) {
            try {
                installReferrer = URLDecoder.decode(encodedInstallReferrer, "utf-8");
            } catch (UnsupportedEncodingException e) {
                GrowthLink.getInstance().getLogger().error("Failed to decode install referrer: " + e.getMessage());
            }
        }

        GrowthLink.getInstance().getPreference().setContext(context.getApplicationContext());
        GrowthLink.getInstance().setInstallReferrer(installReferrer);

    }

}
