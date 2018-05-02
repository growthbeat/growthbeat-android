package com.growthpush.handler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.growthbeat.utils.PermissionUtils;
import com.growthpush.GrowthPush;
import com.growthpush.GrowthPushConstants;
import com.growthpush.view.AlertActivity;
import com.growthpush.view.DialogType;

import java.util.Random;

public class BaseReceiveHandler implements ReceiveHandler {

    private Callback callback = new Callback();

    public BaseReceiveHandler() {
        super();
    }

    public BaseReceiveHandler(Callback callback) {
        this();
        setCallback(callback);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }

    protected void showAlert(Context context, Intent intent) {

        if (context == null || intent == null || intent.getExtras() == null)
            return;

        if (!intent.getExtras().containsKey("message") && !intent.getExtras().containsKey("dialogType"))
            return;

        if (intent.getExtras().containsKey("message")) {
            String message = intent.getExtras().getString("message");
            if (message == null || message.length() <= 0 || message.equals(""))
                return;
        }

        DialogType dialogType = DialogType.none;
        if (intent.getExtras().containsKey("dialogType")) {
            try {
                dialogType = DialogType.valueOf(intent.getExtras().getString("dialogType"));
            } catch (IllegalArgumentException e) {
            } catch (NullPointerException e) {
            }
        }

        if (dialogType == DialogType.none)
            return;

        Intent alertIntent = new Intent(context, AlertActivity.class);
        alertIntent.putExtras(intent.getExtras());
        alertIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(alertIntent);

    }

    protected void addNotification(Context context, Intent intent) {

        if (context == null || intent == null || intent.getExtras() == null)
            return;

        if (!intent.getExtras().containsKey("message"))
            return;

        String message = intent.getExtras().getString("message");
        if (message == null || message.length() <= 0 || message.equals(""))
            return;

        int randomNotificationId = randomIntNumber();

        NotificationCompat.Builder builder = defaultNotificationBuilder(context, intent.getExtras(), defaultLaunchPendingIntent(randomNotificationId, context, intent.getExtras()));
        addNotification(context, randomNotificationId, builder.build());
    }

    public void addNotification(Context context, int notificationId, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify("GrowthPush" + context.getPackageName(), notificationId, notification);
        }
    }

    public NotificationCompat.Builder defaultNotificationBuilder(Context context, Bundle extras, PendingIntent contextIntent) {
        return defaultNotificationBuilder(getBuilder(context), context, extras, contextIntent);
    }

    public NotificationCompat.Builder defaultNotificationBuilder(NotificationCompat.Builder builder, Context context, Bundle extras, PendingIntent contextIntent) {
        PackageManager packageManager = context.getPackageManager();

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            int icon = packageManager.getApplicationInfo(context.getPackageName(), 0).icon;
            if (applicationInfo.metaData != null && applicationInfo.metaData.containsKey(GrowthPushConstants.NOTIFICATION_ICON_META_KEY))
                icon = applicationInfo.metaData.getInt(GrowthPushConstants.NOTIFICATION_ICON_META_KEY);
            String title = packageManager.getApplicationLabel(applicationInfo).toString();

            builder.setTicker(title);
            builder.setSmallIcon(icon);
            builder.setContentTitle(title);
            bigTextStyle.setBigContentTitle(title);

            if (applicationInfo.metaData != null
                && applicationInfo.metaData.containsKey(GrowthPushConstants.NOTIFICATION_ICON_BACKGROUND_COLOR_META_KEY)) {
                builder.setColor(ContextCompat.getColor(context, applicationInfo.metaData.getInt(GrowthPushConstants.NOTIFICATION_ICON_BACKGROUND_COLOR_META_KEY)));
            }

            if (applicationInfo.metaData != null
                && applicationInfo.metaData.containsKey(GrowthPushConstants.NOTIFICATION_BIG_ICON_META_KEY)) {
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), applicationInfo.metaData.getInt(GrowthPushConstants.NOTIFICATION_BIG_ICON_META_KEY)));
            }
        } catch (NameNotFoundException e) {
        }

        String message = extras.getString("message");
        boolean sound = extras.containsKey("sound") ? Boolean.valueOf(extras.getString("sound")) : false;

        builder.setContentIntent(contextIntent == null ? defaultLaunchPendingIntent(randomIntNumber(), context, extras) : contextIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setDefaults(Notification.PRIORITY_DEFAULT);
        }

        builder.setContentText(message);
        bigTextStyle.bigText(message);
        builder.setStyle(bigTextStyle);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);

        if (sound && PermissionUtils.permitted(context, "android.permission.VIBRATE"))
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        return builder;
    }

    private PendingIntent defaultLaunchPendingIntent(int requestCode, Context context, Bundle extras) {
        Intent intent = new Intent(context, AlertActivity.class);
        intent.putExtras(extras);
        intent.putExtra("dialogType", DialogType.none.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @SuppressWarnings("deprecation")
    public NotificationCompat.Builder getBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return builderWithNotificationChannel(context);
        } else {
            return new NotificationCompat.Builder(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationCompat.Builder builderWithNotificationChannel(Context context) {

        if (GrowthPush.getInstance().getChannelId() != null) {
            return new NotificationCompat.Builder(context, GrowthPush.getInstance().getChannelId());
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel defaultChannel = notificationManager.getNotificationChannel(GrowthPushConstants.DEFAULT_NOTIFICATION_CHANNEL_ID);
        if (defaultChannel == null) {
            String channelName = "Notification";
            try {
                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo.metaData.containsKey(GrowthPushConstants.DEFAULT_NOTIFICATION_CHANNEL_NAME)) {
                    channelName = applicationInfo.metaData.getString(GrowthPushConstants.DEFAULT_NOTIFICATION_CHANNEL_NAME);
                }
            } catch (NameNotFoundException e) {
            }
            defaultChannel = new NotificationChannel(GrowthPushConstants.DEFAULT_NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            defaultChannel.enableLights(true);
            defaultChannel.enableVibration(true);
            defaultChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        notificationManager.createNotificationChannel(defaultChannel);

        return new NotificationCompat.Builder(context, GrowthPushConstants.DEFAULT_NOTIFICATION_CHANNEL_ID);

    }

    private int randomIntNumber() {
        String endTimestamp = String.valueOf(System.currentTimeMillis());
        int maxIdRange = Integer.valueOf(endTimestamp.substring(endTimestamp.length() - 9, endTimestamp.length()));
        return new Random().nextInt(maxIdRange);
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static class Callback {

        public void onOpen(Context context, Intent intent) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(context.getPackageName())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

}
