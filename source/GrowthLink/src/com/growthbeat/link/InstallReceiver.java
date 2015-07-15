package com.growthbeat.link;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class InstallReceiver extends BroadcastReceiver {
	public static final String LOGGER_DEFAULT_TAG = "GrowthLink";
	@Override
	public void onReceive(Context context, Intent intent) {
		precessIntent(context,  intent);
	}
	
	private void precessIntent(Context context, Intent intent)
    {
        ApplicationInfo applicationinfo;
		try {
			applicationinfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String strPreProcess = applicationinfo.metaData.getString("GROWTHLINK_PREPROCESS_RECEIVER");
	        processMetadataString(strPreProcess, context, intent);
	        processGLIntent(context, intent);
	        String strPostProcess = applicationinfo.metaData.getString("GROWTHLINK_POSTPROCESS_RECEIVER");
	        processMetadataString(strPostProcess, context, intent);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			processGLIntent(context, intent);
		}
        return;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processMetadataString(String metaDataString, Context context, Intent intent){
		if (metaDataString == null || metaDataString.length() == 0) return;
		String[] packages = metaDataString.split(",", 0);
		for(String packageName : packages){
			if (packageName.length() == 0)continue;
			packageName = packageName.trim();
			Log.d("processMetadataString", packageName);
			try
	        {
	            ClassLoader classloader = getClass().getClassLoader();
	            Class receiver = classloader.loadClass(packageName);
	            Object receiverObj = receiver.newInstance();
	            Method method = receiver.getMethod("onReceive", new Class[] {
	                android.content.Context.class, android.content.Intent.class
	            });
	            method.invoke(receiverObj, new Object[] {
	                context, intent
	            });
	        }
	        catch(Exception exception)
	        {
	            exception.printStackTrace();
	        }
		}
	}
	
	private void processGLIntent(Context context, Intent intent){
		Log.d("processGLIntent", "done");
	}
	
}
