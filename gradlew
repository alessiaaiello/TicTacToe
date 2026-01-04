#!/usr/bin/env sh
# Gradle wrapper script (Unix)
# Minimal standard wrapper script compatible with Gradle distributions

if [ -z "$JAVA_HOME" ] ; then
  java_exe="java"
else
  java_exe="$JAVA_HOME/bin/java"
fi

DIRNAME="`dirname "$0"`"
APP_HOME="`(cd "$DIRNAME" && pwd)`"

# Wrapper properties location
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PROPS="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Gradle wrapper JAR not found: $WRAPPER_JAR"
  echo "If you have Gradle installed, run: gradle wrapper"
  echo "Alternatively, run Gradle from Android Studio which will generate the wrapper."
fi

"$java_exe" -jar "$WRAPPER_JAR" "$@"
