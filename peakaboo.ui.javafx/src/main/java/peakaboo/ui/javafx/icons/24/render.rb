#!/usr/bin/ruby

def render(svgs)
	svgs.each{|svg| `rsvg #{svg} #{svg[0..-5]}.png`}
end

svgs = `ls *.svg`.strip.split("\n")
render(svgs)
