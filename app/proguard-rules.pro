# Keep Gemini SDK classes
-keep class com.google.ai.client.generativeai.** { *; }

# Keep ML Kit
-keep class com.google.mlkit.** { *; }

# Keep Vosk
-keep class org.vosk.** { *; }

# Keep Prism4j
-keep class io.noties.prism4j.** { *; }

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# General Android rules
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider