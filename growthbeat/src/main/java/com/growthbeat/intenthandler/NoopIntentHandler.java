package com.growthbeat.intenthandler;

import com.growthbeat.model.Intent;

public class NoopIntentHandler implements IntentHandler {

    @Override
    public boolean handle(Intent intent) {
        return (intent.getType() == Intent.Type.noop);
    }

}
