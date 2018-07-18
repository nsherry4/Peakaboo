#!/bin/sh

#rm -rf ./rpmbuild/
#mkdir -p ./rpmbuild/{RPMS,SRPMS,BUILD,SOURCES,SPECS,tmp}

#cd 5.0
#tar czf Peakaboo.tgz usr
#mv Peakaboo.tgz .. -f
#cd ..

#cp Peakaboo.spec ./rpmbuild/SPECS/Peakaboo.spec
#cp Peakaboo.tgz ./rpmbuild/SOURCES/

#cd rpmbuild
#rpmbuild --define '_topdir /home/nathaniel/Projects/SS/Git/Peakaboo/Packaging/rpm/rpmbuild' -bb SPECS/Peakaboo.spec

rm ./*.rpm -f
fpm -s dir -t rpm -n Peakaboo -v 5.0.0 -C ./5.0.0/ -a all --rpm-summary "XRF Plotter and Mapper" --description "Peakaboo allows users to identify the spectral origins of the XRF spectrum using a technique that fits all components of the K, L, or M spectrum including escape peaks and pileup peaks, and then plots their spatial intensity distributions as maps."
