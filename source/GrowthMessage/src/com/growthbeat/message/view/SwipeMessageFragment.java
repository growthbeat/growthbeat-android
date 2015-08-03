package com.growthbeat.message.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.CloseButton;
import com.growthbeat.message.model.ImageButton;
import com.growthbeat.message.model.Picture;
import com.growthbeat.message.model.SwipeMessage;
import com.growthbeat.message.model.SwipeMessage.SwipeType;

public class SwipeMessageFragment extends Fragment {

	private FrameLayout baseLayout = null;
	private SwipeMessage swipeMessage = null;

	private ProgressBar progressBar = null;

	Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Object message = getArguments().get("message");
		if (message == null || !(message instanceof SwipeMessage))
			return null;

		this.swipeMessage = (SwipeMessage) message;

		baseLayout = new FrameLayout(getActivity());
		baseLayout.setBackgroundColor(Color.argb(128, 0, 0, 0));

		progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(100, 100);
		layoutParams.gravity = Gravity.CENTER;
		baseLayout.addView(progressBar, layoutParams);

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		double imageHeightRate = swipeMessage.getSwipeType().equals(SwipeType.imageOnly) ? 0.90 : 0.80;

		final Rect imageRect = new Rect();
		imageRect.setLeft((int) (displayMetrics.widthPixels * (1 - 0.85) * 0.5));
		imageRect.setTop((int) (displayMetrics.heightPixels * (1 - 0.85) * 0.5));
		imageRect.setWidth((int) (displayMetrics.widthPixels * 0.85));
		imageRect.setHeight((int) (displayMetrics.heightPixels * 0.85 * imageHeightRate));

		final Rect buttonRect = new Rect();
		buttonRect.setLeft(imageRect.getLeft());
		buttonRect.setTop(imageRect.getTop() + imageRect.getHeight());
		buttonRect.setWidth(imageRect.getWidth());
		buttonRect.setHeight((int) (displayMetrics.heightPixels * 0.85 * 0.10));

		final Rect closeRect = new Rect();
		closeRect.setLeft(imageRect.getLeft() + imageRect.getWidth() - (int) (displayMetrics.density * 20 * 0.5));
		closeRect.setTop(imageRect.getTop() - (int) (displayMetrics.density * 20 * 0.5));
		closeRect.setWidth((int) (displayMetrics.density * 20));
		closeRect.setHeight((int) (displayMetrics.density * 20));

		MessageImageDownloader.Callback callback = new MessageImageDownloader.Callback() {
			@Override
			public void success(Map<String, Bitmap> images) {
				cachedImages = images;
				progressBar.setVisibility(View.GONE);

				showPager(baseLayout, imageRect, buttonRect);
				if (swipeMessage.getSwipeType().equals(SwipeType.oneButton))
					showOneButton(baseLayout, buttonRect);
				showCloseButton(baseLayout, closeRect);
			}

			@Override
			public void failure() {
				if (!getActivity().isFinishing())
					getActivity().finish();
			}
		};

		MessageImageDownloader messageImageDonwloader = new MessageImageDownloader(
				getActivity().getSupportLoaderManager(), getActivity(), swipeMessage, callback);
		messageImageDonwloader.download();

		return baseLayout;

	}

	private void showPager(FrameLayout innerLayout, Rect imageRect, Rect buttonRect) {
		SwipePagerAdapter adapter = new SwipePagerAdapter(getActivity());
		List<Picture> pictures = swipeMessage.getPictures();
		List<Button> buttons = extractButtons(EnumSet.of(Button.Type.image));

		int i = 0;
		for (Picture picture : pictures) {
			FrameLayout frameLayout = new FrameLayout(getActivity());
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
			layoutParams.gravity = android.view.Gravity.FILL;
			frameLayout.setLayoutParams(layoutParams);

			frameLayout.addView(createImage(picture, imageRect));

			if (swipeMessage.getSwipeType().equals(SwipeType.buttons) && buttons.size() > i) {
				View buttonView = createButton(buttons.get(i), buttonRect);
				if (buttonView != null)
					frameLayout.addView(buttonView);
			}

			adapter.add(frameLayout);
			i++;
		}

		ViewPager viewPager = new ViewPager(getActivity());
		ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
		layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
		layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
		viewPager.setLayoutParams(layoutParams);
		viewPager.setAdapter(adapter);

		innerLayout.addView(viewPager);
	}

	private void showOneButton(FrameLayout innerLayout, Rect rect) {
		List<Button> buttons = extractButtons(EnumSet.of(Button.Type.image));

		if (buttons.size() != 1)
			return;

		View buttonView = createButton(buttons.get(0), rect);
		if (buttonView != null) {
			innerLayout.addView(buttonView);
		}
	}

	private void showCloseButton(FrameLayout innerLayout, Rect rect) {
		List<Button> buttons = extractButtons(EnumSet.of(Button.Type.close));

		if (buttons.size() < 1)
			return;

		final CloseButton closeButton = (CloseButton) buttons.get(0);

		TouchableImageView touchableImageView = new TouchableImageView(getActivity());
		touchableImageView.setScaleType(ScaleType.CENTER_INSIDE);
		touchableImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GrowthMessage.getInstance().selectButton(closeButton, swipeMessage);
				if (!getActivity().isFinishing())
					getActivity().finish();
			}
		});
		touchableImageView.setImageBitmap(cachedImages.get(closeButton.getPicture().getUrl()));

		innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, rect));
	}

	private View createImage(Picture picture, Rect rect) {
		ImageView imageView = new ImageView(getActivity());
		FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(rect.getWidth(), rect.getHeight());
		imageLayoutParams.leftMargin = rect.getLeft();
		imageLayoutParams.topMargin = rect.getTop();
		imageView.setLayoutParams(imageLayoutParams);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageView.setImageBitmap(cachedImages.get(picture.getUrl()));

		return imageView;
	}

	private View createButton(Button button, Rect rect) {
		switch (button.getType()) {
		case image:
			final ImageButton imageButton = (ImageButton) button;

			TouchableImageView touchableImageView = new TouchableImageView(getActivity());
			touchableImageView.setScaleType(ScaleType.FIT_CENTER);
			touchableImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					GrowthMessage.getInstance().selectButton(imageButton, swipeMessage);
					if (!getActivity().isFinishing())
						getActivity().finish();
				}
			});
			touchableImageView.setImageBitmap(cachedImages.get(imageButton.getPicture().getUrl()));
			return wrapViewWithAbsoluteLayout(touchableImageView, rect);
		default:
			return null;
		}
	}

	private View wrapViewWithAbsoluteLayout(View view, Rect rect) {

		FrameLayout frameLayout = new FrameLayout(getActivity());
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(rect.getWidth(), rect.getHeight());
		layoutParams.setMargins(rect.getLeft(), rect.getTop(), 0, 0);
		layoutParams.gravity = android.view.Gravity.FILL;
		frameLayout.setLayoutParams(layoutParams);

		view.setLayoutParams(new ViewGroup.LayoutParams(rect.getWidth(), rect.getHeight()));
		frameLayout.addView(view);

		return frameLayout;

	}

	private List<Button> extractButtons(EnumSet<Button.Type> types) {

		List<Button> buttons = new ArrayList<Button>();

		for (Button button : swipeMessage.getButtons()) {
			if (types.contains(button.getType())) {
				buttons.add(button);
			}
		}

		return buttons;

	}
}
