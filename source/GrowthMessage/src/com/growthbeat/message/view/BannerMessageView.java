package com.growthbeat.message.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.model.BannerMessage;
import com.growthbeat.message.model.BannerMessage.BannerType;
import com.growthbeat.message.model.BannerMessage.Position;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.CloseButton;
import com.growthbeat.message.model.ImageButton;
import com.growthbeat.message.model.Message;

public class BannerMessageView extends FrameLayout {

	private BannerMessage bannerMessage = null;
	private ProgressBar progressBar = null;

	private Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

	private boolean showBanner = false;

	private BannerMetrics bannerMetrics = null;

	private class BannerMetrics {
		public int designWidth = 320;
		public int designHeight = 70;
		public float ratio = 1.0f;
		public int longPixels = 0;
		public int shortPixels = 0;

		BannerMetrics() {
			DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
			longPixels = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
			ratio = longPixels / displayMetrics.density / designWidth;
			shortPixels = (int) (designHeight * ratio * displayMetrics.density);
		}
	}

	public BannerMessageView(Context context, Message message) {

		super(context);

		if (message == null || !(message instanceof BannerMessage))
			return;

		this.bannerMessage = (BannerMessage) message;

		progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
		this.addView(progressBar, new FrameLayout.LayoutParams(100, 100, Gravity.CENTER));

		MessageImageDownloader.Callback callback = new MessageImageDownloader.Callback() {
			@Override
			public void success(Map<String, Bitmap> images) {
				cachedImages = images;
				progressBar.setVisibility(View.GONE);
				showOnlyImage(BannerMessageView.this);
				showImageText(BannerMessageView.this);
				showCloseButton(BannerMessageView.this);
			}

			@Override
			public void failure() {

			}
		};

		BannerImageLoader bannerImageLoader = new BannerImageLoader(message, callback);
		bannerImageLoader.download();

		bannerMetrics = new BannerMetrics();
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.width = bannerMetrics.longPixels;
		if (bannerMessage.getBannerType() == BannerType.onlyImage)
			layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
		else
			layoutParams.height = bannerMetrics.shortPixels;

		layoutParams.gravity = bannerMessage.getPosition() == Position.top ? Gravity.TOP : Gravity.BOTTOM;
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		layoutParams.format = PixelFormat.TRANSLUCENT;

		getWindowsManager().addView(this, layoutParams);

	}

