#!/usr/bin/ruby

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

setup
