# Galactic Tavern for Star Citizen
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

#### Screenshots
![Screenshot of the drawer menu](./screenshots/appscreenshots_drawer.jpg?raw=true "Screenshot of the drawer menu")
![Screenshot of the start map activity](./screenshots/appscreenshots_map.jpg?raw=true "Screenshot of the start map activity")
![Screenshot of the ships viewer activity](./screenshots/appscreenshots_ships.jpg?raw=true "Screenshot of the ships viewer activity")

#### Video
[![Video](https://img.youtube.com/vi/SJ-6UW2xP-E/0.jpg)](https://www.youtube.com/watch?v=SJ-6UW2xP-E)

### Development
#### Some rules for the source code layout:
* Before every commit, apply the source code formatter and cleanup
* Follow the general folder structure and Flux architecture
* Bundle keys reside in the *receiving* class

#### Used libraries
[RxFlux](https://github.com/skimarxall/RxFlux) - provides a framework to build an apps upon. It provides guides to implement the
Facebooks Flux pattern with the help of RxJava. This will help keeping the code maintainable
once it grows in size.

[Retrofit](http://square.github.io/retrofit/) Provides a easy and clean way to communicate with REST interfaces.
Retrofit integrates nicely with RxJava. This will be used fetch data from http://sc-api.com/

[Retrolambda](https://github.com/orfjackal/retrolambda) and [Retrolambda Grade Plugin](https://github.com/evant/gradle-retrolambda) Backport of Java 8’s Lambdas to Android’s Java 7

[StorIO](https://github.com/pushtorefresh/storio) - An abstraction layout for SQLiteDatabase and ContentResolver. This is for persisting data to SQLite

[Stetho](http://facebook.github.io/stetho/) - For making debugging easier (especially browsing the SQL database easily from Chrome). This will be implemented in Debug build only.

[FloatingSearchView](https://github.com/renaudcerrato/FloatingSearchView) - For providing a nice looking search view

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

##### JSON / GSON
* While [jsonschema2pojo](http://www.jsonschema2pojo.org/) is great for quickly converting a JSON string to a usable POJO there are some things to consider.
  * Make sure to have a JSON file which has all data and does not contain nulls where there should be an actual object.
  * Check the resulting POJOs for Object fields where there should be another type like String or another class instance.

### License
Copyright 2016 Stefan Stammberger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.