#!/usr/bin/ruby

require_relative 'harvestLogic.rb'

folder = ARGV[0]
name = ARGV[1]
newname = ARGV[2]
newname = name if newname == nil 

exit(1) if name == nil || folder == nil


path = "/home/nathaniel/.icons/"

harvestIcon path, folder, name, newname
