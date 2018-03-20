#!/usr/bin/ruby

files = `ls ./vector/*.svg`.split("\n")
files.each{|file|
	name = File.basename(file, ".svg")
	`rsvg -w 128 -h 128 #{file} ./raster/#{name}.png`
}

