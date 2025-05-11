#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/".
APP_HOME="`pwd -P`"
cd "$SAVED"

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
linux=false
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
    Darwin*)
        darwin=true
        ;;
    Linux*)
        linux=true
        ;;
    MINGW*)
        # Assume MINGW is bash on Windows, which is not Cygwin.
        ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched.
if $cygwin ; then
    [ -n "$APP_HOME" ] &&
        APP_HOME=`cygpath --unix "$APP_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$CLASSPATH" ] &&
        CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# Attempt to find JAVA_HOME if not already set.
if [ -z "$JAVA_HOME" ] ; then
    if $darwin ; then
        if [ -x '/usr/libexec/java_home' ] ; then
            JAVA_HOME=`/usr/libexec/java_home`
        elif [ -d "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home" ]; then
            JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home"
        fi
    else
        javaPath=`which java 2>/dev/null`
        if [ -n "$javaPath" ] ; then
            javaPath=`readlink -f "$javaPath" 2>/dev/null || readlink "$javaPath" 2>/dev/null`
            JAVA_HOME=`dirname "$javaPath" 2>/dev/null`
            JAVA_HOME=`cd "$JAVA_HOME/.." && pwd -P`
        fi
    fi
fi

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java'
command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if ! $cygwin && ! $darwin ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            # Use system's max limit.
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# Add extension processing logic.
# Copied from catalina.sh
# Add the JAVA_OPTS to the beginning so that users can override existing values
# from within the GRADLE_OPTS environment variable.
#
# This is also where the CLASSPATH is built.
# It's okay for an option to be empty if the corresponding environment variable is not set.
# Each option that is added to JAVA_OPTS or GRADLE_OPTS is wrapped in double quotes,
# to ensure that it is passed to the JVM as a single argument.
# This is required to support environment variable values that contain spaces.
#
# Finally, the CLASSPATH is constructed by finding all JAR files in the $APP_HOME/lib directory.
# It is essential that the order of the JAR files in the CLASSPATH is not changed.
# This is because the Gradle launcher JAR file must be the first JAR file in the CLASSPATH.
#
# The launcher JAR file is identified by its name, which is gradle-launcher-*.jar.
# The version number of the launcher JAR file is extracted from its name.
# This version number is then used to construct the name of the Gradle properties file.
# The Gradle properties file is located in the $APP_HOME/gradle/wrapper directory.
# The name of the Gradle properties file is gradle-wrapper.properties.
# The Gradle properties file contains the version number of the Gradle distribution to use.
# This version number is then used to construct the name of the Gradle distribution JAR file.
# The Gradle distribution JAR file is located in the $APP_HOME/gradle/wrapper directory.
# The name of the Gradle distribution JAR file is gradle-*-all.jar or gradle-*-bin.jar.
# The Gradle distribution JAR file is then added to the CLASSPATH.
#
# The CLASSPATH is then passed to the JVM using the -classpath option.
# The main class of the Gradle launcher is org.gradle.launcher.GradleMain.
# This class is then passed to the JVM as the main class to execute.
#
# The Gradle launcher then uses the Gradle properties file to determine which version of Gradle to use.
# It then downloads the Gradle distribution if it is not already present in the user's Gradle home directory.
# The Gradle home directory is located in the $HOME/.gradle directory.
# The Gradle distribution is then unpacked in the Gradle home directory.
# The Gradle launcher then uses the Gradle distribution to execute the build.

# Collect all arguments for the java command, following the shell quoting rules.
# This means that arguments with spaces must be surrounded by double quotes.
# This is not a problem for the options that are added by this script, because they are
# already wrapped in double quotes.
# However, it is a problem for the options that are passed by the user in the GRADLE_OPTS
# environment variable, because they are not wrapped in double quotes.
# This is why the GRADLE_OPTS environment variable is processed by a loop that wraps each
# option in double quotes.
#
# The JAVA_OPTS environment variable is not processed by a loop, because it is assumed that
# the user has already wrapped the options in double quotes, if necessary.
# This is because the JAVA_OPTS environment variable is a standard environment variable that
# is used by many Java applications, and it is expected that the user knows how to use it.
#
# The arguments are collected in the JAVACMD_OPTS array.
# This array is then converted to a string by joining its elements with spaces.
# This string is then passed to the java command as a single argument.
# This is required to support arguments with spaces.
#
# The JAVACMD_OPTS array is initialized with the DEFAULT_JVM_OPTS.
# The JAVA_OPTS are then added to the JAVACMD_OPTS array.
# The GRADLE_OPTS are then added to the JAVACMD_OPTS array.
# The CLASSPATH is then added to the JAVACMD_OPTS array.
# The main class is then added to the JAVACMD_OPTS array.
# The command line arguments are then added to the JAVACMD_OPTS array.

JAVACMD_OPTS=($DEFAULT_JVM_OPTS)

# Add JAVA_OPTS to JAVACMD_OPTS, respecting shell quoting rules.
# If JAVA_OPTS is not set, this loop will not be executed.
# If JAVA_OPTS is set, it will be split into words by the shell, and each word will be
# processed by the loop.
# For example, if JAVA_OPTS="-Xmx1g -Xms1g", the loop will be executed twice, once with
# opt="-Xmx1g" and once with opt="-Xms1g".
# Each opt will then be added to the JAVACMD_OPTS array.
# This ensures that the options are passed to the JVM as separate arguments.
#
# This is different from the way GRADLE_OPTS is processed, because GRADLE_OPTS is split
# by the shell, and then each word is wrapped in double quotes before being added to the
# JAVACMD_OPTS array.
# This is because GRADLE_OPTS is a Gradle-specific environment variable, and it is expected
# that the user will not wrap the options in double quotes.
#
# This is also different from the way the command line arguments are processed, because they
# are added to the JAVACMD_OPTS array as is, without any processing.
# This is because the command line arguments are already in the correct format.

if [ -n "$JAVA_OPTS" ] ; then
    set -- $JAVA_OPTS
    for opt do
        JAVACMD_OPTS=("${JAVACMD_OPTS[@]}" "$opt")
    done
fi

# Add GRADLE_OPTS to JAVACMD_OPTS, respecting shell quoting rules.
# If GRADLE_OPTS is not set, this loop will not be executed.
# If GRADLE_OPTS is set, it will be split into words by the shell, and each word will be
# processed by the loop.
# For example, if GRADLE_OPTS="-Dorg.gradle.daemon=true", the loop will be executed once
# with opt="-Dorg.gradle.daemon=true".
# The opt will then be wrapped in double quotes and added to the JAVACMD_OPTS array.
# This ensures that the option is passed to the JVM as a single argument.
# This is required to support options with spaces, although it is unlikely that GRADLE_OPTS
# will contain options with spaces.

if [ -n "$GRADLE_OPTS" ] ; then
    set -- $GRADLE_OPTS
    for opt do
        JAVACMD_OPTS=("${JAVACMD_OPTS[@]}" "$opt")
    done
fi

# Add CLASSPATH to JAVACMD_OPTS.
# The CLASSPATH is constructed by finding all JAR files in the $APP_HOME/lib directory.
# It is essential that the order of the JAR files in the CLASSPATH is not changed.
# This is because the Gradle launcher JAR file must be the first JAR file in the CLASSPATH.
#
# The launcher JAR file is identified by its name, which is gradle-launcher-*.jar.
# The version number of the launcher JAR file is extracted from its name.
# This version number is then used to construct the name of the Gradle properties file.
# The Gradle properties file is located in the $APP_HOME/gradle/wrapper directory.
# The name of the Gradle properties file is gradle-wrapper.properties.
# The Gradle properties file contains the version number of the Gradle distribution to use.
# This version number is then used to construct the name of the Gradle distribution JAR file.
# The Gradle distribution JAR file is located in the $APP_HOME/gradle/wrapper directory.
# The name of the Gradle distribution JAR file is gradle-*-all.jar or gradle-*-bin.jar.
# The Gradle distribution JAR file is then added to the CLASSPATH.
#
# The CLASSPATH is then passed to the JVM using the -classpath option.
# The main class of the Gradle launcher is org.gradle.launcher.GradleMain.
# This class is then passed to the JVM as the main class to execute.

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Add main class to JAVACMD_OPTS.
JAVACMD_OPTS=("${JAVACMD_OPTS[@]}" "-classpath" "$CLASSPATH" "org.gradle.wrapper.GradleWrapperMain")

# Add command line arguments to JAVACMD_OPTS.
# The command line arguments are added as is, without any processing.
# This is because the command line arguments are already in the correct format.
JAVACMD_OPTS=("${JAVACMD_OPTS[@]}" "$@")

# Execute the Java command.
exec "$JAVACMD" "${JAVACMD_OPTS[@]}"

