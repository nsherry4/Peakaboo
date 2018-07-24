#!/bin/bash

rm -rf Peakaboo
mkdir Peakaboo
./jar2app/jar2app.py ../Peakaboo.jar -n "Peakaboo 5.0.0" -d "Peakaboo 5" -i ./peakaboo.icns -b peakaboo -v "5.0.0" -s 5 -m peakaboo.ui.swing.Peakaboo -j "-Xmx1024M" -e Peakaboo --low-res-mode
mv "Peakaboo 5.0.0.app/" Peakaboo

sudo ./dir2dmg.sh  ./Peakaboo/ Peakaboo-5.0.0.dmg Peakaboo5
