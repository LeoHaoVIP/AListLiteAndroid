# set personalized config

# set openlist version
echo "SET OPENLIST VERSION TO" ${LATEST_OPENLIST_VERSION}
sed -i "s/\(public static String OPENLIST_VERSION = \"\)\([^\";]*\)\(\";\)/\1${LATEST_OPENLIST_VERSION}\3/" ../../app/src/main/java/com/leohao/android/alistlite/util/Constants.java

# set app version
echo "SET APP VERSION TO" ${app_version}
sed -i "s/\(versionName \"\)[^\"]*\(\"\)/\1${app_version}\2/" ../../app/build.gradle
