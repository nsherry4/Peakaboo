package peakaboo.datasource.plugin.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import fava.functionable.Range;
import fava.signatures.FnMap;
import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.plugin.AbstractDSP;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;

public class EmsaDSP extends AbstractDSP {

	private List<Spectrum> scans;
	private float maxEnergy = 0;
	private float channelOffset = 0;
	private Map<String, String> tags;
	
	
	public EmsaDSP() {
		scans = SpectrumList.create(getDataFormat());
	}
	
	
	@Override
	public List<String> getFileExtensions() {
		return Collections.singletonList("txt");
	}

	@Override
	public boolean canRead(String filename) {
		if (!filename.endsWith(".txt")) return false;
		
		try {
			Scanner scanner = new Scanner(new File(filename));
			scanner.useDelimiter("\n");
			if (!scanner.hasNext()) return false;
			String line = scanner.next();
			System.out.println(line);
			if (!line.trim().equals("#FORMAT      : EMSA/MAS Spectral Data File")) return false;
			System.out.println(line);
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
			
	}

	@Override
	public boolean canRead(List<String> filenames) {
		if (filenames.size() == 0) return false;
		return canRead(filenames.get(0));
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
	public void read(String filename) throws Exception {
		
		readTags(filename);
		
		Map<Float, Float> energies = new HashMap<>();
		Scanner scanner = new Scanner(new File(filename));
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
			
		Spectrum spectrum = new Spectrum(tag_npoints );
		
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
	public void read(List<String> filenames) throws Exception {
		for (String filename : filenames) {
			read(filename);
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
	public List<String> scanNames()
	{
		return new Range(0, scanCount()-1).map(element -> "Scan #" + (element+1)).toSink();
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
	public Coord<Integer> getDataCoordinatesAtIndex(int index)
			throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDataFormat() {
		return "Inca EMSA";
	}

	@Override
	public String getDataFormatDescription() {
		return "Inca EMSA X-ray Spectra";
	}
	
	
	
	
	

	//==============================================
	// UNSUPPORTED METHODS
	//==============================================
	
	@Override
	public boolean hasMetadata()
	{
		return false;
	}

	@Override
	public String getCreationTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCreator()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProjectName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSessionName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFacilityName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLaboratoryName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getExperimentName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInstrumentName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTechniqueName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSampleName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScanName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStartTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEndTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Bounds<Number>> getRealDimensions()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealDimensionsUnit()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Integer> getDataDimensions()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasScanDimensions()
	{
		return false;
	}


}
