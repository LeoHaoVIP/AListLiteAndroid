# set personalized config
LATEST_OPENLIST_VERSION=4.1.7
APP_VERSION=2.0.7-beta5
# set openlist version
echo "SET OPENLIST VERSION TO" $LATEST_OPENLIST_VERSION
sed -i "s/\(public static String OPENLIST_VERSION = \"\)\([^\";]*\)\(\";\)/\1$LATEST_OPENLIST_VERSION\3/" ../../app/src/main/java/com/leohao/android/alistlite/util/Constants.java

# set app version
echo "SET APP VERSION TO" $APP_VERSION
sed -i "s/\(versionName \"\)[^\"]*\(\"\)/\1$APP_VERSION\2/" ../../app/build.gradle
