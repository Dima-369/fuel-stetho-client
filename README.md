### Integrate [Stetho](http://facebook.github.io/stetho/) into [Fuel](https://github.com/kittinunf/Fuel)

Include the client (preferably in `Application.onCreate()`) in your source code:

```kotlin
if (BuildConfig.DEBUG) {
	FuelManager.instance.client = FuelStethoClient()
}
```
