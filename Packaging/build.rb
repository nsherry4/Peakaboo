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
			FileUtils.mkdir_p(target)
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
	dapppath = "./deb/5.0/usr/share/Peakaboo/"
	dbinpath = "./deb/5.0/usr/bin/"
	dconpath = "./deb/5.0/DEBIAN/"
	resources = [["shared/icon.png", dapppath], ["shared/logo.png", dapppath], ["linux/peakaboo", dbinpath], ["linux/control", dconpath], ["linux/Peakaboo.desktop", "./deb/5.0/usr/share/applications/"]]
	doSetup(jarfile, dapppath, resources, false)


	#RPM Package
	apppath = "./rpm/5.0/usr/share/Peakaboo"
	binpath = "./rpm/5.0/usr/bin/"
	resources = [["shared/icon.png", apppath], ["shared/logo.png", apppath], ["linux/peakaboo", binpath], ["linux/Peakaboo.desktop", "./rpm/5.0/usr/share/applications/"]]
	doSetup(jarfile, apppath, resources, false)

	#Win32 Package
	#winpath = "./windows/Peakaboo/"
	#resources = [["windows/Logo.ico", winpath], ["windows/peakaboo.vbs", winpath], ["windows/Peakaboo.exe", winpath]]
	##doSetup(jarfile, winpath, resources)
	#doSetup(jarfile, winpath, resources, false)
	
	
	winpath= "./windows-launch4j/"
	resources = [["windows/Logo.ico", winpath]]
	doSetup(jarfile, winpath, resources, false)
	
	
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

puts "Building Red Hat Package..."
`cd ./rpm && ./generate.sh`
`cp -f ./rpm/Peakaboo*.rpm ./build/`
puts "DONE\n\n"

puts "Building Windows Package..."
#`rm -rf ./windows/_win32/_win32/*.exe`
#`cd ./windows/_win32/ && ./buildWindowsInstaller.sh`
#`cp ./windows/_win32/_win32/*.exe ./build/`
`rm -rf ./windows-launch4j/*.exe`
`cd ./windows-launch4j/ && ./build.sh`
`cp ./windows-launch4j/*.exe ./build/`
puts "DONE\n\n"

puts "Building Mac Package..."
`cd ./mac && sudo ./dir2dmg.sh ./Peakaboo/ Peakaboo5.dmg Peakaboo5`
`cp -rf ./mac/Peakaboo5.dmg ./build/`
