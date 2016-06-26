package com.growthbeat.message.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.handler.ShowMessageHandler;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.CardMessage;
import com.growthbeat.message.model.CloseButton;
import com.growthbeat.message.model.ImageButton;
import com.growthbeat.message.model.ScreenButton;
import com.growthbeat.message.model.Task;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class CardMessageFragment extends BaseMessageFragment {

    private CardMessage cardMessage = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Object message = getArguments().get("message");
        if (message == null || !(message instanceof CardMessage))
            return null;

        final String uuid = getArguments().getString("uuid");
        this.cardMessage = (CardMessage) message;
        this.baseLayout = generateBaselayout(cardMessage.getBackground());

        layoutMessage(cardMessage, uuid, new ShowMessageHandler.MessageRenderHandler() {
            @Override
            public void render() {
                renderMessage();
            }
        });

        return baseLayout;

    }

    private void renderMessage() {

        int width = (int)((cardMessage.getTask().getOrientation() == Task.Orientation.vertical ? cardMessage.getBaseWidth() : cardMessage.getBaseHeight()) * displayMetrics.density);
        int height = (int)((cardMessage.getTask().getOrientation() == Task.Orientation.vertical ? cardMessage.getBaseHeight() : cardMessage.getBaseWidth()) * displayMetrics.density);
        int left = (displayMetrics.widthPixels - width) / 2;
        int top = (displayMetrics.heightPixels - height) / 2;
        Rect rect = new Rect(left, top, width, height);

        showImage(baseLayout, rect);
        showScreenButton(baseLayout, rect);
        showImageButtons(baseLayout, rect);
        showCloseButton(baseLayout, rect);
    }

    private void showImage(FrameLayout innerLayout, Rect rect) {

        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ScaleType.FIT_CENTER);
        imageView.setImageBitmap(cachedImages.get(cardMessage.getPicture().getUrl()));
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // this will make sure event is not propagated to others,
                // nesting same view area
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
                GrowthMessage.getInstance().selectButton(screenButton, cardMessage);
                finishActivity();
            }
        });
        touchableImageView.setImageBitmap(cachedImages.get(cardMessage.getPicture().getUrl()));

        innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, rect));

    }

    private void showImageButtons(FrameLayout innerLayout, Rect rect) {

        List<Button> buttons = extractButtons(Button.ButtonType.image);
        Collections.reverse(buttons);

        int top = rect.getTop() + rect.getHeight();
        for (Button button : buttons) {

            final ImageButton imageButton = (ImageButton) button;

            int width = (int) ((cardMessage.getTask().getOrientation() == Task.Orientation.vertical ? cardMessage.getBaseWidth() : cardMessage.getBaseHeight()) * displayMetrics.density);
            int height = (int) (imageButton.getBaseHeight() * displayMetrics.density);
            int left = rect.getLeft() + (rect.getWidth() - width) / 2;
            top -= height;

            TouchableImageView touchableImageView = new TouchableImageView(getActivity());
            touchableImageView.setScaleType(ScaleType.FIT_CENTER);
            touchableImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GrowthMessage.getInstance().selectButton(imageButton, cardMessage);
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

        int width = (int) (closeButton.getBaseWidth() * displayMetrics.density);
        int height = (int) (closeButton.getBaseHeight() * displayMetrics.density);
        int left = rect.getLeft() + rect.getWidth() - width - (int) (8 * displayMetrics.density);
        int top = rect.getTop() + 8 * (int) displayMetrics.density;

        TouchableImageView touchableImageView = new TouchableImageView(getActivity());
        touchableImageView.setScaleType(ScaleType.FIT_CENTER);
        touchableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowthMessage.getInstance().selectButton(closeButton, cardMessage);
                finishActivity();
            }
        });
        touchableImageView.setImageBitmap(cachedImages.get(closeButton.getPicture().getUrl()));

        innerLayout.addView(wrapViewWithAbsoluteLayout(touchableImageView, new Rect(left, top, width, height)));

    }

    private List<Button> extractButtons(Button.ButtonType type) {

        List<Button> buttons = new ArrayList<Button>();

        for (Button button : cardMessage.getButtons()) {
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
