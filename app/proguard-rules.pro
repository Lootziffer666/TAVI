-keep class com.google.mediapipe.** { *; }
-keep class rikka.shizuku.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.squareup.moshi.* <methods>;
}
