#!/usr/bin/ruby

#function to extract the jar file
def setup

	def doSetup(jarfile, path, resources)

		`rm -rf #{path}`
		`unzip "#{jarfile}" -d #{path}`
		resources.each{|res|
			`cp ./resources/#{res} #{path}`
		}
	end

	jarfile = `ls *.jar`.split("\n")[0]

	#Deb Package
	debpath = "./deb/3.0/usr/share/Peakaboo/"
	resources = ["icon.png", "logo.png", "peakaboo.sh"]

	doSetup(jarfile, debpath, resources)

	#Win32 Package
	winpath = "./windows/Peakaboo/"
	resources = ["Logo.ico", "peakaboo.vbs"]
	doSetup(jarfile, winpath, resources)

end


puts ""
puts "Importing JAR File..."
setup
puts "DONE\n\n"


`rm -rf ./build/`
`mkdir ./build/`

puts "Building Debian Package..."
`cd ./deb && ./generate.sh`
`cp -f ./deb/Peakaboo.deb ./build/`
puts "DONE\n\n"

puts "Building Windows Package..."
`cd ./windows/_win32/ && ./buildWindowsInstaller.sh`
`cp "./windows/_win32/_win32/Peakaboo 3 Setup.exe" ./build/`
puts "DONE\n\n"
