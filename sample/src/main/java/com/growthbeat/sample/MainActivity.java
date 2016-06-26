package com.growthbeat.sample;

import com.growthbeat.Growthbeat;
import com.growthbeat.link.GrowthLink;
import com.growthbeat.message.handler.ShowMessageHandler;
import com.growthbeat.model.Client;
import com.growthpush.GrowthPush;
import com.growthpush.model.Environment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		GrowthPush.getInstance().getHttpClient().setBaseUrl("https://api.stg.growthpush.com/");
		GrowthPush.getInstance().initialize(this, "PIaD6TaVt7wvKwao",  "RtYOQo4QaSaFHNYdZSddSeoeEiJ2kboW",
				BuildConfig.DEBUG ? Environment.development : Environment.production);
		GrowthLink.getInstance().initialize(this, "PIaD6TaVt7wvKwao", "FD2w93wXcWlb68ILOObsKz5P3af9oVMo");
		GrowthPush.getInstance().requestRegistrationId("186415479559");
		GrowthLink.getInstance().handleOpenUrl(getIntent().getData());

		GrowthPush.getInstance().trackEvent("Launch");

		new Thread(new Runnable() {
			@Override
			public void run() {
				Client client = Growthbeat.getInstance().waitClient();
				Log.d("GrowthbeatSample", String.format("clientId is %s", client.getId()));
			}
		}).start();

		GrowthPush.getInstance().trackEvent("ReceiveMessage", null, new ShowMessageHandler() {
			@Override
			public void complete(MessageRenderHandler renderHandler) {
				renderHandler.render();
			}

			@Override
			public void onError() {

			}
		});

		GrowthPush.getInstance().setTag("tag1", "TAG");
		GrowthPush.getInstance().trackEvent("AllowPushPermission");

		findViewById(R.id.random_tag_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: add message2.0 event
				// GrowthAnalytics.getInstance().setRandom();
			}
		});

		findViewById(R.id.development_tag_check_box).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				// TODO: add message2.0 event
				// GrowthAnalytics.getInstance().setDevelopment(checkBox.isChecked());
			}
		});

		findViewById(R.id.level_tag_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText editText = (EditText) findViewById(R.id.level_edit_text);
					// TODO: add message2.0 event
					// GrowthAnalytics.getInstance().setLevel(Integer.valueOf(editText.getText().toString()));
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
					// TODO: add message2.0 event
					// GrowthAnalytics.getInstance().purchase(Integer.valueOf(priceEditText.getText().toString()),
					// "item",
					// productEditText.getText().toString());
				} catch (NumberFormatException e) {
					Log.w("Grwothbeat Sample", "Input value error :" + e.getMessage());
				}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}
