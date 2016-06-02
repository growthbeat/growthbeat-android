package com.growthbeat.message.view;

import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.growthbeat.utils.BitmapUtilis;

/**
 * Created by tabatakatsutoshi on 2016/06/02.
 */
public class BaseImageMessageFragment extends Fragment {

    protected FrameLayout baseLayout = null;

    protected void finishActivity() {
        if (getActivity().isFinishing() || baseLayout == null) {
            return;
        }
        BitmapUtilis.unbindRecursively(baseLayout);
    }
}
