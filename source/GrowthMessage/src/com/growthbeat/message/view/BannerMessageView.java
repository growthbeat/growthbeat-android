package com.growthbeat.message.view;

import java.util.HashMap;
import java.util.Map;

import com.growthbeat.message.model.BannerMessage;
import com.growthbeat.message.model.BannerMessage.BannerType;
import com.growthbeat.message.model.BannerMessage.Position;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;


public class BannerMessageView extends FragmentActivity{
	
	private BannerMessage bannerMessage = null;
	private FrameLayout baseLayout = null;
	private ProgressBar progressBar = null;
	private Context context = null;
	
	Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

	public BannerMessageView(Context context, Object message) {
		
		if (message == null || !(message instanceof BannerMessage))
			return;

		this.bannerMessage = (BannerMessage) message;
		
		this.baseLayout = new FrameLayout(context);
		this.context = context;
		
		progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
		baseLayout.addView(progressBar, new FrameLayout.LayoutParams(100, 100, Gravity.CENTER));
		
		MessageImageDownloader.Callback callback = new MessageImageDownloader.Callback() {
			@Override
			public void success(Map<String, Bitmap> images) {
				cachedImages = images;
				progressBar.setVisibility(View.GONE);
				showOnlyImage(baseLayout);
				showImageText(baseLayout);
				showCloseButton(baseLayout);
			}

			@Override
			public void failure() {
				this.failure();
			}
		};
		MessageImageDownloader messageImageDonwloader = new MessageImageDownloader(this.getSupportLoaderManager(), this,
				bannerMessage, callback);
		messageImageDonwloader.download();
		
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = 100;
		layoutParams.gravity = bannerMessage.getPosition() == Position.top ? Gravity.TOP : Gravity.BOTTOM;
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		layoutParams.format = PixelFormat.TRANSLUCENT;
		
        getWindowsManager().addView(baseLayout, layoutParams);
	}
	
	private WindowManager getWindowsManager() {
		return (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
	}
	
	private void showOnlyImage(FrameLayout innerLayout) {
		
		if (bannerMessage.getBannerType() != BannerType.onlyImage )
			return;

		ImageView imageView = new ImageView(this.context);
		imageView.setScaleType(ScaleType.FIT_CENTER);
		Log.e("", "" + bannerMessage.getPicture().getUrl());
		Log.e("", "" + bannerMessage.getPicture().getWidth());
		imageView.setImageBitmap(cachedImages.get(bannerMessage.getPicture().getUrl()));
		innerLayout.addView(imageView);

	}
	
	private void showImageText(FrameLayout innerLayout) {
		
		if (bannerMessage.getBannerType() != BannerType.imageText )
			return;
		
		Log.w("", "" + bannerMessage.getCaption());
		Log.w("", "" + bannerMessage.getText());
	
	}

	private void showCloseButton(FrameLayout innerLayout) {
		
		if (bannerMessage.getButtons().size() < 2)
			return;
		
	}

//	private List<Button> extractButtons(Button.Type type) {
//
//		List<Button> buttons = new ArrayList<Button>();
//
//		for (Button button : bannerMessage.getButtons()) {
//			if (button.getType() == type) {
//				buttons.add(button);
//			}
//		}
//
//		return buttons;
//
//	}
//
//	private View wrapViewWithAbsoluteLayout(View view, Rect rect) {
//
//		FrameLayout frameLayout = new FrameLayout(context);
//		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(rect.getWidth(), rect.getHeight());
//		layoutParams.setMargins(rect.getLeft(), rect.getTop(), 0, 0);
//		layoutParams.gravity = android.view.Gravity.FILL;
//		frameLayout.setLayoutParams(layoutParams);
//
//		view.setLayoutParams(new ViewGroup.LayoutParams(rect.getWidth(), rect.getHeight()));
//		frameLayout.addView(view);
//
//		return frameLayout;
//
//	}

}
