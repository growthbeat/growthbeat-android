package com.growthbeat.message.view;

import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.growthbeat.utils.BitmapUtilis;

/**
 * Created by tabatakatsutoshi on 2016/06/02.
 */
public class BaseMessageFragment extends Fragment {

    protected FrameLayout baseLayout = null;
    protected ProgressBar progressBar = null;
    protected DisplayMetrics displayMetrics;

    protected void finishActivity() {
        if (getActivity().isFinishing() || baseLayout == null) {
            return;
        }
        BitmapUtilis.unbindRecursively(baseLayout);
        getActivity().finish();
    }


}
