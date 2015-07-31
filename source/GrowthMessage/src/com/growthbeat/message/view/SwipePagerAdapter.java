package com.growthbeat.message.view;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SwipePagerAdapter extends PagerAdapter {

	private Context context;
	private ArrayList<FrameLayout> itemList;

	public SwipePagerAdapter(Context context) {
		this.context = context;

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
