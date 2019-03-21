#!/usr/bin/ruby

puts `ls ./48/`.split("\n").map{|f| f.gsub(".png", "").upcase.gsub("-", "_")}.join(",\n")

