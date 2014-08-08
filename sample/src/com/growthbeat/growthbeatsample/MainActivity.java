package com.growthbeat.growthbeatsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.growthbeat.Growthbeat;
import com.growthpush.GrowthPush;
import com.growthpush.model.Environment;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Growthbeat.getInstance().initialize(this.getApplicationContext(), "dy6VlRMnN3juhW9L", "NuvkVhQtRDG2nrNeDzHXzZO5c6j0Xu5t");
		Growthbeat.getInstance().initializeGrowthPush(BuildConfig.DEBUG ? Environment.development : Environment.production, "955057365401");
		Growthbeat.getInstance().initializeGrowthReplay();
		GrowthPush.getInstance().setDeviceTags();
		GrowthPush.getInstance().trackEvent("Launch");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
