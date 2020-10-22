# Rapid Viewer Plugin #

This is the Rapid Viewer Plugin. It exposes APIs to manage work lists and work items in them.
The main consumer of the APIs is the Rapid viewer, but the APIs can be used elsewhere.

# Building #

To build the Rapid Viewer plugin:

1. If you haven't already, clone this repository and cd to the newly cloned folder.
1. Build the plugin: `./gradlew jar` (on Windows, you can use the batch file: `gradlew.bat jar`). This should build the plugin in the file **build/libs/rapid-viewer-plugin-*.jar** (the version may differ based on updates to the code).
1. Copy the plugin jar to your plugins folder: `cp build/libs/rapid-viewer-plugin-*.jar /data/xnat/home/plugins`
