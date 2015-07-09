package com.growthbeat.growthbeatsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.growthbeat.Growthbeat;
import com.growthpush.GrowthPush;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.message.GrowthMessage;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		GrowthMessage.getInstance().getHttpClient().setBaseUrl("http://api.stg.message.growthbeat.com");
		Growthbeat.getInstance().initialize(this, "P5C3vzoLOEijnlVj", "btFlFAitBJ1CBdL3IR3ROnhLYbeqmLlY", BuildConfig.DEBUG);
		GrowthPush.getInstance().requestRegistrationId("955057365401");

		findViewById(R.id.random_tag_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GrowthAnalytics.getInstance().setRandom();
			}
		});

		findViewById(R.id.development_tag_check_box).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				GrowthAnalytics.getInstance().setDevelopment(checkBox.isChecked());
			}
		});

		findViewById(R.id.level_tag_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText editText = (EditText) findViewById(R.id.level_edit_text);
					GrowthAnalytics.getInstance().setLevel(Integer.valueOf(editText.getText().toString()));
				} catch (NumberFormatException e) {
					Log.w("Grwothbeat Sample", "Input value error :" + e.getMessage());
				}
			}
		});

		findViewById(R.id.purchase_event_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText priceEditText = (EditText) findViewById(R.id.price_edit_text);
					EditText productEditText = (EditText) findViewById(R.id.product_edit_text);
					GrowthAnalytics.getInstance().purchase(Integer.valueOf(priceEditText.getText().toString()), "item",
							productEditText.getText().toString());
				} catch (NumberFormatException e) {
					Log.w("Grwothbeat Sample", "Input value error :" + e.getMessage());
				}
			}
		});

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
