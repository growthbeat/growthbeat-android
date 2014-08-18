# Growthbeat SDK for Android

Growthbeat SDK for Android

## Usage

1. Add growthbeat.jar into libs directory in your project. 

1. Write initialization code

	```java
	Growthbeat.getInstance().initialize(this.getApplicationContext(), "APPLICATION_ID", "CREDENTIAL_ID");
	```

	You can get the APPLICATION_ID and CREDENTIAL_ID on web site of Growthbeat. 

1. Use Growth Push.

	```java
	Growthbeat.getInstance().initializeGrowthPush(BuildConfig.DEBUG ? Environment.development : Environment.production, "SENDER_ID");
	```

1. Use Growth Replay.

	```java
	Growthbeat.getInstance().initializeGrowthReplay();
	GrowthReplay.getInstance().start();
	```

1. Track events and set tags.

	```java
	GrowthPush.getInstance().setTag("NAME", "VALUE");
	GrowthPush.getInstance().trackEvent("NAME", "VALUE");
	```

## Included SDKs

Growthbeat is growth hack platform for mobile apps. This repository includes Growthbeat Core SDK, Growth Push SDK and Growth Replay SDK.

### Growthbeat Core

Growthbeat Core SDK is core functions for Growthbeat integrated services.

* [Growthbeat Core SDK for Android](https://github.com/SIROK/growthbeat-core-android/)

### Growth Push

[Growth Push](https://growthpush.com/) is push notification and analysis platform for mobile apps.

* [Growth Push SDK for Android](https://github.com/SIROK/growthpush-android)

### Growth Replay

[Growth Replay](https://growthreplay.com/) is usability testing tool for mobile apps.

* [Growth Replay SDK for Android](https://github.com/SIROK/growthreplay-android)

## License

Apache License, Version 2.0
