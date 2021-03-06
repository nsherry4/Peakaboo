#!/bin/bash
#
# make_dmg
#
# make hfsplus disk image from directory
#
# sudo make_dmg <dir_path> <dmg_nameg> [volume_label]
#

#if [ -z "$SUDO_COMMAND" ]   # Need to run this with sudo 
#then 
#   mntusr=$(id -u) grpusr=$(id -g) sudo $0 $* 
#   exit 0 
#fi 
if [ -d "$1" ]              # dir_path 
then 
 dir_path=$1
else
  echo "Must pass in valid dir" 
  exit 
fi
if [ -n "$2" ]              # dmg_name
then 
 dmg_name=$2
else
  echo "Must pass name for dmg" 
  exit 
fi
if [ -n "$3" ]              # volume_label. 
then 
 volume_label=$3
else
  volume_label="Untitled"
  echo
  echo "Using volume_label=Untitled" 
  echo
fi

du_output=`du -sk $dir_path 2>&1`
dir_size=`echo $du_output | cut -f1 -d" "`
dir_size=`expr $dir_size + 5000` 
dd if=/dev/zero of="$dmg_name" bs=1024 count=$dir_size
mkfs.hfsplus -v "$volume_label" "$dmg_name"
rm -rf ./tmp
mkdir ./tmp
mount -o loop -t hfsplus "./$dmg_name" ./tmp/
cp -r $dir_path/* ./tmp
umount ./tmp
rm -rf ./tmp
