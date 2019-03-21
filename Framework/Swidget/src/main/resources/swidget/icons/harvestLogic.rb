

def harvestIcon(path, folder, name, newname, elementary=true)

	puts name + " -> " + newname

	if elementary then
		puts "48"
		harvestIconAtSize(path, folder, name, newname, 48)
		puts "32"
		harvestIconAtSize(path, folder, name, newname, 32, 48)
		puts "24"
		harvestIconAtSize(path, folder, name, newname, 24)
		puts "16"
		harvestIconAtSize(path, folder, name, newname, 16, 24)

	else
		if (File.exists? "#{path}/48x48/#{folder}/#{name}.png") then
			`cp "#{path}/48x48/#{folder}/#{name}.png" "./48/#{newname}.png"`
		else
			`rsvg-convert "#{path}/scalable/#{folder}/#{name}.svg" >"./48/#{newname}.png"`
		end
		
		if (File.exists? "#{path}/32x32/#{folder}/#{name}.png") then
			`cp "#{path}/32x32/#{folder}/#{name}.png" "./32/#{newname}.png"`
		else
			`rsvg-convert "#{path}/scalable/#{folder}/#{name}.svg" >"./32/#{newname}.png"`
		end
		
		`cp "#{path}/32x32/#{folder}/#{name}.png" "./32/#{newname}.png"`
		`cp "#{path}/24x24/#{folder}/#{name}.png" "./24/#{newname}.png"`
		`cp "#{path}/16x16/#{folder}/#{name}.png" "./16/#{newname}.png"`
	end
	
	puts ""
	
end


#assumes elementary icon
def harvestIconAtSize(path, folder, name, newname, size, fallbackSize=nil)

	if File.exist?("#{path}/#{folder}/#{size}/#{name}.svg")
		cmd = "rsvg-convert '#{path}/#{folder}/#{size}/#{name}.svg' >'./#{size}/#{newname}.png'"
		#puts cmd
		`#{cmd}`
		#`rsvg "#{path}/#{folder}/#{size}/#{name}.svg" "./#{size}/#{newname}.png"`
	elsif fallbackSize != nil and File.exist?("#{path}/#{folder}/#{fallbackSize}/#{name}.svg")
		cmd = "rsvg-convert -w #{size} -h #{size} '#{path}/#{folder}/#{fallbackSize}/#{name}.svg' >'./#{size}/#{newname}.png'"
		#puts cmd
		`#{cmd}`
		#`rsvg-convert -w#{size} -h#{size} "#{path}/#{folder}/#{fallbackSize}/#{name}.svg" "./#{size}/#{newname}.png"`
	end

end

def harvestCustom(name, newname)

	`rsvg-convert -w 48 -h 48 "./custom/#{name}.svg" >"./48/#{newname}.png"`
	`rsvg-convert -w 32 -h 32 "./custom/#{name}.svg" >"./32/#{newname}.png"`
	`rsvg-convert -w 24 -h 24 "./custom/#{name}.svg" >"./24/#{newname}.png"`
	`rsvg-convert -w 16 -h 16 "./custom/#{name}.svg" >"./16/#{newname}.png"`

end
