#!/usr/bin/ruby

require_relative 'harvestLogic.rb'

path = ARGV[0]
folder = ARGV[1]
name = ARGV[2]
newname = ARGV[3]
newname = name if newname == nil 

exit(1) if name == nil || folder == nil || path == nil

harvestIcon path, folder, name, newname, false
