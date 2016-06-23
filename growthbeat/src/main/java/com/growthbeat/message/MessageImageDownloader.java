package com.growthbeat.message;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.CardMessage;
import com.growthbeat.message.model.CloseButton;
import com.growthbeat.message.model.ImageButton;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.Picture;
import com.growthbeat.message.model.SwipeMessage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;

public class MessageImageDownloader implements LoaderCallbacks<Bitmap> {

	private LoaderManager loaderManager;
	private Context context;
	private Message message;
	private float density;
	private Callback callback;

	private List<String> urlStrings = new ArrayList<String>();
	private Map<String, Bitmap> images = new HashMap<String, Bitmap>();

	public MessageImageDownloader(LoaderManager loaderManager, Context context, Message message, float density, Callback callback) {
		super();
		this.loaderManager = loaderManager;
		this.context = context;
		this.message = message;
		this.density = density;
		this.callback = callback;
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
			if (callback != null) {
				callback.failure();
				callback = null;
			}
			break;
		}

	}

	private void download(CardMessage cardMessage) {

		if (cardMessage.getPicture().getUrl() != null) {
            String pictureUrl = addDensityByPictureUrl(cardMessage.getPicture().getUrl());
            cardMessage.getPicture().setUrl(pictureUrl);
			urlStrings.add(pictureUrl);
		}

        download(cardMessage.getButtons());

		int loaderId = -1;
		for (String urlString : urlStrings) {
			Bundle bundle = new Bundle();
			bundle.putString("url", urlString);
			loaderManager.initLoader(loaderId++, bundle, this);
		}

	}

	private void download(SwipeMessage swipeMessage) {

        for (Picture picture : swipeMessage.getPictures())
			urlStrings.add(picture.getUrl());

		download(swipeMessage.getButtons());

		int loaderId = -1;
		for (String urlString : urlStrings) {
			Bundle bundle = new Bundle();
			bundle.putString("url", urlString);
			loaderManager.initLoader(loaderId++, bundle, this);
		}

	}

    private void download(List<Button> buttons) {

        for (Button button : buttons) {

            switch (button.getType()) {
                case image:
                    String imageButtonUrl = addDensityByPictureUrl(((ImageButton) button).getPicture().getUrl());
                    urlStrings.add(addDensityByPictureUrl(imageButtonUrl));
                    ((ImageButton) button).getPicture().setUrl(imageButtonUrl);
                    break;
                case close:
                    String closeButtonUrl = addDensityByPictureUrl(((CloseButton) button).getPicture().getUrl());
                    urlStrings.add(addDensityByPictureUrl(((CloseButton) button).getPicture().getUrl()));
                    ((CloseButton) button).getPicture().setUrl(closeButtonUrl);
                    break;
                default:
                    continue;
            }
        }

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

	@Override
	public Loader<Bitmap> onCreateLoader(int id, Bundle bundle) {
		Loader<Bitmap> loader = new ImageLoader(context, bundle.getString("url"));
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Bitmap> loader, Bitmap bitmap) {

		if (bitmap == null) {
			if (callback != null) {
				callback.failure();
				callback = null;
			}
			return;
		}

		if (!(loader instanceof ImageLoader))
			return;
		String urlString = ((ImageLoader) loader).getUrlString();

		images.put(urlString, bitmap);
		urlStrings.remove(urlString);
		if (urlStrings.size() == 0) {
			if (callback != null) {
				callback.success(images);
			}
		}

	}

	@Override
	public void onLoaderReset(Loader<Bitmap> loader) {
		loader.reset();
	}

	public interface Callback {

		public void success(Map<String, Bitmap> images);

		public void failure();

	}

	private static class ImageLoader extends AsyncTaskLoader<Bitmap> {

		private static final int IMAGE_DOWNLOAD_TIMEOUT = 10 * 1000;

		private String urlString;

		public ImageLoader(Context context, String urlString) {
			super(context);
			this.urlString = urlString;
		}

		@Override
		public Bitmap loadInBackground() {

			if (urlString == null)
				return null;

			try {
				URL url = new URL(urlString);
				HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestMethod("GET");
				httpConnection.setConnectTimeout(IMAGE_DOWNLOAD_TIMEOUT);
				httpConnection.setReadTimeout(IMAGE_DOWNLOAD_TIMEOUT);
				httpConnection.connect();
				int code = httpConnection.getResponseCode();
				if (code < 200 && code >= 300)
					return null;
				return BitmapFactory.decodeStream(httpConnection.getInputStream());
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public void onCanceled(Bitmap bitmap) {
			if (bitmap == null)
				return;
			if (bitmap.isRecycled())
				return;
			bitmap.recycle();
			bitmap = null;
		}

		public String getUrlString() {
			return urlString;
		}

	}

}
