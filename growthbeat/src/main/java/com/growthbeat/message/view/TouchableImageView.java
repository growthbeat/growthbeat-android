package com.growthbeat.message.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class TouchableImageView extends ImageView {

	public TouchableImageView(Context context) {
		super(context);
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				ImageView imageView = (ImageView) view;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					imageView.setColorFilter(Color.argb(128, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
					imageView.invalidate();
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					imageView.getDrawable().clearColorFilter();
					imageView.invalidate();
					break;
				}
				return false;
			}
		});
	}

}
