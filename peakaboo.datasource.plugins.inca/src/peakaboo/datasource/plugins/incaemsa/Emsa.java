package peakaboo.datasource.plugins.incaemsa;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import fava.functionable.Range;
import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.fileformat.FileFormat;
import peakaboo.datasource.components.fileformat.FileFormatCompatibility;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.components.scandata.ScanData;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.ISpectrum;
import scitypes.Spectrum;

public class Emsa extends AbstractDataSource implements FileFormat, ScanData {

	private List<Spectrum> scans;
	private float maxEnergy = 0;
	private float channelOffset = 0;
	private Map<String, String> tags;
	
	
	public Emsa() {
		scans = SpectrumList.create(getFormatName());
	}
	
	
	
	/******************************
	 * DataSourceFileFormat
	 ******************************/
	@Override
	public List<String> getFileExtensions() {
		return Collections.singletonList("txt");
	}

	@Override
	public FileFormatCompatibility compatibility(File file) {
		if (!file.getAbsolutePath().endsWith(".txt")) return FileFormatCompatibility.NO;
		
		try {
			Scanner scanner = new Scanner(file);
			scanner.useDelimiter("\n");
			if (!scanner.hasNext()) return FileFormatCompatibility.NO;
			String line = scanner.next();
			if (!line.trim().equals("#FORMAT      : EMSA/MAS Spectral Data File")) return FileFormatCompatibility.NO;
			return FileFormatCompatibility.YES_BY_CONTENTS;
		} catch (FileNotFoundException e) {
			return FileFormatCompatibility.NO;
		}
			
	}

	@Override
	public FileFormatCompatibility compatibility(List<File> files) {
		if (files.size() == 0) return FileFormatCompatibility.NO;
		return compatibility(files.get(0));
	}

	
	@Override
	public FileFormat getFileFormat() {
		return this;
	}
	

	@Override
	public String getFormatName() {
		return "Inca EMSA";
	}

	@Override
	public String getFormatDescription() {
		return "Inca EMSA X-ray Spectra";
	}
	
	
	
	
	
	
	
	private void readTags(String filename) throws Exception {
		
		if (tags != null) { return; }
		tags = new HashMap<>();
		
		Scanner scanner = new Scanner(new File(filename));
		scanner.useDelimiter("\n");
		
		while (scanner.hasNext()) {
			String line = scanner.next();
			if (!line.trim().startsWith("#")) continue; //not a comment
			if (!line.contains(":")) { continue; } //not a key:value comment
			
			String key, value, parts[];
			parts = line.split(":");
			key = parts[0].trim().substring(1).trim();  //get rid of #
			value = parts[1].trim();
			tags.put(key, value);
			
		}
		scanner.close();
	}
	
	@Override
	public void read(File file) throws Exception {
		
		readTags(file.getAbsolutePath());
		
		Map<Float, Float> energies = new HashMap<>();
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("\n");
		
		while (scanner.hasNext()) {
			String line = scanner.next();
			if (line.trim().startsWith("#")) continue; //comment
			if (!line.contains(",")) continue; //not an "energy, count" line
			
			String energy, counts, parts[];
			parts = line.split(",");
			energy = parts[0].trim();
			counts = parts[1].trim();
			
			try {
				energies.put(Float.parseFloat(energy), Float.parseFloat(counts));
			} catch (NumberFormatException e) {}
			
		}
		scanner.close();
		
		
		float tag_xperchan = 0.1f, tag_offset = 0, tag_choffset = 0;
		int tag_npoints = 2048;
				
		//width of channel
		if (tags.containsKey("XPERCHAN")) {
			tag_xperchan = Float.parseFloat(tags.get("XPERCHAN"));
		}
		
		//energy value of first channel
		if (tags.containsKey("OFFSET")) {
			tag_offset = Float.parseFloat(tags.get("OFFSET"));
		}
		
		//translation of all channels
		if (tags.containsKey("CHOFFSET")) {
			tag_choffset = Float.parseFloat(tags.get("CHOFFSET"));
		}
		
		//number of channels
		if (tags.containsKey("NPOINTS")) {
			tag_npoints = (int)Float.parseFloat(tags.get("NPOINTS"));
		}
			
		Spectrum spectrum = new ISpectrum(tag_npoints );
		
		List<Float> keys = new ArrayList<>(energies.keySet());
		keys.sort((a, b) -> Float.compare(a, b));

		float offset = tag_offset;
		for (float energy : keys) {
			
			if (offset < 0) {
				offset += tag_xperchan;
				continue;
			}
			
			spectrum.add(energies.get(energy));
			maxEnergy = energy;
			
		}
		scans.add(spectrum);
		
	}

	@Override
	public void read(List<File> files) throws Exception {
		for (File file : files) {
			read(file);
		}
	}

	@Override
	public Spectrum get(int index) throws IndexOutOfBoundsException {
		return scans.get(index);
	}

	@Override
	public int scanCount() {
		return scans.size();
	}

	@Override
	public String scanName(int index) {
		return "Scan #" + (index+1);
	}

	@Override
	public float maxEnergy() {
		return maxEnergy;
	}

	@Override
	public String datasetName() {
		return "EMSA Dataset";
	}



	@Override
	public ScanData getScanData() {
		return this;
	}
	
	

	//==============================================
	// UNSUPPORTED FEATURES
	//==============================================
	

	@Override
	public Metadata getMetadata() {
		return null;
	}
	
	@Override
	public DataSize getDataSize() {
		return null;
	}


	@Override
	public PhysicalSize getPhysicalSize() {
		return null;
	}









}
