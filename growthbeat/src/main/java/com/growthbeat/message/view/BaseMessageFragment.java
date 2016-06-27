package com.growthbeat.message.view;

import java.util.HashMap;
import java.util.Map;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.MessageImageDownloader;
import com.growthbeat.message.handler.ShowMessageHandler;
import com.growthbeat.message.model.Background;
import com.growthbeat.message.model.Message;
import com.growthbeat.utils.BitmapUtilis;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by tabatakatsutoshi on 2016/06/02.
 */
public class BaseMessageFragment extends Fragment {

	protected FrameLayout baseLayout = null;
	protected DisplayMetrics displayMetrics;

	protected Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

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

	protected void layoutMessage(final Message message, final String uuid,
			final ShowMessageHandler.MessageRenderHandler messageRenderHandler) {

		MessageImageDownloader.Callback callback = new MessageImageDownloader.Callback() {

			@Override
			public void success(Map<String, Bitmap> images) {
				cachedImages = images;
				ShowMessageHandler showMessageHandler = GrowthMessage.getInstance().findShowMessageHandler(uuid);

				if (showMessageHandler != null) {
					showMessageHandler.complete(messageRenderHandler);
				} else {
					messageRenderHandler.render();
				}
			}

			@Override
			public void failure() {
				finishActivity();
			}
		};

		MessageImageDownloader messageImageDonwloader = new MessageImageDownloader(getActivity().getSupportLoaderManager(), getActivity(),
				message, displayMetrics.density, callback);
		messageImageDonwloader.download();

	}

	protected void finishActivity() {
		if ((getActivity() != null && getActivity().isFinishing()) || baseLayout == null) {
			return;
		}
		BitmapUtilis.unbindRecursively(baseLayout);
		getActivity().finish();
	}

}
