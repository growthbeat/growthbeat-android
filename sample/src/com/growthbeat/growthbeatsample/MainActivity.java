package com.growthbeat.growthbeatsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.growthbeat.Growthbeat;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthpush.model.Environment;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Growthbeat.getInstance().initialize(getApplicationContext(), "OyVa3zboPjHVjsDC", "3EKydeJ0imxJ5WqS22FJfdVamFLgu7XA");
		Growthbeat.getInstance().initializeGrowthPush(BuildConfig.DEBUG ? Environment.development : Environment.production, "955057365401");
		Growthbeat.getInstance().initializeGrowthReplay();
	}

	@Override
	public void onStart() {

		super.onStart();

		GrowthAnalytics.getInstance().open();
		GrowthAnalytics.getInstance().setBasicTags();

	}

	@Override
	public void onStop() {
		super.onStop();
		GrowthAnalytics.getInstance().close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
