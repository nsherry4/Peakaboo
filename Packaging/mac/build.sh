#!/bin/bash

cd build

../jar2app/jar2app.py ./Peakaboo.jar -n "Peakaboo $1" -d "Peakaboo $2" -i ./peakaboo.icns -b peakaboo -v "$1" -s $2 -m org.peakaboo.ui.swing.Peakaboo -j "-Xmx1024M" -e Peakaboo
mv "Peakaboo $1.app/" "Peakaboo.app"
mkdir App
mv Peakaboo.app App


sudo ../dir2dmg.sh  ./App/ "Peakaboo $1.dmg" "Peakaboo $2"
