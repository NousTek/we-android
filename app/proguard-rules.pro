# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-allowaccessmodification
-repackageclasses ''

# For using GSON @Expose annotation
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#okhattp
-keepattributes Signature
-keepattributes *Annotation*

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-ignorewarnings
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepattributes Signature
# POJOs used with GSON
# The variable names are JSON key values and should not be obfuscated
-keepclassmembers class com.we.beyond.model { <fields>; }
-keep class com.we.beyond.model.** { *; }
-keep class com.we.beyond.util.** { *; }


-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

# dialog
-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
public <init>(...);
}


# crop image
-keep class androidx.appcompat.widget.** { *; }

-keepattributes JavascriptInterface
-keepattributes *Annotation*


-optimizations !method/inlining/*

