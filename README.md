# Growthbeat SDK for Android

Growthbeat SDK for Android

## Usage

1. Add growthbeat.jar into libs directory in your project. 

1. Write initialization code

	```java
	Growthbeat.getInstance().initialize(getApplicationContext(), "APPLICATION_ID", "CREDENTIAL_ID");
	```

	You can get the APPLICATION_ID and CREDENTIAL_ID on web site of Growthbeat. 

1. Initialize Growth Analytics.

	```java
	Growthbeat.getInstance().initializeGrowthAnalytics();
	```
	
1. Initialize Growth Message.

	```java
	Growthbeat.getInstance().initializeGrowthMessage();
	```

1. Initialize Growth Push. (Under development)

	```java
	Growthbeat.getInstance().initializeGrowthPush(BuildConfig.DEBUG ? Environment.development : Environment.production, "SENDER_ID");
	```

1. Initialize Growth Replay. (Under development)

	```java
	Growthbeat.getInstance().initializeGrowthReplay();
	```

1. Call Growthbeat's start method on Application#onStart

	```java
	Growthbeat.getInstance().start();
	```

1. Call Growthbeat's stop method on Application#onStop

	```java
	Growthbeat.getInstance().stop();
	```

1. Add following code as a element of `<application/>` in AndroidManifest.xml

	```xml
	<activity
		android:name="com.growthbeat.message.view.AlertActivity"
	android:theme="@android:style/Theme.Translucent" />
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

### Growth Push (Under development)

[Growth Push](https://growthpush.com/) is push notification and analysis platform for mobile apps.

* [Growth Push SDK for Android](https://github.com/SIROK/growthpush-android)

### Growth Replay (Under development)

[Growth Replay](https://growthreplay.com/) is usability testing tool for mobile apps.

* [Growth Replay SDK for Android](https://github.com/SIROK/growthreplay-android)

## License

Apache License, Version 2.0
