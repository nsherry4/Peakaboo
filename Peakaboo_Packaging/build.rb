#!/usr/bin/ruby
require 'fileutils'

#function to extract the jar file
def setup

	def doSetup(jarfile, path, resources, unzip=true)

		if unzip
			FileUtils.mkdir_p(path)
			`rm -rf #{path}`
			FileUtils.mkdir_p(path)
			`unzip "#{jarfile}" -d "#{path}"`
		else
			FileUtils.mkdir_p(path)
			`cp "#{jarfile}" "#{path}"`
		end
		
		resources.each{|res|
			source, target = res
			`cp -f "./resources/#{source}" "#{target}"`
		}
	end

	jarfile = `ls *.jar`.split("\n")[0]

	 if jarfile == nil
		puts "No JAR file found."
		exit(1)
	 end
	 
	 `mv -f "./#{jarfile}" "./Peakaboo.jar"`
	 
	 jarfile = "Peakaboo.jar"

	#Deb Package
	dapppath = "./deb/3.0/usr/share/Peakaboo/"
	dbinpath = "./deb/3.0/usr/bin/"
	
	resources = [["shared/icon.png", dapppath], ["shared/logo.png", dapppath], ["linux/peakaboo", dbinpath]]

	doSetup(jarfile, dapppath, resources)




	#Win32 Package
	winpath = "./windows/Peakaboo/"
	
	resources = [["windows/Logo.ico", winpath], ["windows/peakaboo.vbs", winpath]]
	
	doSetup(jarfile, winpath, resources)
	
	
	
	
	#Mac OS Package
	macpath = "./mac/Peakaboo/Peakaboo.app/Contents/Resources/Java"
	
	resources = []
	
	doSetup(jarfile, macpath, resources, false)

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
`rm -rf ./windows/_win32/_win32/*.exe`
`cd ./windows/_win32/ && ./buildWindowsInstaller.sh`
`cp ./windows/_win32/_win32/*.exe ./build/`
puts "DONE\n\n"

puts "Building Mac Package..."
`cd ./mac && sudo ./dir2dmg.sh ./Peakaboo/ Peakaboo3.dmg Peakaboo3`
`cp -rf ./mac/Peakaboo3.dmg ./build/`
