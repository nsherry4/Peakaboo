#!/bin/bash

#hard-coded values
linuxUserMemCap=2560
percent=90
suffix="m"

#get the system memory from /proc/
memLine=`cat /proc/meminfo | grep MemTotal`
mem=${memLine#*:}
memnum=${mem%\ *}
memMeg=$((memnum / 1024))

#don't use all of system memory, use some percentage of it
goodSize=$((memMeg * percent / 100))

#take the smaller of the jvm memory cap and the % of system memory
if [ $linuxUserMemCap -lt $goodSize ]; then
	maxMem=$linuxUserMemCap
else
	maxMem=$memMeg
fi

#append a size suffix
javaMem=$maxMem$suffix

#launch program
#Xmx is heap size
#XX:MaxNewSize is maximum size of 'new' object/memory area. This is set low to keep memory consumption down
java -Xmx$javaMem -XX:MaxNewSize=20m -XX:+UseFastAccessorMethods -XX:+AggressiveOpts peakaboo.Peakaboo
