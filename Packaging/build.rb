#!/usr/bin/ruby
require 'fileutils'

#function to extract the jar file


def doSetup(jarfile, path, resources)

	FileUtils.mkdir_p(path)
	`cp "#{jarfile}" "#{path}"`


	resources.each{|res|
		source, target = res
		FileUtils.mkdir_p(target)
		`cp -f "./resources/#{source}" "#{target}"`
	}
end

def import(version)
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
	`cp Peakaboo.jar "./build/Peakaboo #{version}.jar"`

	return jarfile

end

def deb_old(jarfile, version)

	builddir = "./deb/build"
	`mkdir #{builddir}`

	pkgdir = "#{builddir}/package/"

	#Deb Package
	apppath = "#{pkgdir}/usr/share/Peakaboo/"
	binpath = "#{pkgdir}/usr/bin/"
	conpath = "#{pkgdir}/DEBIAN/"
	appspath = "#{pkgdir}/usr/share/applications/"
	resources = [["shared/icon.png", apppath], ["shared/logo.png", apppath], ["linux/peakaboo", binpath], ["linux/control", conpath], ["linux/Peakaboo.desktop", appspath]]
	doSetup(jarfile, dapppath, resources)

	puts "Building Debian Package..."
	`cd ./deb && ./generate.sh`
	`cp -f ./deb/Peakaboo.deb ./build/`
	puts "DONE\n\n"

end


def deb(jarfile, version)

	builddir = "./deb/build/"
	`mkdir #{builddir}`

	pkgdir = "#{builddir}/package/"

	#DEB Package
	apppath = "#{pkgdir}/usr/share/Peakaboo"
	binpath = "#{pkgdir}/usr/bin/"
	resources = [["shared/icon.png", apppath], ["shared/logo.png", apppath], ["linux/peakaboo", binpath], ["linux/Peakaboo.desktop", "#{pkgdir}/usr/share/applications/"]]
	doSetup(jarfile, apppath, resources)

	puts "Building Debian Package..."
	`cd ./deb && ./generate.sh #{version}`
	`cp -f ./deb/build/*.deb ./build/`
	puts "DONE\n\n"

	`rm -rf #{builddir}`

end



def rpm(jarfile, version)

	builddir = "./rpm/build/"
	`mkdir #{builddir}`

	pkgdir = "#{builddir}/package/"

	#RPM Package
	apppath = "#{pkgdir}/usr/share/Peakaboo"
	binpath = "#{pkgdir}/usr/bin/"
	resources = [["shared/icon.png", apppath], ["shared/logo.png", apppath], ["linux/peakaboo", binpath], ["linux/Peakaboo.desktop", "#{pkgdir}/usr/share/applications/"]]
	doSetup(jarfile, apppath, resources)

	puts "Building Red Hat Package..."
	`cd ./rpm && ./generate.sh #{version}`
	`cp -f ./rpm/build/*.rpm ./build/`
	puts "DONE\n\n"

	`rm -rf #{builddir}`

end


def windows(jarfile, version)

	#create the build directory
	winpath= "./windows/build/"
	`mkdir #{winpath}`

	#copy jarfile, resources
	resources = [["windows/Logo.ico", winpath]]
	doSetup(jarfile, winpath, resources)

	#Build windows exe and copy it out to the final build dir
	puts "Building Windows Package..."
	`cd ./windows/ && ./build.sh #{version}`
	`cp ./windows/build/*.exe ./build/`
	puts "DONE\n\n"

	`rm -rf #{winpath}`

end


def macos(jarfile, version, shortversion)

	builddir = "./mac/build/"
	`mkdir #{builddir}`

	#Mac OS Package
	macpath = "./mac"
	resources = [["mac/peakaboo.icns", builddir]]
	doSetup(jarfile, builddir, resources)

	puts "Building Mac Package..."
	`cd ./mac && ./build.sh #{version} #{shortversion}`
	`cp -rf #{builddir}/Peakaboo*.dmg ./build/`

	`rm -rf #{builddir}`
end



if ARGV.length == 0
	puts "Need version number argument"
	exit 0
end

version = ARGV[0]
shortversion = version.split(".")[0]

jarfile = import version
deb jarfile, version
rpm jarfile, version
windows jarfile, version
macos jarfile, version, shortversion






