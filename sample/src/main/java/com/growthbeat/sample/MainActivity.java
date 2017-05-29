package com.growthbeat.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.growthbeat.Growthbeat;
import com.growthbeat.message.handler.ShowMessageHandler;
import com.growthbeat.model.Client;
import com.growthpush.GrowthPush;
import com.growthpush.handler.DefaultReceiveHandler;
import com.growthpush.model.Environment;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GrowthPush.getInstance().initialize(this, "PIaD6TaVt7wvKwao", "FD2w93wXcWlb68ILOObsKz5P3af9oVMo",
            BuildConfig.DEBUG ? Environment.development : Environment.production);
        GrowthPush.getInstance().requestRegistrationId("186415479559");

        GrowthPush.getInstance().setReceiveHandler(new DefaultReceiveHandler() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                NotificationCompat.Builder builder = super.defaultNotificationBuilder(context, extras, null);
                String url = extras.getString("image");
                if (url != null) {
                    try {
                        URL image = new URL(url);
                        InputStream istream = image.openStream();
                        Bitmap iBmp = BitmapFactory.decodeStream(istream);
                        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle(builder);
                        builder.setStyle(bigPictureStyle.bigPicture(iBmp));
                        istream.close();
                        builder.setStyle(bigPictureStyle.setSummaryText(extras.getString("message")));
                    } catch (Exception e) {
                    }
                }
                super.addNotification(context, 1, builder.build());
            }
        });

        GrowthPush.getInstance().trackEvent("Launch");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = Growthbeat.getInstance().waitClient();
                Log.d("GrowthbeatSample", String.format("clientId is %s", client.getId()));
            }
        }).start();

        GrowthPush.getInstance().trackEvent("AllowPushPermission", null, new ShowMessageHandler() {
            @Override
            public void complete(MessageRenderHandler renderHandler) {
                Log.i("GrowthMessage", "run renderHandler, show message.");
                renderHandler.render();
            }

            @Override
            public void error(String error) {
                Log.d("GrowthMessage", error);
            }
        });

        findViewById(R.id.set_tag_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText nameEditText = (EditText) findViewById(R.id.tag_name_text);
                    EditText valueEditText = (EditText) findViewById(R.id.tag_value_text);

                    GrowthPush.getInstance().setTag(nameEditText.getText().toString(), valueEditText.getText().toString());
                } catch (NumberFormatException e) {
                    Log.w("Grwothbeat Sample", "Input value error :" + e.getMessage());
                }
            }
        });

        findViewById(R.id.track_event_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText nameEditText = (EditText) findViewById(R.id.event_name_text);
                    EditText valueEditText = (EditText) findViewById(R.id.event_value_text);

                    GrowthPush.getInstance().trackEvent(nameEditText.getText().toString(), valueEditText.getText().toString());
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
