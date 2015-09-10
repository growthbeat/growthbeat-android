package com.growthbeat.link;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.growthbeat.utils.SystemServiceUtils;

public class FingerprintReceiver {

	public interface Callback {
		void onComplete(String fingerprintParameters);
	}

	public static void getFingerprintParameters(Context context, String fingerprintUrl, final Callback callback) {

		final WindowManager windowManager = SystemServiceUtils.getWindowManager(context);

		WebView webView = new WebView(context);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setVisibility(View.INVISIBLE);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView argWebView, String argString) {

				String requestString = argString;
				if (requestString.startsWith("native://fingerprint?fingerprintParameters=")) {
					Log.d("request_string", requestString);
					Map<String, String> param;
					String fingerpritnParameters = null;
					try {
						param = splitQuery(new URI(requestString));
						fingerpritnParameters = param.get("fingerprintParameters");
					} catch (URISyntaxException e) {
						e.printStackTrace();
						callback.onComplete(null);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					windowManager.removeView(argWebView);
					callback.onComplete(fingerpritnParameters);
				}
				return (true);
			}
		});

		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_TOAST,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
		windowManager.addView(webView, layoutParams);

		webView.loadUrl(fingerprintUrl);

	}

	private static Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String query = uri.getQuery();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}

}
