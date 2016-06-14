package com.growthbeat.message.view;

import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.CloseButton;
import com.growthbeat.message.model.ImageButton;
import com.growthbeat.message.model.ImageMessage;
import com.growthbeat.message.model.ScreenButton;
import com.growthbeat.message.model.Task;

public class ImageMessageFragment extends Fragment {

    private FrameLayout baseLayout = null;
    private ImageMessage imageMessage = null;

    private ProgressBar progressBar = null;
    private int defaultWidth = 280;
    private int defaultHeight = 448;
    private int imageButtonWidthMax = 280;
    private int imageButtonHeightMax = 48;
    private int closeButtonSizeMax = 64;

    Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Object message = getArguments().get("message");
        if (message == null || !(message instanceof ImageMessage))
            return null;

        this.imageMessage = (ImageMessage) message;

        if (imageMessage.getTask().getOrientation() == Task.Orientation.horizontal) {
            defaultWidth = 448;
            defaultHeight = 280;
            imageButtonWidthMax = 448;
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        double availableWidth = Math.min(imageMessage.getPicture().getWidth() * displayMetrics.density, defaultWidth * displayMetrics.density);
        double availableHeight = Math.min(imageMessage.getPicture().getHeight() * displayMetrics.density,
            defaultHeight * displayMetrics.density);

        final double ratio = Math.min(availableWidth / imageMessage.getPicture().getWidth(), availableHeight
            / imageMessage.getPicture().getHeight());

        int width = (int) (imageMessage.getPicture().getWidth() * ratio);
        int height = (int) (imageMessage.getPicture().getHeight() * ratio);
        int left = (int) ((displayMetrics.widthPixels - width) / 2);
        int top = (int) ((displayMetrics.heightPixels - height) / 2);

        final Rect rect = new Rect(left, top, width, height);

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
                showImage(baseLayout, rect);
                showScreenButton(baseLayout, rect);
                showImageButtons(baseLayout, rect, ratio);
                showCloseButton(baseLayout, rect, ratio);
            }

            @Override
            public void failure() {
                if (!getActivity().isFinishing())
                    getActivity().finish();
            }
        };
        MessageImageDownloader messageImageDonwloader = new MessageImageDownloader(getActivity().getSupportLoaderManager(), getActivity(),
            imageMessage, callback);
        messageImageDonwloader.download();

        return baseLayout;

    }

    private void showImage(FrameLayout innerLayout, Rect rect) {

        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ScaleType.FIT_CENTER);
        imageView.setImageBitmap(cachedImages.get(imageMessage.getPicture().getUrl()));

        innerLayout.addView(wrapViewWithAbsoluteLayout(imageView, rect));

    }

    private void showScreenButton(FrameLayout innerLayout, Rect rect) {

        List<Button> buttons = extractButtons(Button.Type.screen);

        if (buttons.size() < 1)
            return;

        final ScreenButton screenButton = (ScreenButton) buttons.get(0);

        TouchableImageView touchableImageView = new TouchableImageView(getActivity());
        touchableImageView.setScaleType(ScaleType.FIT_CENTER);
        touchableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowthMessage.getInstance().selectButton(screenButton, imageMessage);
                if (!getActivity().isFinishing())
                    getActivity().finish();
            }
        });
        touchableImageView.setImageBitmap(cachedImages.get(imageMessage.getPicture().getUrl()));

        innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, rect));

    }

    private void showImageButtons(FrameLayout innerLayout, Rect rect, double ratio) {

        List<Button> buttons = extractButtons(Button.Type.image);
        Collections.reverse(buttons);

        int top = rect.getTop() + rect.getHeight();
        for (Button button : buttons) {

            final ImageButton imageButton = (ImageButton) button;
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            double availableWidth = Math.min(imageButton.getPicture().getWidth() * ratio, imageButtonWidthMax * displayMetrics.density);
            double availableHeight = Math.min(imageButton.getPicture().getHeight() * ratio,
                imageButtonHeightMax * displayMetrics.density);
            int width = (int) availableWidth;
            int height = (int) availableHeight;
            int left = rect.getLeft() + (rect.getWidth() - width) / 2;
            top -= height;

            TouchableImageView touchableImageView = new TouchableImageView(getActivity());
            touchableImageView.setScaleType(ScaleType.FIT_CENTER);
            touchableImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GrowthMessage.getInstance().selectButton(imageButton, imageMessage);
                    if (!getActivity().isFinishing())
                        getActivity().finish();
                }
            });
            touchableImageView.setImageBitmap(cachedImages.get(imageButton.getPicture().getUrl()));

            innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, new Rect(left, top, width, height)));

        }

    }

    private void showCloseButton(FrameLayout innerLayout, Rect rect, double ratio) {

        List<Button> buttons = extractButtons(Button.Type.close);

        if (buttons.size() < 1)
            return;

        final CloseButton closeButton = (CloseButton) buttons.get(0);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        double availableWidth = Math.min(closeButton.getPicture().getWidth() * ratio, closeButtonSizeMax * displayMetrics.density);
        double availableHeight = Math.min(closeButton.getPicture().getHeight() * ratio,
            closeButtonSizeMax * displayMetrics.density);
        int width = (int) availableWidth;
        int height = (int) availableHeight;
        int left = rect.getLeft() + rect.getWidth() - width - 8 * (int)displayMetrics.density;
        int top = rect.getTop() + 8 * (int)displayMetrics.density;

        TouchableImageView touchableImageView = new TouchableImageView(getActivity());
        touchableImageView.setScaleType(ScaleType.FIT_CENTER);
        touchableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowthMessage.getInstance().selectButton(closeButton, imageMessage);
                if (!getActivity().isFinishing())
                    getActivity().finish();
            }
        });
        touchableImageView.setImageBitmap(cachedImages.get(closeButton.getPicture().getUrl()));

        innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, new Rect(left, top, width, height)));

    }

    private List<Button> extractButtons(Button.Type type) {

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
