# Star Citizen Informer
#### Description
This is an app about the game Star Citizen by Cloud Imperium Games.

Follow the latest official news about the developments of the game and its community.

Search for Users, Organizations and the forums. Follow your favourite organizations and users for quick access to their latest news and progressions.

#### Intended User
People that are interested in the Star Citizen game.
More info about the game can be found at: https://robertsspaceindustries.com/


#### Features
Get the latest official news as soon as they are released
Search Users, Organizations, Ships and more
Favorite Users, Orgs and Forum threads for quick access later on

### Development
#### Some rules for the source code layout:
* Before every commit, apply the source code formatter and cleanup
* Follow the general folder structure and Flux architecture
* Bundle keys reside in the *receiving* class

#### Used libraries
[RxFlux](www.fsdg.de) - provides a framework to build an apps upon. It provides guides to implement the
Facebooks Flux pattern with the help of RxJava. This will help keeping the code maintainable
once it grows in size.

[Retrofit](http://square.github.io/retrofit/) Provides a easy and clean way to communicate with REST interfaces.
Retrofit integrates nicely with RxJava. This will be used fetch data from http://sc-api.com/

[Retrolambda](https://github.com/orfjackal/retrolambda) and [Retrolambda Grade Plugin](https://github.com/evant/gradle-retrolambda) Backport of Java 8’s Lambdas to Android’s Java 7

[PkRSS](https://github.com/Pkmmte/PkRSS)  - Is a library for reading an RSS feed and making it available for easy consumption within my code

[StorIO](https://github.com/pushtorefresh/storio) - An abstraction layout for SQLiteDatabase and ContentResolver. This is for persisting data to SQLite

[Stetho](http://facebook.github.io/stetho/) - For making debugging easier (especially browsing the SQL database easily from Chrome). This will be implemented in Debug build only.

Optional:
[Dagger2](http://google.github.io/dagger/) - A dependency injection framework for managing dependencies in a maintainable way. This needs serious research which might cause the project to take much longer.

#### Lessons learned during development
Here I keep a loose log of problems and weird stuff I came across during development for future reference.

##### Android
* Never put a RecyclerView inside a ScrollingView. Bad things will happen like RecyclerView not showing anything.
Doing this makes no sense anyway, because nested scrolling views will lead to confusing interaction for users when the scroll direction is the same.
Embedding a RecyclerView inside another RecyclerView is OK when scroll direction is different. When nesting RecyclerView's and using [AppBarLayout](http://developer.android.com/reference/android/support/design/widget/AppBarLayout.html)
 then make sure to set [RecyclerView#setNestedScrollingEnabled(boolean)](http://developer.android.com/reference/android/support/v7/widget/RecyclerView.html#setNestedScrollingEnabled(boolean)) to false on the nested view. Failing to do so will [break the AppBarLayout](https://goo.gl/photos/LN23TKQiwR7gMhF36).
* Intents and Bundles are not indented to handle large data sets like comm links.
For the Bundle receiver, the data will be incomplete and/or muddled -> Basically I knew this beforehand but hoped it'll work out.
Well, it didn't. *Solution:* Store comm links in database and retrieve data from database on demand.
* Convert Url String to Android Uri by using [Uri#parse](http://developer.android.com/reference/android/net/Uri.html#parse(java.lang.String))
* When adding activity transitions, don't forget to enable them in your style when using a non material theme: *<item name="android:windowActivityTransitions"\>true</item\>*

##### RxJava
* Always implement onError and actually log the error message
* Make sure to add arguments to the RxActions in [RxActionCreator#newRxAction](https://github.com/skimarxall/RxFlux/wiki/Actions)
to avoid the newly created action being mistaken to an existing one