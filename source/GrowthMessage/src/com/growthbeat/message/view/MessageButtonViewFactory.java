package com.growthbeat.message.view;

import java.util.Map;

import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.ImageButton;
import com.growthbeat.message.model.PlainButton;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView.ScaleType;

public class MessageButtonViewFactory {
	
	MessageButtonViewFactory() {
	}
	
	public static TouchableImageView getInstance(final Context context, final Button button, Map<String, Bitmap> cachedImages) {
		TouchableImageView touchableImageView = new TouchableImageView(context);
		touchableImageView.setScaleType(ScaleType.FIT_CENTER);
		touchableImageView.setImageBitmap(cachedImages.get(((ImageButton)button).getPicture().getUrl()));
		return touchableImageView;
	}
	
	public static android.widget.Button getInstance(final Context context, final Button button) {
		android.widget.Button plainButtonView = new android.widget.Button(context);
		plainButtonView.setText(((PlainButton)button).getLabel());
		return plainButtonView;
	}
}
