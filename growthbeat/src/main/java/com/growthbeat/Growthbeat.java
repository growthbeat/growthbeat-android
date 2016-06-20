package com.growthbeat;

import android.content.Context;
import android.os.Build;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.model.Client;
import com.growthpush.GrowthPush;

public class Growthbeat {

    private static final Growthbeat instance = new Growthbeat();

    private Growthbeat() {
        super();
    }

    public static Growthbeat getInstance() {
        return instance;
    }

    public void initialize(Context context, String applicationId, String credentialId) {
        initialize(context, applicationId, credentialId, true);
    }

    public void initialize(Context context, String applicationId, String credentialId, boolean adInfoEnabled) {
        context = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            GrowthbeatCore.getInstance().initialize(context, applicationId, credentialId);
            GrowthPush.getInstance().initialize(context, applicationId, credentialId);
            GrowthMessage.getInstance().initialize(context, applicationId, credentialId);
        }
    }

    public void start() {
        // TODO: Growth Push default open event
    }

    public void stop() {

    }

    public void setLoggerSilent(boolean silent) {
        GrowthbeatCore.getInstance().getLogger().setSilent(silent);
        GrowthMessage.getInstance().getLogger().setSilent(silent);
        GrowthPush.getInstance().getLogger().setSilent(silent);
    }

    public void getClient(final ClientCallback clientCallback) {

        if(clientCallback == null)
            return;

        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                clientCallback.callback(GrowthbeatCore.getInstance().waitClient());
            }
        });
    }

    public static abstract class ClientCallback {
        public abstract void callback(Client client);
    }

}