	private WindowManager getWindowsManager() {
		return (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	}

	private void showOnlyImage(FrameLayout innerLayout) {

		if (bannerMessage.getBannerType() != BannerType.onlyImage)
			return;

		ImageView imageView = new ImageView(getContext());
		imageView.setScaleType(ScaleType.FIT_CENTER);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hide();
				Button button = bannerMessage.getButtons().get(0);
				GrowthMessage.getInstance().selectButton(button, bannerMessage);
			}
		});

		imageView.setImageBitmap(cachedImages.get(bannerMessage.getPicture().getUrl()));
		innerLayout.addView(imageView);
		showBanner = true;

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				hide();
			}
		}, this.bannerMessage.getDuration());

	}

	private void showImageText(FrameLayout innerLayout) {

		if (bannerMessage.getBannerType() != BannerType.imageText)
			return;

		LinearLayout baseLayout = new LinearLayout(getContext());
		baseLayout.setOrientation(LinearLayout.HORIZONTAL);
		baseLayout.setBackgroundColor(Color.GRAY);
		AlphaAnimation alpha = new AlphaAnimation(0.98f, 0.98f);
		alpha.setDuration(0);
		alpha.setFillAfter(true);
		baseLayout.startAnimation(alpha);
		baseLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
				Button button = bannerMessage.getButtons().get(0);
				GrowthMessage.getInstance().selectButton(button, bannerMessage);
			}
		});

		int iconDesignWidth = 56;
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

		int iconWidthpixels = (int) (iconDesignWidth * bannerMetrics.ratio * displayMetrics.density);
		LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(iconWidthpixels, iconWidthpixels);
		int margin = (int) ((bannerMetrics.shortPixels - iconWidthpixels) * 0.5);
		iconLayoutParams.setMargins(margin, margin, margin, 0);
		ImageView iconImage = new ImageView(getContext());
		iconImage.setScaleType(ScaleType.FIT_CENTER);
		iconImage.setImageBitmap(cachedImages.get(bannerMessage.getPicture().getUrl()));
		baseLayout.addView(iconImage, iconLayoutParams);

		RelativeLayout textLayout = new RelativeLayout(getContext());
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				(int) (bannerMetrics.longPixels * 0.65), RelativeLayout.LayoutParams.MATCH_PARENT);
		textLayout.setLayoutParams(layoutParams);

		TextView caption = new TextView(getContext());
		caption.setTextSize(10 * bannerMetrics.ratio);
		caption.setHorizontallyScrolling(true);
		caption.setEllipsize(TruncateAt.END);
		caption.setText(bannerMessage.getCaption());
		RelativeLayout.LayoutParams captionParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		captionParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		captionParams.setMargins(0, (int) (bannerMetrics.shortPixels * 0.28), 0, 0);
		caption.setLayoutParams(captionParams);
		textLayout.addView(caption);

		TextView text = new TextView(getContext());
		text.setTextSize(12 * bannerMetrics.ratio);
		text.setHorizontallyScrolling(true);
		text.setEllipsize(TruncateAt.END);
		text.setText(bannerMessage.getText());
		RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		textParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		textParams.setMargins(0, 0, 0, (int) (bannerMetrics.shortPixels * 0.28));
		text.setLayoutParams(textParams);
		textLayout.addView(text);

		baseLayout.addView(textLayout);

		innerLayout.addView(baseLayout);
		showBanner = true;

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				hide();
			}
		}, this.bannerMessage.getDuration());

	}

	private void showCloseButton(FrameLayout innerLayout) {

		List<Button> buttons = extractButtons(Button.Type.close);
		if (bannerMessage.getButtons().size() < 2)
			return;

		int closeDesignWidth = 20;
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int closeWidthpixels = (int) (closeDesignWidth * bannerMetrics.ratio * displayMetrics.density);
		FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(closeWidthpixels, closeWidthpixels);
		closeParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		closeParams.setMargins(0, 0, (int) (closeWidthpixels * 0.8), 0);

		final CloseButton closeButton = (CloseButton) buttons.get(0);

		TouchableImageView touchableImageView = new TouchableImageView(getContext());
		touchableImageView.setScaleType(ScaleType.FIT_CENTER);
		touchableImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
				GrowthMessage.getInstance().selectButton(closeButton, bannerMessage);
			}
		});
		touchableImageView.setImageBitmap(cachedImages.get(closeButton.getPicture().getUrl()));
		innerLayout.addView(touchableImageView, closeParams);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				hide();
			}
		}, this.bannerMessage.getDuration());

	}

	public void hide() {
		if (showBanner) {
			showBanner = false;
			getWindowsManager().removeView(this);
		}
	}

	private List<Button> extractButtons(Button.Type type) {

		List<Button> buttons = new ArrayList<Button>();

		for (Button button : bannerMessage.getButtons()) {
			if (button.getType() == type) {
				buttons.add(button);
			}
		}

		return buttons;

	}

	private static class BannerImageLoader {

		private Message message = null;
		private MessageImageDownloader.Callback callback = null;

		public BannerImageLoader(Message message, MessageImageDownloader.Callback callback) {
			this.message = message;
			this.callback = callback;
		}

		public void download() {
			switch (message.getType()) {
			case banner:
				download((BannerMessage) message);
				break;

			default:
				break;
			}
		}

		private void download(BannerMessage bannerMessage) {

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

		private static class AsyncImageLoader extends AsyncTask<String, Integer, Map<String, Bitmap>> {

			private static final int IMAGE_DOWNLOAD_TIMEOUT = 10 * 1000;
			private MessageImageDownloader.Callback callback = null;

			public AsyncImageLoader(MessageImageDownloader.Callback callback) {
				this.callback = callback;
			}

			@Override
			protected Map<String, Bitmap> doInBackground(String... params) {

				Map<String, Bitmap> images = new HashMap<String, Bitmap>();

				for (String urlString : params) {

					HttpClient httpClient = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), IMAGE_DOWNLOAD_TIMEOUT);
					HttpConnectionParams.setSoTimeout(httpClient.getParams(), IMAGE_DOWNLOAD_TIMEOUT);

					try {
						HttpResponse httpResponse = httpClient.execute(new HttpGet(urlString));
						if (httpResponse.getStatusLine().getStatusCode() < 200
								&& httpResponse.getStatusLine().getStatusCode() >= 300)
							continue;
						images.put(urlString, BitmapFactory.decodeStream(httpResponse.getEntity().getContent()));
					} catch (Exception e) {
					}
				}

				return images;

			}

			@Override
			protected void onPostExecute(Map<String, Bitmap> images) {
				callback.success(images);
			}

		}
	}

}
