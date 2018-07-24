#!/bin/sh

cd build

fpm -s dir -t deb -n Peakaboo -v $1 -C ./package/ -a all --description "Peakaboo allows users to identify the spectral origins of the XRF spectrum using a technique that fits all components of the K, L, or M spectrum including escape peaks and pileup peaks, and then plots their spatial intensity distributions as maps."
