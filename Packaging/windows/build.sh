#!/bin/bash
#java --illegal-access=permit -jar ./launch4j/launch4j.jar peakaboo.xml 
/usr/java/jdk1.8.0_171-amd64/bin/java -jar ./launch4j/launch4j.jar peakaboo.xml 
mv "./build/Peakaboo.exe" "./build/Peakaboo $1.exe"
