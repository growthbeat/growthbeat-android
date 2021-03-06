package com.growthbeat.message.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.model.Background;
import com.growthbeat.utils.BitmapUtilis;

/**
 * Created by tabatakatsutoshi on 2016/06/02.
 */
public class BaseImageMessageFragment extends Fragment {

    protected static final int BASE_CLOSE_PADDING = 8;

    protected FrameLayout baseLayout = null;
    protected DisplayMetrics displayMetrics;

    protected FrameLayout generateBaseLayout(Background background) {

        displayMetrics = getResources().getDisplayMetrics();

        FrameLayout baseLayout = new FrameLayout(getActivity().getApplicationContext());
        int color = Color.parseColor(String.format("#%06X", (0xFFFFFF & background.getColor())));
        baseLayout.setBackgroundColor(Color.argb((int) (background.getOpacity() * 255), Color.red(color), Color.green(color),
            Color.blue(color)));

        if (background.isOutsideClose()) {
            baseLayout.setClickable(true);
            baseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishActivity();
                }
            });
        }

        return baseLayout;

    }

    protected Bitmap getImageResource(String urlKey) {
        return GrowthMessage.getInstance().getMessageImageCacheManager().get(urlKey);
    }

    protected void finishActivity() {
        if ((getActivity() != null && getActivity().isFinishing()) || baseLayout == null) {
            return;
        }
        BitmapUtilis.unbindRecursively(baseLayout);
        getActivity().finish();
    }

}
