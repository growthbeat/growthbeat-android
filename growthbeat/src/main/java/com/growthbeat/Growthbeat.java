package com.growthbeat;

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
