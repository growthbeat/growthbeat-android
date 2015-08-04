package com.growthbeat.message.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.view.View;

public class SwipePagerIndicator extends View {
	private static final float DISTANCE = 50.0f;
	private static final float RADIUS = 10.0f;

	private ViewPager viewPager;
	private int position;
	private Paint paint;

	public SwipePagerIndicator(Context context) {
		super(context);

		paint = new Paint();
		paint.setStrokeWidth(1);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAntiAlias(true);
	}

	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;

		this.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
		final float longOffset = (getWidth() * 0.5f) + (DISTANCE * 0.5f) - (count * DISTANCE * 0.5f);
		final float shortOffset = getHeight() * 0.5f;

		for (int i = 0; i < count; i++) {
			if (position == i) {
				paint.setColor(Color.WHITE);
			} else {
				paint.setColor(Color.DKGRAY);
			}
			float cx = longOffset + (i * DISTANCE);
			float cy = shortOffset;
			canvas.drawCircle(cx, cy, RADIUS, paint);
		}
	}
}
