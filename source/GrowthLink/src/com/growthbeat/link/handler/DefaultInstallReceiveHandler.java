package com.growthbeat.link.handler;

import android.content.Context;
import android.content.Intent;

public class DefaultInstallReceiveHandler implements InstallReceiveHandler {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		defaultProcess();
	}
	
	public void defaultProcess(){
		
	}

}
