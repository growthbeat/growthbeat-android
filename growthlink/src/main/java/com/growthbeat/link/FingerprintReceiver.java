package com.growthbeat.link;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.growthbeat.utils.SystemServiceUtils;

public class FingerprintReceiver {

    public interface Callback {
        void onComplete(String fingerprintParameters);
    }

    public static void getFingerprintParameters(final Context context, final String fingerprintUrl, final Callback callback) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                final WindowManager windowManager = SystemServiceUtils.getWindowManager(context);

                WebView webView = new WebView(context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.setVisibility(View.INVISIBLE);
                webView.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView webView, String urlString) {

                        Uri uri = Uri.parse(urlString);
                        if (uri == null)
                            return false;

                        if (!uri.getScheme().equals("native"))
                            return false;

                        if (uri.getHost().equals("fingerprint")) {
                            callback.onComplete(uri.getQueryParameter("fingerprintParameters"));
                            windowManager.removeView(webView);
                        }

                        return true;

                    }
                });

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
                windowManager.addView(webView, layoutParams);

                webView.loadUrl(fingerprintUrl);

            }
        });

    }

}
