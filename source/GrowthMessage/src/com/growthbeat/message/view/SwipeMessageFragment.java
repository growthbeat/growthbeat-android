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
import com.growthbeat.message.model.PlainButton;
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

		MessageImageDownloader.Callback callback = new MessageImageDownloader.Callback() {
			@Override
			public void success(Map<String, Bitmap> images) {
				cachedImages = images;
				progressBar.setVisibility(View.GONE);

				showImages(baseLayout);
				if (swipeMessage.getSwipeType().equals(SwipeType.oneButton))
					showOneButton(baseLayout);
				showCloseButton(baseLayout);
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

	private void showImages(FrameLayout innerLayout) {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		double imageHeightRate = swipeMessage.getSwipeType().equals(SwipeType.imageOnly) ? 0.90 : 0.80;
		int width = (int) (displayMetrics.widthPixels * 0.85);
		int height = (int) (displayMetrics.heightPixels * 0.85 * imageHeightRate);
		int leftMargin = (int) (displayMetrics.widthPixels * (1 - 0.85) * 0.5);
		int topMargin = (int) (displayMetrics.heightPixels * (1 - 0.85) * 0.5);

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

			ImageView imageView = new ImageView(getActivity());
			FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(width, height);
			imageLayoutParams.leftMargin = leftMargin;
			imageLayoutParams.topMargin = topMargin;
			imageView.setLayoutParams(imageLayoutParams);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			imageView.setImageBitmap(cachedImages.get(picture.getUrl()));

			frameLayout.addView(imageView);

			Button button = buttons.get(i);

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

			int buttonWidth = (int) (displayMetrics.widthPixels * 0.85);
			int buttonHeight = (int) (displayMetrics.heightPixels * 0.85 * 0.10);
			int butotnLeftMargin = (int) (displayMetrics.widthPixels * (1 - 0.85) * 0.5);
			int buttonTopMargin = (int) (displayMetrics.heightPixels * (1 - 0.85) * 0.5) + buttonHeight * 8;

			frameLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView,
					new Rect(butotnLeftMargin, buttonTopMargin, buttonWidth, buttonHeight)));

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

	private void showOneButton(FrameLayout innerLayout) {
		List<Button> buttons = extractButtons(EnumSet.of(Button.Type.image));

		if (buttons.size() != 1)
			return;

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int width = (int) (displayMetrics.widthPixels * 0.85);
		int height = (int) (displayMetrics.heightPixels * 0.85 * 0.10);
		int leftMargin = (int) (displayMetrics.widthPixels * (1 - 0.85) * 0.5);
		int topMargin = (int) (displayMetrics.heightPixels * (1 - 0.85) * 0.5) + height * 8;

		Button button = buttons.get(0);

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

			innerLayout.addView(
					wrapViewWithAbsoluteLayout(touchableImageView, new Rect(leftMargin, topMargin, width, height)));
			break;
		default:
			break;
		}
	}

	private void showCloseButton(FrameLayout innerLayout) {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int width = (int) (displayMetrics.widthPixels * 0.10);
		int height = (int) (displayMetrics.heightPixels * 0.10);
		int left = (int) (displayMetrics.widthPixels * 0.90);
		int top = (int) (displayMetrics.heightPixels * 0.01);

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

		innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, new Rect(left, top, width, height)));
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
