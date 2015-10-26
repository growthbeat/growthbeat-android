Growthbeat SDK for Android
===
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/growthbeat/maven/growthbeat-android/images/download.svg)](https://bintray.com/growthbeat/maven/growthbeat-android/_latestVersion)


Growthbeat SDK for Android

## Install

- **Gradle**

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.growthbeat:growthbeat-android:1.2.4@aar'
}
```

## Dependencies

- Android Support Library
- Google Play Services SDK

See more in the [`growthbeat/build.gradle`](https://github.com/growthbeat/growthbeat-android/blob/master/growthbeat/build.gradle).

## Usage

### Growthbeat

1. Write initialization code

  ```java
  Growthbeat.getInstance().initialize(getApplicationContext(), "APPLICATION_ID", "CREDENTIAL_ID");
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

### Growth Analytics

1. Write following code in the place to track custom event with Growth Analytics .

  ```java
  GrowthAnalytics.getInstance().track("EVENT_ID");
  ```
	
### Growth Message

1. Write following code in the place to display a message with Growth Message. (The same code with Growth Analytics)
	
  ```java
  GrowthAnalytics.getInstance().track("EVENT_ID");
  ```
	
1. Add following code as a element of `<application/>` in AndroidManifest.xml

  ```xml
  <activity
      android:name="com.growthbeat.message.view.MessageActivity"
      android:theme="@android:style/Theme.Translucent" />
  ```

### Growth Push

1. Write following code to get device token and send it to server.

  ```java
  GrowthPush.getInstance().requestRegistrationId("YOUR_SENDER_ID", BuildConfig.DEBUG ? Environment.development : Environment.production);
  ```

1. Setting AndroidManifest.xml.

  Add following code as a element of `<application/>`.

  ```xml
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
  ```

  Add following code as a elements of `<manifest/>`.

  ```xml
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

### Growth Link

1. Add following code as a element of `<application/>` in AndroidManifest.xml

  ```xml
  <receiver
      android:name="com.growthbeat.link.InstallReferrerReceiver"
      android:enabled="true"
      android:exported="true">
      <intent-filter>
          <action android:name="com.android.vending.INSTALL_REFERRER" />
      </intent-filter>
  </receiver>
  ```

1. Write initialization code

  ```java
  GrowthLink.getInstance().initialize(getApplicationContext(), "APPLICATION_ID", "CREDENTIAL_ID");
  ```

  You can get the APPLICATION_ID and CREDENTIAL_ID on web site of Growthbeat.
	
1. Write following code to handle url in Activity's onCreate after initialization

  ```java
  GrowthLink.getInstance().handleOpenUrl(getIntent().getData());
  ```

## Supported Environment

* Android 2.3.3 and later.

## License

Apache License, Version 2.0
