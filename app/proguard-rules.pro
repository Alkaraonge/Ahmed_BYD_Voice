# Core domain models & 50 themes
-keep class com.ahmed.bydvoice.core.theme.** { *; }
-keep class com.ahmed.bydvoice.core.update.** { *; }
-keep class com.ahmed.bydvoice.core.voice.** { *; }

# Vosk & JNA native bindings (CRITICAL for Release APK stability)
-keep class org.vosk.** { *; }
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.Structure {
    public <fields>;
}
-keepclassmembers class * extends com.sun.jna.Structure$ByValue { *; }
-keepclassmembers class * extends com.sun.jna.Structure$ByReference { *; }
-dontwarn com.sun.jna.**
-dontwarn java.awt.**
-dontwarn javax.naming.**

# AndroidX ViewModel & FileProvider
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class androidx.core.content.FileProvider { *; }

# Strip verbose debug logs in Release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
