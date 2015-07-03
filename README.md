# Growthbeat SDK for Android

Growthbeat SDK for Android

## Usage

1. Add growthbeat.jar into libs directory in your project. 

1. Write initialization code

	```java
	Growthbeat.getInstance().initialize(getApplicationContext(), "APPLICATION_ID", "CREDENTIAL_ID", BuildConfig.DEBUG);
	```

	You can get the APPLICATION_ID and CREDENTIAL_ID on web site of Growthbeat. 

1. Call Growthbeat's start method on MainActivity#onStart

	```java
	Growthbeat.getInstance().start();
	```

1. Call Growthbeat's stop method on MainActivity#onStop

	```java
	Growthbeat.getInstance().stop();
	```

1. Add following code as a element of `<application/>` in AndroidManifest.xml

	```xml
	<activity
		android:name="com.growthbeat.message.view.AlertActivity"
		android:theme="@android:style/Theme.Translucent" />
	```
    
1. Write following code in the place to track custom event with Growth Analytics or display a message with Growth Message.

	```java
	GrowthAnalytics.getInstance().track("EVENT_ID");
	```

1. Use push notification

	*Setting xml activity, receiver and permission.*

	```xml
	<application>
	 ...summary

	 <activity
            android:name="com.growthpush.view.AlertActivity"
            android:configChanges="orientation|keyboardHidden"
            android:launchMode="singleInstance"
            android:theme="@android:style/Theme.Translucent" />

        <receiver
            android:name="com.growthpush.BroadcastReceiver"
            android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <action android:name="com.google.android.c2dm.intent.REGISTRATION" />

                <category android:name="YOUR_PACKAGE_NAME" />
            </intent-filter>
        </receiver>

       ...
     </application>

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <permission
        android:name="YOUR_PACKAGE_NAME.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />

    <uses-permission android:name="YOUR_PACKAGE_NAME.permission.C2D_MESSAGE" />
	```

	```java
	Growthbeat.getInstance().requestRegistrationId("YOUR_SENDER_ID");
	```

	
## Included SDKs

Growthbeat is growth hack platform for mobile apps. This repository includes Growthbeat Core SDK, Growth Analytics, Growth Push SDK and Growth Replay SDK.

### Growthbeat Core

Growthbeat Core SDK is core functions for Growthbeat integrated services.

* [Growthbeat Core SDK for Android](https://github.com/SIROK/growthbeat-core-android/)

### Growth Analytics

[Growth Analytics](https://analytics.growthbeat.com/) is analytics service for mobile apps.

* [Growth Analytics SDK for Android](https://github.com/SIROK/growthanalytics-android)

### Growth Message

[Growth Message](https://message.growthbeat.com/) is in-app message service for mobile apps.

* [Growth Message SDK for Android](https://github.com/SIROK/growthmessage-android)

### Growth Push

[Growth Push](https://growthpush.com/) is push notification and analysis platform for mobile apps.

* [Growth Push SDK for Android](https://github.com/SIROK/growthpush-android)

## Supported Environment

* Growthbeat Core support Android 2.3 and later.
* Growth Analytics support Android 2.3 and later.
* Growth Message support Android 2.3 and later.
* Growth Push support Android 2.3 and later.

## License

Apache License, Version 2.0
