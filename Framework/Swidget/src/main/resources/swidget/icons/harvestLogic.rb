

def harvestIcon(path, folder, name, newname, elementary=true)

	puts name + " -> " + newname

	if elementary then
		puts "48"
		harvestIconAtSize(path, folder, name, newname, 48)
		harvestIconAtSize(path, folder, name, newname, 48, nil, symbolic=true)
		puts "32"
		harvestIconAtSize(path, folder, name, newname, 32, 48)
		harvestIconAtSize(path, folder, name, newname, 32, nil, symbolic=true)
		puts "24"
		harvestIconAtSize(path, folder, name, newname, 24)
		harvestIconAtSize(path, folder, name, newname, 24, nil, symbolic=true)
		puts "16"
		harvestIconAtSize(path, folder, name, newname, 16, 24)
		harvestIconAtSize(path, folder, name, newname, 16, nil, symbolic=true)

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
def harvestIconAtSize(path, folder, name, newname, size, fallbackSize=nil, symbolic=false)

  srcsize = symbolic ? "symbolic" : "#{size}"
  destsize = symbolic ? "symbolic/#{size}" : "#{size}" 
  srcname = symbolic ? "#{name}-symbolic" : name
  tmpfile = "/tmp/harvest.svg"

  filename = "#{path}/#{folder}/#{srcsize}/#{srcname}.svg"

  puts symbolic
  puts filename

	if File.exist?(filename)
	  `cp #{filename} #{tmpfile}`
	  
	  if symbolic
	    `sed -i 's/bebebe/323232/g' #{tmpfile}`
	  end
	  
		cmd = "rsvg-convert -w #{size} -h #{size} '#{tmpfile}' > './#{destsize}/#{newname}.png'"
		#puts cmd
		`#{cmd}`
		#`rsvg "#{path}/#{folder}/#{srcsize}/#{name}.svg" "./#{size}/#{newname}.png"`
	elsif fallbackSize != nil and File.exist?("#{path}/#{folder}/#{fallbackSize}/#{srcname}.svg")
		cmd = "rsvg-convert -w #{size} -h #{size} '#{path}/#{folder}/#{fallbackSize}/#{srcname}.svg' >'./#{destsize}/#{newname}.png'"
		#puts cmd
		`#{cmd}`
		#`rsvg-convert -w#{size} -h#{size} "#{path}/#{folder}/#{fallbackSize}/#{name}.svg" "./#{destsize}/#{newname}.png"`
	end
	
	if symbolic
	  #`convert './#{destsize}/#{newname}.png' -colorspace HSB -channel b -negate './#{destsize}/#{newname}.png'`
	  #`convert './#{destsize}/#{newname}.png' -fuzz 100% -fill "#323232" -opaque white './#{destsize}/#{newname}.png'`
	end

end

def harvestCustom(name, newname)

	`rsvg-convert -w 48 -h 48 "./custom/#{name}.svg" >"./48/#{newname}.png"`
	`rsvg-convert -w 32 -h 32 "./custom/#{name}.svg" >"./32/#{newname}.png"`
	`rsvg-convert -w 24 -h 24 "./custom/#{name}.svg" >"./24/#{newname}.png"`
	`rsvg-convert -w 16 -h 16 "./custom/#{name}.svg" >"./16/#{newname}.png"`

end
