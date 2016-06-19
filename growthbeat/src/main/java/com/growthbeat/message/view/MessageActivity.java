package com.growthbeat.message.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.growthbeat.message.model.Message;
import com.growthpush.GrowthPush;

public class MessageActivity extends FragmentActivity {

    private BroadcastReceiver receiver;

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
            case swipe:
                SwipeMessageFragment swipeMessageFragment = new SwipeMessageFragment();
                swipeMessageFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, swipeMessageFragment).commitAllowingStateLoss();
                break;
            default:
                break;
        }

        // Pressed Home button
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                close();
            }
        };
        this.registerReceiver(receiver, intentFilter);
    }

    private void close() {
        if (!isFinishing())
            finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GrowthPush.getInstance().notifyClose();
        if (receiver != null)
            this.unregisterReceiver(receiver);
    }
}
