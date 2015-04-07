package com.growthbeat.growthbeatsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.growthbeat.Growthbeat;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Growthbeat.getInstance().initialize(this, "P5C3vzoLOEijnlVj", "btFlFAitBJ1CBdL3IR3ROnhLYbeqmLlY");
		Growthbeat.getInstance().initializeGrowthAnalytics();
		Growthbeat.getInstance().initializeGrowthMessage();
	}

	@Override
	public void onStart() {
		super.onStart();
		Growthbeat.getInstance().start();
	}

	@Override
	public void onStop() {
		super.onStop();
		Growthbeat.getInstance().stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
