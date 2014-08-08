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
		Growthbeat.initialize(this.getApplicationContext(), "dy6VlRMnN3juhW9L", "NuvkVhQtRDG2nrNeDzHXzZO5c6j0Xu5t");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
