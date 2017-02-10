### Integrate [Stetho](http://facebook.github.io/stetho/) into [Fuel](https://github.com/kittinunf/Fuel)

Include the client in your source code by:

```kotlin
if (BuildConfig.DEBUG) {
	FuelManager.instance.client = FuelStethoClient()
}
```
