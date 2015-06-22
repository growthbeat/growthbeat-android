package com.growthbeat.message.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.model.BannerMessage;
import com.growthbeat.message.model.BannerMessage.BannerType;
import com.growthbeat.message.model.BannerMessage.Position;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.Message;

public class BannerMessageView extends FrameLayout {

	private BannerMessage bannerMessage = null;
	// private Context context = null;
	private ProgressBar progressBar = null;

	private Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

	public BannerMessageView(Activity activity, Message message) {

		super(activity.getApplicationContext());

		if (message == null || !(message instanceof BannerMessage))
			return;

		Context context = activity.getApplicationContext();
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
				this.failure();
			}
		};

		MessageImageDownloader messageImageDonwloader = new MessageImageDownloader(((FragmentActivity) activity).getSupportLoaderManager(),
				activity, bannerMessage, callback);
		messageImageDonwloader.download();

		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = 100;
		layoutParams.gravity = bannerMessage.getPosition() == Position.top ? Gravity.TOP : Gravity.BOTTOM;
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
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
				Button button = bannerMessage.getButtons().get(0);
				GrowthMessage.getInstance().selectButton(button, bannerMessage);
				hide();
			}
		});

		Log.e("", "" + bannerMessage.getPicture().getUrl());
		Log.e("", "" + bannerMessage.getPicture().getWidth());
		imageView.setImageBitmap(cachedImages.get(bannerMessage.getPicture().getUrl()));
		innerLayout.addView(imageView);

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
		LinearLayout textLayout = new LinearLayout(getContext());
		textLayout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		TextView caption = new TextView(getContext());
		textLayout.addView(caption, textLayoutParams);
		TextView text = new TextView(getContext());
		textLayout.addView(text, textLayoutParams);

		LinearLayout.LayoutParams baseLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		baseLayout.addView(textLayout, baseLayoutParams);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				hide();
			}
		}, this.bannerMessage.getDuration());

	}

	private void showCloseButton(FrameLayout innerLayout) {

		if (bannerMessage.getButtons().size() < 2)
			return;

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				hide();
			}
		}, this.bannerMessage.getDuration());

	}

	public void hide() {
		getWindowsManager().removeView(this);
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

	private View wrapViewWithAbsoluteLayout(View view, Rect rect) {

		FrameLayout frameLayout = new FrameLayout(getContext());
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(rect.getWidth(), rect.getHeight());
		layoutParams.setMargins(rect.getLeft(), rect.getTop(), 0, 0);
		layoutParams.gravity = android.view.Gravity.FILL;
		frameLayout.setLayoutParams(layoutParams);

		view.setLayoutParams(new ViewGroup.LayoutParams(rect.getWidth(), rect.getHeight()));
		frameLayout.addView(view);

		return frameLayout;

	}

}
