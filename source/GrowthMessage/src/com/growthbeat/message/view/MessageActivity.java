package com.growthbeat.message.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.growthbeat.message.model.Message;

public class MessageActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme(android.R.style.Theme_Translucent);

		Message message = (Message) getIntent().getExtras().get("message");
		Bundle bundle = new Bundle();
		bundle.putParcelable("message", (Parcelable) message);

		switch (message.getType()) {
		case plain:
			PlainMessageFragment plainMessageFragment = new PlainMessageFragment();
			plainMessageFragment.setCancelable(false);
			plainMessageFragment.setArguments(bundle);
			plainMessageFragment.show(getSupportFragmentManager(), getClass().getName());
			break;
		case image:
			ImageMessageFragment imageMessageFragment = new ImageMessageFragment();
			imageMessageFragment.setArguments(bundle);
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content, imageMessageFragment).commitAllowingStateLoss();
			break;
		case banner:
			break;
		default:
			break;
		}

	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
