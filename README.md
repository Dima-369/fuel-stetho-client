### Integrate [Stetho](http://facebook.github.io/stetho/) into [Fuel](https://github.com/kittinunf/Fuel)

Include the client, preferably in `Application.onCreate()`:

```kotlin
if (BuildConfig.DEBUG) {
	FuelManager.instance.client = FuelStethoClient()
}
```

`build.gradle` needs to be extended as well:

```gradle
dependencies {
	compile 'com.facebook.stetho:stetho:1.4.2'
	compile 'com.facebook.stetho:stetho-urlconnection:1.4.2'
}
```
