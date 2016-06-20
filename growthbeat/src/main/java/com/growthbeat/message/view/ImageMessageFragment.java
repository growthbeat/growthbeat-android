package com.growthbeat.message.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.growthbeat.message.model.ImageMessage;
import com.growthbeat.message.model.ScreenButton;

public class ImageMessageFragment extends BaseMessageFragment {

    private  static final int CLOSE_BUTTON_SIZE_MAX =  64;

    private ImageMessage imageMessage = null;

    private Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Object message = getArguments().get("message");
        if (message == null || !(message instanceof ImageMessage))
            return null;

        this.imageMessage = (ImageMessage) message;
        displayMetrics = getResources().getDisplayMetrics();

        int width = (int) (imageMessage.getBaseWidth() * displayMetrics.density);
        int height = (int) (imageMessage.getBaseHeight() * displayMetrics.density);
        int left = (int) ((displayMetrics.widthPixels - width) / 2);
        int top = (int) ((displayMetrics.heightPixels - height) / 2);

        final Rect rect = new Rect(left, top, width, height);

        baseLayout = new FrameLayout(getActivity());
        int color = Color.parseColor(String.format("#%06X", (0xFFFFFF & imageMessage.getBackground().getColor())));
        baseLayout.setBackgroundColor(Color.argb((int)(imageMessage.getBackground().getOpacity() * 255), Color.red(color), Color.green(color), Color.blue(color)));

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(100, 100);
        layoutParams.gravity = Gravity.CENTER;

        if (imageMessage.getBackground().isOutsideClose()) {
            baseLayout.setClickable(true);
            baseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishActivity();
                }
            });
        }
        baseLayout.addView(progressBar, layoutParams);


        MessageImageDownloader.Callback callback = new MessageImageDownloader.Callback() {
            @Override
            public void success(Map<String, Bitmap> images) {
                cachedImages = images;
                progressBar.setVisibility(View.GONE);
                baseLayout.removeView(progressBar);
                showImage(baseLayout, rect);
                showScreenButton(baseLayout, rect);
                showImageButtons(baseLayout, rect);
                showCloseButton(baseLayout, rect);
            }

            @Override
            public void failure() {
                finishActivity();
            }
        };
        MessageImageDownloader messageImageDonwloader = new MessageImageDownloader(getActivity().getSupportLoaderManager(), getActivity(),
            imageMessage, displayMetrics.density, callback);
        messageImageDonwloader.download();

        return baseLayout;

    }

    private void showImage(FrameLayout innerLayout, Rect rect) {

        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ScaleType.FIT_CENTER);
        imageView.setImageBitmap(cachedImages.get(imageMessage.getPicture().getUrl()));
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // this will make sure event is not propagated to others, nesting same view area
                return true;
            }

        });


        innerLayout.addView(wrapViewWithAbsoluteLayout(imageView, rect));

    }

    private void showScreenButton(FrameLayout innerLayout, Rect rect) {

        List<Button> buttons = extractButtons(Button.ButtonType.screen);

        if (buttons.size() < 1)
            return;

        final ScreenButton screenButton = (ScreenButton) buttons.get(0);

        TouchableImageView touchableImageView = new TouchableImageView(getActivity());
        touchableImageView.setScaleType(ScaleType.FIT_CENTER);
        touchableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowthMessage.getInstance().selectButton(screenButton, imageMessage);
                finishActivity();
            }
        });
        touchableImageView.setImageBitmap(cachedImages.get(imageMessage.getPicture().getUrl()));

        innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, rect));

    }

    private void showImageButtons(FrameLayout innerLayout, Rect rect) {

        List<Button> buttons = extractButtons(Button.ButtonType.image);
        Collections.reverse(buttons);

        int top = rect.getTop() + rect.getHeight();
        for (Button button : buttons) {

            final ImageButton imageButton = (ImageButton) button;

            int width = (int) (imageButton.getBaseWidth() * displayMetrics.density);
            int height = (int) (imageButton.getBaseHeight() * displayMetrics.density);
            int left = rect.getLeft() + (rect.getWidth() - width) / 2;
            top -= height;

            TouchableImageView touchableImageView = new TouchableImageView(getActivity());
            touchableImageView.setScaleType(ScaleType.FIT_CENTER);
            touchableImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GrowthMessage.getInstance().selectButton(imageButton, imageMessage);
                    finishActivity();
                }
            });
            touchableImageView.setImageBitmap(cachedImages.get(imageButton.getPicture().getUrl()));

            innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, new Rect(left, top, width, height)));

        }

    }

    private void showCloseButton(FrameLayout innerLayout, Rect rect) {

        List<Button> buttons = extractButtons(Button.ButtonType.close);

        if (buttons.size() < 1)
            return;

        final CloseButton closeButton = (CloseButton) buttons.get(0);
        double availableWidth = Math.min(closeButton.getBaseWidth(), CLOSE_BUTTON_SIZE_MAX);
        double availableHeight  = Math.min(closeButton.getBaseHeight(), CLOSE_BUTTON_SIZE_MAX);
        double ratio = Math.min(availableWidth / closeButton.getBaseWidth(), availableHeight / closeButton.getBaseHeight());


        int width = (int) (closeButton.getPicture().getWidth() * ratio);
        int height = (int) (closeButton.getPicture().getHeight() * ratio);
        int left = rect.getLeft() + rect.getWidth() - width - (int) (8 * displayMetrics.density);
        int top = rect.getTop() + 8 * (int)displayMetrics.density;

        TouchableImageView touchableImageView = new TouchableImageView(getActivity());
        touchableImageView.setScaleType(ScaleType.FIT_CENTER);
        touchableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowthMessage.getInstance().selectButton(closeButton, imageMessage);
                finishActivity();
            }
        });
        touchableImageView.setImageBitmap(cachedImages.get(closeButton.getPicture().getUrl()));

        innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, new Rect(left, top, width, height)));

    }

    private List<Button> extractButtons(Button.ButtonType type) {

        List<Button> buttons = new ArrayList<Button>();

        for (Button button : imageMessage.getButtons()) {
            if (button.getType() == type) {
                buttons.add(button);
            }
        }

        return buttons;

    }

    private View wrapViewWithAbsoluteLayout(View view, Rect rect) {

        FrameLayout frameLayout = new FrameLayout(getActivity());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(rect.getWidth(), rect.getHeight());
        layoutParams.setMargins(rect.getLeft(), rect.getTop(), 0, 0);
        layoutParams.gravity = Gravity.FILL;
        frameLayout.setLayoutParams(layoutParams);

        view.setLayoutParams(new ViewGroup.LayoutParams(rect.getWidth(), rect.getHeight()));
        frameLayout.addView(view);

        return frameLayout;

    }

}
