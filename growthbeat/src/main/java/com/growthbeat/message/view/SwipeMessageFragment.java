package com.growthbeat.message.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
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
import com.growthbeat.message.model.ImageMessage;
import com.growthbeat.message.model.Picture;
import com.growthbeat.message.model.SwipeMessage;
import com.growthbeat.message.model.SwipeMessage.SwipeType;

public class SwipeMessageFragment extends Fragment {

    private static final int CLOSE_BUTTON_SIZE_MAX =  64;
    private static final int PAGING_HEIGHT = 16;

    private FrameLayout baseLayout = null;
    private SwipeMessage swipeMessage = null;

    private ProgressBar progressBar = null;
    private DisplayMetrics displayMetrics;
    private ViewPager viewPager = null;

    private Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Object message = getArguments().get("message");
        if (message == null || !(message instanceof SwipeMessage))
            return null;

        displayMetrics = getResources().getDisplayMetrics();

        this.swipeMessage = (SwipeMessage) message;

        baseLayout = new FrameLayout(getActivity());

        int color = Color.parseColor(String.format("#%06X", (0xFFFFFF & swipeMessage.getBackground().getColor())));
        baseLayout.setBackgroundColor(Color.argb((int)(swipeMessage.getBackground().getOpacity() * 255), Color.red(color), Color.green(color), Color.blue(color)));

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(100, 100);
        layoutParams.gravity = Gravity.CENTER;
        if (swipeMessage.getBackground().isOutsideClose()) {
            baseLayout.setClickable(true);
            baseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!getActivity().isFinishing())
                        getActivity().finish();
                }
            });
        }
        baseLayout.addView(progressBar, layoutParams);

        double imageHeightRate = swipeMessage.getSwipeType().equals(SwipeType.imageOnly) ? 0.90 : 0.80;

        final Rect imageRect = new Rect();
        imageRect.setLeft((int) ((displayMetrics.widthPixels - swipeMessage.getBaseWidth() * displayMetrics.density) * 0.5));
        imageRect.setTop((int) ((displayMetrics.heightPixels - swipeMessage.getBaseHeight() * displayMetrics.density) * 0.5));
        imageRect.setWidth((int) (swipeMessage.getBaseWidth() * displayMetrics.density));
        imageRect.setHeight((int) (swipeMessage.getBaseHeight() * displayMetrics.density));



        final Rect indicatorRect = new Rect();
        indicatorRect.setLeft(imageRect.getLeft());
        indicatorRect.setTop(imageRect.getTop() + imageRect.getHeight());
        indicatorRect.setWidth(imageRect.getWidth());
        indicatorRect.setHeight((int)(PAGING_HEIGHT * displayMetrics.density));

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

                showPager(baseLayout, imageRect);
                if (swipeMessage.getSwipeType().equals(SwipeType.oneButton))
                    showOneButton(baseLayout, imageRect);
                showIndicator(baseLayout, indicatorRect);
                showCloseButton(baseLayout, imageRect);
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

    private void showPager(FrameLayout innerLayout, Rect imageRect) {
        SwipePagerAdapter adapter = new SwipePagerAdapter();
        List<Picture> pictures = swipeMessage.getSwipeImages().getPictures();
        List<Button> buttons = extractButtons(EnumSet.of(Button.Type.image));

        int i = 0;
        for (Picture picture : pictures) {
            FrameLayout frameLayout = new FrameLayout(getActivity());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.FILL;
            frameLayout.setLayoutParams(layoutParams);

            frameLayout.addView(createImage(picture, imageRect));

            adapter.add(frameLayout);
            i++;
        }

        viewPager = new ViewPager(getActivity());
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
        ImageButton imageButton = (ImageButton)buttons.get(0);
        double availableWidth = Math.min(imageButton.getBaseWidth() * displayMetrics.density, rect.getWidth());
        double ratio =  Math.min(availableWidth / imageButton.getBaseWidth(), 1);


        int width = (int) (imageButton.getBaseWidth() * displayMetrics.density * ratio);
        int height = (int) (imageButton.getBaseHeight() * displayMetrics.density * ratio);
        int left = rect.getLeft() + (rect.getWidth() - width) / 2;
        int top = rect.getTop() + rect.getHeight() - (int) (PAGING_HEIGHT * displayMetrics.density ) - height;
        final Rect buttonRect = new Rect();
        buttonRect.setLeft(left);
        buttonRect.setTop(top);
        buttonRect.setWidth(width);
        buttonRect.setHeight(height);

        View buttonView = createButton(imageButton, buttonRect);
        if (buttonView != null) {
            innerLayout.addView(buttonView);
        }
    }

    private void showIndicator(FrameLayout innerLayout, Rect rect) {
        SwipePagerIndicator swipePagerIndicator = new SwipePagerIndicator();
        swipePagerIndicator.setViewPager(viewPager);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(rect.getWidth(), rect.getHeight());
        layoutParams2.leftMargin = rect.getLeft();
        layoutParams2.topMargin = rect.getTop();
        swipePagerIndicator.setLayoutParams(layoutParams2);
        innerLayout.addView(swipePagerIndicator);
    }

    private void showCloseButton(FrameLayout innerLayout, Rect rect) {
        List<Button> buttons = extractButtons(EnumSet.of(Button.Type.close));

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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        touchableImageView.setLayoutParams(layoutParams);
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

        innerLayout.addView(touchableImageView);
    }

    private View createImage(Picture picture, Rect rect) {
        ImageView imageView = new ImageView(getActivity());
        FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(rect.getWidth(), rect.getHeight());
        imageLayoutParams.leftMargin = rect.getLeft();
        imageLayoutParams.topMargin = rect.getTop();
        imageView.setLayoutParams(imageLayoutParams);
        imageView.setScaleType(ScaleType.FIT_CENTER);
        imageView.setImageBitmap(cachedImages.get(picture.getUrl()));

        return imageView;
    }

    private View createButton(Button button, Rect rect) {
        switch (button.getType()) {
            case image:
                final ImageButton imageButton = (ImageButton) button;

                TouchableImageView touchableImageView = new TouchableImageView(getActivity());
                FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(rect.getWidth(), rect.getHeight());
                imageLayoutParams.leftMargin = rect.getLeft();
                imageLayoutParams.topMargin = rect.getTop();
                touchableImageView.setLayoutParams(imageLayoutParams);
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
                return touchableImageView;
            default:
                return null;
        }
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

    private class SwipePagerAdapter extends PagerAdapter {
        private ArrayList<FrameLayout> itemList;

        public SwipePagerAdapter() {
            itemList = new ArrayList<FrameLayout>();
        }

        public void add(FrameLayout layout) {
            itemList.add(layout);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FrameLayout layout = itemList.get(position);
            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (FrameLayout) object;
        }
    }

    private class SwipePagerIndicator extends View {
        private static final float DISTANCE = 16.0f;
        private static final float RADIUS = 4.0f;

        private ViewPager viewPager;
        private int position;
        private Paint paint;

        public SwipePagerIndicator() {
            super(getActivity());

            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
        }

        public void setViewPager(ViewPager viewPager) {
            this.viewPager = viewPager;

            this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    setPosition(position);
                    invalidate();
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        private void setPosition(int position) {
            this.position = position;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (viewPager == null) {
                return;
            }

            final int count = viewPager.getAdapter().getCount();
            final float longOffset = (getWidth() * 0.5f) + (DISTANCE * displayMetrics.density * 0.5f) - (count * DISTANCE * displayMetrics.density * 0.5f);
            final float shortOffset = getHeight() * 0.5f;

            for (int i = 0; i < count; i++) {
                if (position == i) {
                    paint.setColor(Color.WHITE);
                } else {
                    paint.setColor(Color.DKGRAY);
                }
                float cx = longOffset + (i * DISTANCE* displayMetrics.density);
                float cy = shortOffset;
                canvas.drawCircle(cx, cy, RADIUS* displayMetrics.density, paint);
            }
        }
    }
}
