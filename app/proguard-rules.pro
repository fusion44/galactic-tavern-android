# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/fusion44/Dev/Android/SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# for retrolambda
-dontwarn java.lang.invoke.*

# for retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# rounded image view
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**

# SuperRecyclerView
-dontwarn com.malinskiy.superrecyclerview.SwipeDismissRecyclerViewTouchListener*
-keep class jp.wasabeef.recyclerview.animators.** { *; }

# PkRSS
-keep class com.pkmmte.pkrss.Callback{ *; }

# Okio
-dontwarn okio.**

# FloatingSearchView
-keep class com.mypopsy.widget.** { *; }