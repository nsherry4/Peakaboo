#!/usr/bin/ruby

`mkdir 16`
`mkdir 24`
`mkdir 32`
`mkdir 48`

#Format is Iconset/category/size/filename.svg
def render(name)

  sourcefile = "./svg-sources/#{name}.svg"
  for size in [16, 24, 32, 48]
    targetfile = "./#{size}/#{name}.png"
    cmd = "rsvg-convert -w #{size} -h #{size} '#{sourcefile}' > '#{targetfile}'"
    `#{cmd}`
  end

end

Dir['./svg-sources/*.svg'].each{|entry|
	basename = entry[14..-5]
	puts basename
	render(basename)
}
