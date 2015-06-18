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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

		double baseWidth = displayMetrics.widthPixels * 0.85;
		double baseHeight = displayMetrics.heightPixels * 0.85;
		final double buttonHeight = displayMetrics.heightPixels * 0.12;
		final double panelHeight = displayMetrics.heightPixels * 0.7;
		final double marginLeft = (displayMetrics.widthPixels - baseWidth) / 2;

		double maxPictureHeight = baseHeight;

		final List<Rect> imageRects = new ArrayList<Rect>(swipeMessage.getPictures().size());
		for(Picture picture : swipeMessage.getPictures()) {
			double availableWidth = Math.min(picture.getWidth(), baseWidth);
			double availableHeight = Math.min(picture.getHeight(), baseHeight - buttonHeight - panelHeight);
			double ratio = Math.min(availableWidth / picture.getWidth(), availableHeight / picture.getHeight());
			int width = (int)(picture.getWidth() * ratio);
			int height = (int)(picture.getHeight() * ratio);
			int left = (int)((baseWidth - width) / 2);
			imageRects.add(new Rect(left, 0, width, height));
			maxPictureHeight = Math.max(maxPictureHeight, height);
		}
		for(Rect rect : imageRects)
			if(rect.getHeight() < maxPictureHeight)
				rect.setTop((int)((maxPictureHeight - rect.getHeight()) / 2));

		int outerRectHeight = (int)(maxPictureHeight + buttonHeight + panelHeight);
		int outerRectTop = (int)((displayMetrics.heightPixels - outerRectHeight) / 2);
		int imageOuterRectHeight = swipeMessage.getSwipeType().equals(SwipeType.buttons) 
				? (int)(maxPictureHeight + buttonHeight) : (int)maxPictureHeight;

		final Rect imageOuterRect = new Rect((int)marginLeft, outerRectTop, (int)baseWidth, imageOuterRectHeight);
		final Rect outerRect = new Rect((int)marginLeft, outerRectTop, (int)baseWidth, outerRectHeight);

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

				showImages(baseLayout, imageOuterRect, imageRects, (int)buttonHeight);
				if(swipeMessage.getType().equals(SwipeType.oneButton))
					showOneButton(baseLayout, outerRect, imageOuterRect, (int)buttonHeight);
				showPanel(baseLayout, outerRect, (int)panelHeight);
				showCloseButton(baseLayout, outerRect);
			}

			@Override
			public void failure() {
				if (!getActivity().isFinishing())
					getActivity().finish();
			}
		};

		MessageImageDownloader messageImageDonwloader = new MessageImageDownloader(getActivity().getSupportLoaderManager(), getActivity(),
				swipeMessage, callback);
		messageImageDonwloader.download();

		return baseLayout;

	}

	private void showImages(FrameLayout innerLayout, Rect outerRect, List<Rect> imageRects, int buttonHeight) {
		// TODO swipeの挙動。 SwipeType.buttons の場合は、ボタンも表示

	}
	
	private void showOneButton(FrameLayout innerLayout, Rect outerRect, Rect imageOuterRect, int buttonHeight) {
		List<Button> buttons = extractButtons(EnumSet.of(Button.Type.image, Button.Type.plain));

		if (buttons.size() != 1)
			return;
		
		final Button button = buttons.get(0);
		
		Rect buttonRect = new Rect(imageOuterRect.getLeft(), 
				outerRect.getTop() + imageOuterRect.getTop(), imageOuterRect.getWidth(), buttonHeight);

		View buttonView = null;
		switch(button.getType()) {
		case image:
			setImageButtonRect(buttonRect, (ImageButton)button);
			buttonView = MessageButtonViewFactory.getInstance(getActivity(), button, cachedImages);
			break;
		case plain:
			buttonView = MessageButtonViewFactory.getInstance(getActivity(), button);
			break;
		default:
			break;
		}
		buttonView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GrowthMessage.getInstance().selectButton(button, swipeMessage);
				if (!getActivity().isFinishing())
					getActivity().finish();
			}
		});

		innerLayout.addView(wrapViewWithAbsoluteLayout(buttonView, buttonRect));
	}

	private void showPanel(FrameLayout innerLayout, Rect rect, int panelHeight) {
		// TODO　ボタン位置の表示　　swipeのFragmentと側で実装すべき？
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
		rect.setWidth((int)(picture.getWidth() * ratio));
		rect.setHeight((int)(picture.getHeight() * ratio));
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
