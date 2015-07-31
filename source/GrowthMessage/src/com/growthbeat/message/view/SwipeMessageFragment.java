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
				// if (swipeMessage.getType().equals(SwipeType.oneButton))
				// showOneButton(baseLayout, outerRect, imageOuterRect, (int)
				// buttonHeight);
				// showPanel(baseLayout, outerRect, (int) panelHeight);
				// showCloseButton(baseLayout, outerRect);
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
		int width = (int) (displayMetrics.widthPixels * 0.85);
		int height = (int) (displayMetrics.heightPixels * 0.85);
		int marginLeft = (int) (displayMetrics.widthPixels * (1 - 0.85) * 0.5);
		int marginTop = (int) (displayMetrics.heightPixels * (1 - 0.85) * 0.5);

		SwipePagerAdapter adapter = new SwipePagerAdapter(getActivity());
		List<Picture> pictures = swipeMessage.getPictures();

		for (Picture picture : pictures) {
			FrameLayout frameLayout = new FrameLayout(getActivity());
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
			layoutParams.gravity = android.view.Gravity.FILL;
			frameLayout.setLayoutParams(layoutParams);

			ImageView imageView = new ImageView(getActivity());
			FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(width, height);
			imageLayoutParams.leftMargin = marginLeft;
			imageLayoutParams.topMargin = marginTop;
			imageView.setLayoutParams(imageLayoutParams);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			imageView.setImageBitmap(cachedImages.get(picture.getUrl()));

			frameLayout.addView(imageView);
			adapter.add(frameLayout);
		}

		ViewPager viewPager = new ViewPager(getActivity());
		ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
		layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
		layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
		viewPager.setLayoutParams(layoutParams);
		viewPager.setAdapter(adapter);

		innerLayout.addView(viewPager);
	}

	private void showOneButton(FrameLayout innerLayout, Rect outerRect, Rect imageOuterRect, int buttonHeight) {
		List<Button> buttons = extractButtons(EnumSet.of(Button.Type.image, Button.Type.plain));

		if (buttons.size() != 1)
			return;

		Button button = buttons.get(0);

		Rect buttonRect = new Rect(imageOuterRect.getLeft(), outerRect.getTop() + imageOuterRect.getTop(),
				imageOuterRect.getWidth(), buttonHeight);

		switch (button.getType()) {
		case image:
			final ImageButton imageButton = (ImageButton) button;
			setImageButtonRect(buttonRect, imageButton);

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

			innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, buttonRect));
			break;
		case plain:
			final PlainButton plainButton = (PlainButton) button;

			android.widget.Button plainButtonView = new android.widget.Button(getActivity());
			plainButtonView.setText(plainButton.getLabel());

			plainButtonView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					GrowthMessage.getInstance().selectButton(plainButton, swipeMessage);
					if (!getActivity().isFinishing())
						getActivity().finish();
				}
			});
			innerLayout.addView(wrapViewWithAbsoluteLayout(plainButtonView, buttonRect));
			break;
		default:
			break;
		}
	}

	private void showPanel(FrameLayout innerLayout, Rect rect, int panelHeight) {
		// TODO�@�{�^���ʒu�̕\���@�@swipe��Fragment�Ƒ��Ŏ������ׂ��H
	}

	private void showCloseButton(FrameLayout innerLayout, Rect rect) {

		List<Button> buttons = extractButtons(EnumSet.of(Button.Type.close));

		if (buttons.size() < 1)
			return;

		final CloseButton closeButton = (CloseButton) buttons.get(0);

		int width = closeButton.getPicture().getWidth();
		int height = closeButton.getPicture().getHeight();
		int left = rect.getLeft() + rect.getWidth() - width / 2;
		int top = rect.getTop() - height / 2;

		TouchableImageView touchableImageView = new TouchableImageView(getActivity());
		touchableImageView.setScaleType(ScaleType.FIT_CENTER);
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

	private void setImageButtonRect(Rect rect, ImageButton button) {
		Picture picture = button.getPicture();
		double ratio = Math.min(rect.getWidth() / picture.getWidth(), rect.getHeight() / picture.getHeight());
		rect.setWidth((int) (picture.getWidth() * ratio));
		rect.setHeight((int) (picture.getHeight() * ratio));
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
