#!/bin/bash
java -jar ./launch4j/launch4j.jar peakaboo.xml
mv "./build/Peakaboo.exe" "./build/Peakaboo $1.exe"
