package com.growthbeat.link.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Context;
import android.content.Intent;

import com.growthbeat.link.GrowthLink;

public class DefaultInstallReferrerReceiveHandler implements InstallReferrerReceiveHandler {

	@Override
	public void onReceive(Context context, Intent intent) {

		String referrer = intent.getStringExtra("referrer");
		if (referrer != null) {
			String decoded = "";
			try {
				decoded = URLDecoder.decode(referrer, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (decoded.length() > 0) {
				GrowthLink.getInstance().setInstallReferrer(decoded);
			}
		}

	}

}
