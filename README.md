### Integrate [Stetho](http://facebook.github.io/stetho/) into [Fuel](https://github.com/kittinunf/Fuel)

Include the client, preferably in `Application.onCreate()`:

```kotlin
if (BuildConfig.DEBUG) {
	FuelManager.instance.client = FuelStethoClient()
}
```
