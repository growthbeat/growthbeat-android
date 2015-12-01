package com.growthbeat.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.growthbeat.Growthbeat;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.link.GrowthLink;
import com.growthbeat.model.Client;
import com.growthpush.GrowthPush;
import com.growthpush.model.Environment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Growthbeat.getInstance().initialize(this, "PIaD6TaVt7wvKwao", "FD2w93wXcWlb68ILOObsKz5P3af9oVMo");
        GrowthLink.getInstance().initialize(this, "PIaD6TaVt7wvKwao", "FD2w93wXcWlb68ILOObsKz5P3af9oVMo");
        GrowthPush.getInstance().requestRegistrationId("1000565500410", BuildConfig.DEBUG ? Environment.development : Environment.production);
        GrowthLink.getInstance().handleOpenUrl(getIntent().getData());
        Growthbeat.getInstance().getClient(new Growthbeat.ClientCallback() {
            @Override
            public void callback(Client client) {
                Log.d("GrowthbeatSample", String.format("clientId is %s", client.getId()));
            }
        });

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
}
