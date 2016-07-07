package com.growthbeat.message;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.CardMessage;
import com.growthbeat.message.model.CloseButton;
import com.growthbeat.message.model.ImageButton;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.Picture;
import com.growthbeat.message.model.SwipeMessage;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageImageDownloader {

    private Message message = null;
    private Callback callback = null;
    private float density;

    public MessageImageDownloader(Message message, float density, Callback callback) {
        this.message = message;
        this.callback = callback;
        this.density = density;
    }

    public void download() {
        switch (message.getType()) {
            case card:
                download((CardMessage) message);
                break;
            case swipe:
                download((SwipeMessage) message);
                break;
            default:
                break;
        }
    }

    private void download(CardMessage cardMessage) {

        List<String> urlStrings = new ArrayList<String>();

        if (cardMessage.getPicture() != null && cardMessage.getPicture().getUrl() != null) {
            String pictureUrl = addDensityByPictureUrl(cardMessage.getPicture().getUrl());
            cardMessage.getPicture().setUrl(pictureUrl);
            urlStrings.add(pictureUrl);
        }

        List<String> buttonUrl = download(cardMessage.getButtons());
        urlStrings.addAll(buttonUrl);

        AsyncImageLoader loader = new AsyncImageLoader(callback);
        loader.execute(urlStrings.toArray(new String[0]));

    }

    private void download(SwipeMessage swipeMessage) {

        List<String> urlStrings = new ArrayList<String>();

        for (Picture picture : swipeMessage.getPictures()) {
            String pictureUrl = addDensityByPictureUrl(picture.getUrl());
            picture.setUrl(pictureUrl);
            urlStrings.add(pictureUrl);
        }

        List<String> buttonUrl = download(swipeMessage.getButtons());
        urlStrings.addAll(buttonUrl);

        AsyncImageLoader loader = new AsyncImageLoader(callback);
        loader.execute(urlStrings.toArray(new String[0]));

    }

    private List<String> download(List<Button> buttons) {

        List<String> urlStrings = new ArrayList<String>();

        for (Button button : buttons) {

            switch (button.getType()) {
                case image:
                    String imageButtonUrl = addDensityByPictureUrl(((ImageButton) button).getPicture().getUrl());
                    urlStrings.add(imageButtonUrl);
                    ((ImageButton) button).getPicture().setUrl(imageButtonUrl);
                    break;
                case close:
                    String closeButtonUrl = addDensityByPictureUrl(((CloseButton) button).getPicture().getUrl());
                    urlStrings.add(closeButtonUrl);
                    ((CloseButton) button).getPicture().setUrl(closeButtonUrl);
                    break;
                default:
                    continue;
            }
        }

        return urlStrings;

    }

    private String addDensityByPictureUrl(String originUrl) {

        if ((int) density <= 1)
            return originUrl;

        String url = originUrl;
        String[] paths = url.split("/");
        String filename = paths[paths.length - 1];
        String[] extension = filename.split("\\.");
        String resultFileName = String.format("%s@%dx.%s", extension[0], (int) density, extension[1]);
        paths = Arrays.copyOf(paths, paths.length - 1);
        String pathString = TextUtils.join("/", paths);
        return String.format("%s/%s", pathString, resultFileName);
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

        void success();

        void failure();

    }

}
