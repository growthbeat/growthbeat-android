package com.growthbeat.utils;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

public class BitmapUtilis {

	public static boolean usingMemoryCache = false;

	public static void unbindImageView(ImageView imageView) {
		if (imageView == null) {
			return;
		}

		if (imageView.getBackground() != null) {
			imageView.getBackground().setCallback(null);
		}

		if (imageView.getDrawable() == null)
			return;
		if (!(imageView.getDrawable() instanceof BitmapDrawable))
			return;
		if (((BitmapDrawable) imageView.getDrawable()).getBitmap() == null)
			return;

		BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
		try {
			if (drawable != null && imageView.getTag() != null && !drawable.getBitmap().isRecycled()) {
				if (!imageView.getTag().toString().equalsIgnoreCase("resource") && !usingMemoryCache) {
					drawable.getBitmap().recycle();
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		drawable.setCallback(null);
		imageView.setImageBitmap(null);
		imageView.setImageDrawable(null);
	}

	public static void unbindRecursively(View view) {
		if (view == null)
			return;

		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
			setBackgroundDrawable(view, null);
		}

		if (view instanceof Button) {
			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}
			setBackgroundDrawable(view, null);
		}

		if (view instanceof ImageView) {
			unbindImageView((ImageView) view);
			setBackgroundDrawable(view, null);
		}

		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				unbindRecursively(viewGroup.getChildAt(i));
			}
			if (viewGroup instanceof AdapterView) {
				((AdapterView) viewGroup).setAdapter(null);
			} else {
				viewGroup.removeAllViews();
			}
		}
		view.destroyDrawingCache();
		view = null;
	}

	private static void setBackgroundDrawable(View view, Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(drawable);
		} else {
			view.setBackgroundDrawable(drawable);
		}
	}

}
