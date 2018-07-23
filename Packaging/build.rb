#!/usr/bin/ruby
require 'fileutils'

#function to extract the jar file


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

def import()
	puts ""
	puts "Importing JAR File..."

	jarfile = `ls *.jar`.split("\n")[0]

	if jarfile == nil
		puts "No JAR file found."
		exit(1)
	end

	`mv -f "./#{jarfile}" "./Peakaboo.jar"`
	jarfile = "Peakaboo.jar"

	puts "DONE\n\n"
	`rm -rf ./build/`
	`mkdir ./build/`

	return jarfile

end

def deb(jarfile, version)
	#Deb Package
	dapppath = "./deb/#{version}/usr/share/Peakaboo/"
	dbinpath = "./deb/#{version}/usr/bin/"
	dconpath = "./deb/#{version}/DEBIAN/"
	resources = [["shared/icon.png", dapppath], ["shared/logo.png", dapppath], ["linux/peakaboo", dbinpath], ["linux/control", dconpath], ["linux/Peakaboo.desktop", "./deb/#{version}/usr/share/applications/"]]
	doSetup(jarfile, dapppath, resources, false)

	puts "Building Debian Package..."
	`cd ./deb && ./generate.sh`
	`cp -f ./deb/Peakaboo.deb ./build/`
	puts "DONE\n\n"

end



def rpm(jarfile, version)
	#RPM Package
	apppath = "./rpm/#{version}/usr/share/Peakaboo"
	binpath = "./rpm/#{version}/usr/bin/"
	resources = [["shared/icon.png", apppath], ["shared/logo.png", apppath], ["linux/peakaboo", binpath], ["linux/Peakaboo.desktop", "./rpm/#{version}/usr/share/applications/"]]
	doSetup(jarfile, apppath, resources, false)

	puts "Building Red Hat Package..."
	`cd ./rpm && ./generate.sh`
	`cp -f ./rpm/Peakaboo*.rpm ./build/`
	puts "DONE\n\n"

end


def windows(jarfile, version)
	#Win32 Package
	#winpath = "./windows/Peakaboo/"
	#resources = [["windows/Logo.ico", winpath], ["windows/peakaboo.vbs", winpath], ["windows/Peakaboo.exe", winpath]]
	##doSetup(jarfile, winpath, resources)
	#doSetup(jarfile, winpath, resources, false)


	winpath= "./windows-launch4j/"
	resources = [["windows/Logo.ico", winpath]]
	doSetup(jarfile, winpath, resources, false)

	puts "Building Windows Package..."
	#`rm -rf ./windows/_win32/_win32/*.exe`
	#`cd ./windows/_win32/ && ./buildWindowsInstaller.sh`
	#`cp ./windows/_win32/_win32/*.exe ./build/`
	`rm -rf ./windows-launch4j/*.exe`
	`cd ./windows-launch4j/ && ./build.sh`
	`cp ./windows-launch4j/*.exe ./build/`
	puts "DONE\n\n"

end


def macos(jarfile, version)
	#Mac OS Package
	macpath = "./mac/Peakaboo/Peakaboo.app/Contents/Resources/Java"
	resources = []
	doSetup(jarfile, macpath, resources, false)

	puts "Building Mac Package..."
	`cd ./mac && sudo ./dir2dmg.sh ./Peakaboo/ Peakaboo-#{version}.dmg Peakaboo5`
	`cp -rf ./mac/Peakaboo-#{version}.dmg ./build/`
end





version = "5.0.0"
jarfile = import()
#deb jarfile
#rpm jarfile
#windows jarfile
macos jarfile, version






