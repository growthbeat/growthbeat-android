package com.growthbeat.message.handler;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.CardMessage;
import com.growthbeat.message.model.CloseButton;
import com.growthbeat.message.model.ImageButton;
import com.growthbeat.message.model.Message;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageDownloader {

    private Message message = null;
    private Callback callback = null;

    public ImageDownloader(Context context, Message message, float density, Callback callback) {
        this.message = message;
        this.callback = callback;
    }

    public void download() {
        switch (message.getType()) {
            case card:
                download((CardMessage) message);
                break;
            case swipe:
                break;
            default:
                break;
        }
    }

    private void download(CardMessage bannerMessage) {

        List<String> urlStrings = new ArrayList<String>();

        if (bannerMessage.getPicture().getUrl() != null) {
            urlStrings.add(bannerMessage.getPicture().getUrl());
        }

        for (Button button : bannerMessage.getButtons()) {
            switch (button.getType()) {
                case image:
                    urlStrings.add(((ImageButton) button).getPicture().getUrl());
                    break;
                case close:
                    urlStrings.add(((CloseButton) button).getPicture().getUrl());
                    break;
                default:
                    continue;
            }
        }

        AsyncImageLoader loader = new AsyncImageLoader(callback);
        loader.execute(urlStrings.toArray(new String[0]));

    }

    private static class AsyncImageLoader extends AsyncTask<String, Integer, Boolean> {

        private static final int IMAGE_DOWNLOAD_TIMEOUT = 10 * 1000;
        private Callback callback = null;

        public AsyncImageLoader(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            for (String urlString : params) {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestMethod("GET");
                    httpConnection.setConnectTimeout(IMAGE_DOWNLOAD_TIMEOUT);
                    httpConnection.setReadTimeout(IMAGE_DOWNLOAD_TIMEOUT);
                    httpConnection.connect();
                    int code = httpConnection.getResponseCode();
                    if (code < 200 && code >= 300)
                        continue;
                    GrowthMessage.getInstance().getMessageImageCacheManager().put(urlString, BitmapFactory.decodeStream(httpConnection.getInputStream()));
                } catch (Exception e) {
                    callback.failure();
                    return false;
                }
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                callback.success();
        }

    }

    public interface Callback {

        public void success();

        public void failure();

    }

}
