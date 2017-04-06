package peakaboo.datasource.plugins.vespers.vespers.data.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import ca.sciencestudio.data.daf.DAFDataParser;
import ca.sciencestudio.data.daf.DAFRecord;
import ca.sciencestudio.data.daf.DAFSpectrumParser;
import ca.sciencestudio.data.standard.StdConverter;
import ca.sciencestudio.data.standard.StdUnits;
import ca.sciencestudio.data.support.ConverterException;
import ca.sciencestudio.vespers.data.converter.AbstractMapXYVespersConverter;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.fileformat.FileFormat;
import peakaboo.datasource.components.interaction.Interaction;
import peakaboo.datasource.components.interaction.SimpleInteraction;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.components.scandata.ScanData;
import peakaboo.datasource.plugins.vespers.ConverterFactoryDelegatingDSP;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;

/**
 * 
 * @author maxweld
 *
 */
public class MapXYVespersToPDSConverter extends AbstractMapXYVespersConverter implements DataSource, Metadata, DataSize, PhysicalSize, FileFormat, ScanData, StdConverter {

	private static final String DEFAULT_DATASET_NAME = "XRF_Data_Set";
	
	private static final String DEFAULT_CREATOR = MapXYVespersToPDSConverter.class.getName();
	private static final String DEFAULT_CREATION_TIME = new Date(0L).toString();
	
	private static final String DEFAULT_START_TIME = DEFAULT_CREATION_TIME;
	private static final String DEFAULT_END_TIME = DEFAULT_CREATION_TIME;
	
	private String datasetName = DEFAULT_DATASET_NAME;
	
	private String creator = DEFAULT_CREATOR;
	private String creationTime = DEFAULT_CREATION_TIME;
	
	private String startTime = DEFAULT_START_TIME;
	private String endTime = DEFAULT_END_TIME;
	
	private float maxEnergy = 0.0f;
	
	private List<Spectrum> scans = new ArrayList<Spectrum>();
	
	private double sedMaxEnergy = 0.0;
	private double fedMaxEnergy = 0.0;
	
	private List<Double> srCurrents = new ArrayList<Double>();
	private List<List<Double>> mcsCurrents = new ArrayList<List<Double>>();
	
	private List<long[]> sedSpectrums = new ArrayList<long[]>();
	private List<Long> sedFastCounts = new ArrayList<Long>();
	private List<Long> sedSlowCounts = new ArrayList<Long>();
	private List<Double> sedDeadTimePcts = new ArrayList<Double>();
	private List<Double> sedElapsedRealTimes = new ArrayList<Double>();
	private List<Double> sedElapsedLiveTimes = new ArrayList<Double>();
	
	private List<long[]> fedSumSpectrums = new ArrayList<long[]>();
	
	private List<List<long[]>> fedSpectrums = new ArrayList<List<long[]>>();
	private List<List<Long>> fedFastCounts = new ArrayList<List<Long>>();
	private List<List<Long>> fedSlowCounts = new ArrayList<List<Long>>();
	private List<List<Double>> fedDeadTimePcts = new ArrayList<List<Double>>();
	private List<List<Double>> fedElapsedRealTimes = new ArrayList<List<Double>>();
	private List<List<Double>> fedElapsedLiveTimes = new ArrayList<List<Double>>();
	
	private boolean scanOrderX = true;
	private int sizeX = 0, sizeY = 0;
	private double startX = 0.0, startY = 0.0;
	private double endX = 0.0, endY = 0.0;
	private boolean hasStartX = false, hasStartY = false;
	private  boolean hasEndX = false, hasEndY = false;	
	
	public MapXYVespersToPDSConverter(String fromFormat, String toFormat, boolean forceUpdate) {
		super(fromFormat, toFormat, forceUpdate);
	}

	@Override
	protected boolean openDestination(DAFDataParser dafDataParser, DAFSpectrumParser dafSpectrumParser) throws ConverterException {		
		
		File dafDataFile = getDafDataFile();
		
		// Set Creation Time //
		
		if(dafDataFile != null) {
			long lm = dafDataFile.lastModified();
			creationTime = new Date(lm).toString();
		}
		
		// Set Dataset Name //
		
		if(dafDataFile != null) {
			String dafDataFileName = dafDataFile.getName();
			int idx = dafDataFileName.lastIndexOf(FILE_NAME_SUFFIX_DAF_DATA);
			if(idx <= 0) {
				datasetName = dafDataFileName;
			} else {
				datasetName = dafDataFileName.substring(0, idx);
			}
		}
		
		// Set Start Time //
		
		Date epoch = new Date(0L);
		
		Date startTime = epoch;
		if((getScanStartDate() != null) && (getScanStartDate().after(epoch))) {
			startTime = getScanStartDate();
		}
		else if((dafDataParser.getStartTime() != null) && (dafDataParser.getStartTime().after(epoch))) {
			startTime = dafDataParser.getStartTime();
		}
		else if((dafDataFile != null) && (dafDataFile.lastModified() > epoch.getTime())) {
			startTime = new Date(dafDataFile.lastModified());
		}
		this.startTime = startTime.toString();

		// Set End Time //
		
		Date endTime = startTime;
		if((getScanEndDate() != null) && (getScanStartDate().after(epoch))) {
			endTime = getScanEndDate();
		}
		this.endTime = endTime.toString();

		return false;
	}

	@Override
	protected void closeDestination() throws ConverterException {
	
		//
		// The recommended deadtime correction factor is:
		//     cf = (FastCount / SlowCount) * (RealTime / LiveTime)
		//
		// If DeadTime% is given instead of FastCounts and/or SlowCounts then:
		//     (FastCount / SlowCount) = (1 / (1 - (DT%/100)))
		//
		// This algorithm will fall-back to the following:
		//     cf = (FastCount / SlowCount) 
		// or
		//     cf = (RealTime / LiveTime)
		// or
		//     cf = 1
		//
		
		List<float[]> spectrums = new ArrayList<float[]>();
		
		if(!sedSpectrums.isEmpty()) {
			
			for(int specIdx = 0; specIdx < sedSpectrums.size(); specIdx++) {
			
				float corrFact = 1.0f;
				
				if((specIdx < sedSlowCounts.size()) && (specIdx < sedFastCounts.size())) { 
					float fast = sedFastCounts.get(specIdx);
					float slow = sedSlowCounts.get(specIdx);
					if(slow > 0L) {
						corrFact = (fast / slow);
					}
				}
				else if(specIdx < sedDeadTimePcts.size()) {
					float deadTime = sedDeadTimePcts.get(specIdx).floatValue() / 100.0f;
					if(deadTime < 1.0) { 
						corrFact = (1.0f / (1.0f - deadTime));
					}
				}
				
				if((specIdx < sedElapsedLiveTimes.size()) && (specIdx < sedElapsedRealTimes.size())) {
					double real = sedElapsedRealTimes.get(specIdx);
					double live = sedElapsedLiveTimes.get(specIdx);
					if(live > 1.0) {
						corrFact *= (real / live);
					}
				}
				
				long[] spectrum = sedSpectrums.get(specIdx);
				
				if(corrFact != 1.0) {
					for(int i = 0; i < spectrum.length; i++) {
						spectrum[i] *= corrFact;
					}
				}
				
				spectrums.add(copyToFloatArray(spectrum));
			}
			
			maxEnergy = (float)sedMaxEnergy;
		}
		
		if(spectrums.isEmpty() && !fedSumSpectrums.isEmpty()) {
			
			for(int specIdx = 0; specIdx < fedSumSpectrums.size(); specIdx++) {
				spectrums.add(copyToFloatArray(fedSumSpectrums.get(specIdx)));
			}
			
			maxEnergy = (float)fedMaxEnergy;
		}
		
		if(spectrums.isEmpty() && !fedSpectrums.isEmpty()){
			
			for(int elemIdx = 0; elemIdx < fedSpectrums.size(); elemIdx++) {
			
				for(int idx = 0; idx < fedSpectrums.get(elemIdx).size(); idx++) {
			
					double corrFact = 1.0;
					
					if((idx < fedSlowCounts.get(elemIdx).size()) && (idx < fedFastCounts.get(elemIdx).size())) { 
						double fast = fedFastCounts.get(elemIdx).get(idx);
						double slow = fedSlowCounts.get(elemIdx).get(idx);
						if(slow > 0L) {
							corrFact = (fast / slow);
						}
					}
					else if(idx < fedDeadTimePcts.get(elemIdx).size()) {
						double deadTime = fedDeadTimePcts.get(elemIdx).get(idx) / 100.0;
						if(deadTime < 1.0) { 
							corrFact = (1.0 / (1.0 - deadTime));
						}
					}
					
					if((idx < fedElapsedLiveTimes.get(elemIdx).size()) && (idx < fedElapsedRealTimes.get(elemIdx).size())) {
						double real = fedElapsedRealTimes.get(elemIdx).get(idx);
						double live = fedElapsedLiveTimes.get(elemIdx).get(idx);
						if(live > 1.0) {
							corrFact *= (real / live);
						}
					}
				
					long[] spectrum = fedSpectrums.get(elemIdx).get(idx);
					
					if(corrFact != 1.0) {
						for(int i = 0; i < spectrum.length; i++) {
							spectrum[i] *= corrFact;
						}
					}				
				}

				if(elemIdx == 0) {
					for(int specIdx = 0; specIdx < fedSpectrums.get(elemIdx).size(); specIdx++) {
						spectrums.add(copyToFloatArray(fedSpectrums.get(elemIdx).get(specIdx)));
					}
				} else {
					for(int specIdx = 0; specIdx < fedSpectrums.get(elemIdx).size(); specIdx++) {
						float[] dst = spectrums.get(specIdx);
						long[] src = fedSpectrums.get(elemIdx).get(specIdx);
						for(int i = 0; i < src.length; i++) {
							dst[i] += src[i];
						}
					}
				}
			}
			
			maxEnergy = (float)fedMaxEnergy;
		}
		
		// Correction for electron beam current or x-ray beam flux //
		
		double maxCurrent = 0.0;
		double minCurrent = 0.0;
		List<Double> currents = null;
		
		// Use x-ray beam current measurement // 
		
		for(List<Double> mcsCurrent : mcsCurrents) {
			if(mcsCurrent.size() == spectrums.size()) {
				
				maxCurrent = 0.0;
				minCurrent = Double.MAX_VALUE;
				for(double current : mcsCurrent) {
					if(current > maxCurrent) {
						maxCurrent = current;
					}
					if(current < minCurrent) {
						minCurrent = current;
					}
				}
				
				if((maxCurrent >= minCurrent) && (minCurrent > 0.0)) {
					currents = mcsCurrent;
					break;
				}
			}
		}
		
		// Use electron beam current measurement //
		
		if((currents == null) && (srCurrents.size() == spectrums.size())) {
			
			maxCurrent = 0.0;
			minCurrent = Double.MAX_VALUE;
			for(double current : srCurrents) {
				if(current > maxCurrent) {
					maxCurrent = current;
				}
				if(current < minCurrent) {
					minCurrent = current;
				}
			}
			
			if((maxCurrent >= minCurrent) && (minCurrent > 0.0)) {
				currents = srCurrents;
			}
		}
		
		// Apply correction if valid current measurement was found //
		
		if(currents != null) {
			for(int specIdx = 0; specIdx < spectrums.size(); specIdx++) {
				double current = currents.get(specIdx);
				float[] spectrum = spectrums.get(specIdx);
				for(int i = 0; i < spectrum.length; i++) {
					spectrum[i] *= (maxCurrent / current);
				}
			}
		}
		
		// Fill scans with spectrums //
		
		for(float[] spectrum : spectrums) {
			scans.add(new Spectrum(spectrum, false));
		}
		
		// Clear the unused spectrums //
		
		srCurrents.clear();
		mcsCurrents.clear();
		
		sedSpectrums.clear();
		sedFastCounts.clear();
		sedSlowCounts.clear();
		sedDeadTimePcts.clear();
		sedElapsedRealTimes.clear();
		sedElapsedLiveTimes.clear();
		
		fedSumSpectrums.clear();
		
		fedSpectrums.clear();
		fedFastCounts.clear();
		fedSlowCounts.clear();
		fedDeadTimePcts.clear();
		fedElapsedRealTimes.clear();
		fedElapsedLiveTimes.clear();
		
		getResponse().put(ConverterFactoryDelegatingDSP.RESPONSE_KEY_PEAKABOO_DATA_SOURCE, this);
	}

	@Override
	protected void deleteDestination() throws ConverterException {
		// nothing to do //
	}
	
	@Override
	protected void openDataRecord(int dataRecordIndex, DAFRecord record, int sedNChannels, int fedNChannels) throws ConverterException {
		// nothing to do //
	}
	
	@Override
	protected void closeDataRecord(int dataRecordIndex) throws ConverterException {
		// nothing to do //
	}
	
	@Override
	protected void recSizeX(int sizeX) throws ConverterException {
		scanOrderX = false;
		this.sizeX = sizeX;
	}

	@Override
	protected void recStartX(double startX) throws ConverterException {
		hasStartX = true;
		this.startX = startX;
	}

	@Override
	protected void recEndX(double endX) throws ConverterException {
		hasEndX = true;
		this.endX = endX;
	}

	@Override
	protected void recStepX(double stepX) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recPointX(int pointX) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recPositionX(double positionX) throws ConverterException {
		// nothing to do //
	}
	
	@Override
	protected void recSizeY(int sizeY) throws ConverterException {
		scanOrderX = true;
		this.sizeY = sizeY;
	}

	@Override
	protected void recStartY(double startY) throws ConverterException {
		hasStartY = true;
		this.startY = startY;
	}

	@Override
	protected void recEndY(double endY) throws ConverterException {
		hasEndY = true;
		this.endY = endY;
	}

	@Override
	protected void recStepY(double stepY) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recPointY(int pointY) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recPositionY(double positionY) throws ConverterException {
		// nothing to do //
	}
	
	@Override
	protected void recSRCurrent(double srCurrent) throws ConverterException {
		srCurrents.add(srCurrent);
	}

	@Override
	protected void recMCSCurrent(int index, double mcsCurrent) throws ConverterException {
		while(index >= mcsCurrents.size()) {
			mcsCurrents.add(new ArrayList<Double>());
		}
		mcsCurrents.get(index).add(mcsCurrent);		
	}
	
	@Override
	protected void recSedFastCount(long sedFastCount) throws ConverterException {
		sedFastCounts.add(sedFastCount);
	}

	@Override
	protected void recSedSlowCount(long sedSlowCount) throws ConverterException {
		sedSlowCounts.add(sedSlowCount);
	}

	@Override
	protected void recSedDeadTimePct(double sedDeadTimePct) throws ConverterException {
		sedDeadTimePcts.add(sedDeadTimePct);
	}

	@Override
	protected void recSedElapsedRealTime(double sedElapsedRealTime) throws ConverterException {
		sedElapsedRealTimes.add(sedElapsedRealTime);
	}

	@Override
	protected void recSedElapsedLiveTime(double sedElapsedLiveTime) throws ConverterException {
		sedElapsedLiveTimes.add(sedElapsedLiveTime);
	}

	@Override
	protected void recSedSpectrum(long[] sedSpectrum) throws ConverterException {		
		sedSpectrums.add(copyToLongArray(sedSpectrum));
	}
	
	@Override
	protected void recFedFastCount(int index, long fedFastCount) throws ConverterException {
		while(index >= fedFastCounts.size()) {
			fedFastCounts.add(new ArrayList<Long>());
		}
		fedFastCounts.get(index).add(fedFastCount);
	}

	@Override
	protected void recFedSlowCount(int index, long fedSlowCount) throws ConverterException {
		while(index >= fedSlowCounts.size()) {
			fedSlowCounts.add(new ArrayList<Long>());
		}
		fedSlowCounts.get(index).add(fedSlowCount);
	}

	@Override
	protected void recFedDeadTimePct(int index, double fedDeadTimePct) throws ConverterException {
		while(index >= fedDeadTimePcts.size()) {
			fedDeadTimePcts.add(new ArrayList<Double>());
		}
		fedDeadTimePcts.get(index).add(fedDeadTimePct);
	}
	
	@Override
	protected void recFedElapsedRealTime(int index, double fedElapsedRealTime) throws ConverterException {
		while(index >= fedElapsedRealTimes.size()) {
			fedElapsedRealTimes.add(new ArrayList<Double>());
		}
		fedElapsedRealTimes.get(index).add(fedElapsedRealTime);
	}

	@Override
	protected void recFedElapsedLiveTime(int index, double fedElapsedLiveTime) throws ConverterException {
		while(index >= fedElapsedLiveTimes.size()) {
			fedElapsedLiveTimes.add(new ArrayList<Double>());
		}
		fedElapsedLiveTimes.get(index).add(fedElapsedLiveTime);
	}

	@Override
	protected void recFedSumSpectrum(long[] fedSumSpectrum) throws ConverterException {
		fedSumSpectrums.add(copyToLongArray(fedSumSpectrum));
	}
	
	@Override
	protected void recFedSpectrum(int index, long[] fedSpectrum) throws ConverterException {
		while(index >= fedSpectrums.size()) {
			fedSpectrums.add(new ArrayList<long[]>());
		}
		fedSpectrums.get(index).add(copyToLongArray(fedSpectrum));
	}
	
	@Override
	protected void openBkgdRecord(int bkgdRecordIndex) {
		// nothing to do //
	}

	@Override
	protected void closeBkgdRecord(int bkgdRecordIndex) {
		// nothing to do //
	}

	@Override
	protected void recSedMaxEnergy(double sedMaxEnergy, String units, String name, String description) throws ConverterException {
		this.sedMaxEnergy = sedMaxEnergy;
	}

	@Override
	protected void recSedEnergyGapTime(double sedEnergyGapTime, String units, String name, String description) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recSedEnergyPeakingTime(double sedEnergyPeakingTime, String units, String name, String description) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recSedPresetReadTime(double sedPresetRealTime, String units, String name, String description) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recFedMaxEnergy(double fedMaxEnergy, String units, String name, String description) throws ConverterException {
		this.fedMaxEnergy = fedMaxEnergy;
	}

	@Override
	protected void recFedEnergyGapTime(double fedEnergyGapTime, String units, String name, String description) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recFedEnergyPeakingTime(double fedEnergyPeakingTime, String units, String name, String description) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void recFedPresetRealTime(double fedPresetRealTime, String units, String name, String description) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void openPadRecord(int dataRecordIndex) throws ConverterException {
		// nothing to do //
	}

	@Override
	protected void closePadRecord(int dataRecordIndex) throws ConverterException {
		
		if(!srCurrents.isEmpty()) {
			srCurrents.add(srCurrents.get(srCurrents.size()-1));
		}
		
		if(!mcsCurrents.isEmpty()) {
			for(List<Double> c : mcsCurrents) { c.add(new Double(c.get(c.size()-1))); }
		}
				
		if(!sedSpectrums.isEmpty()) {
			sedSpectrums.add(copyToLongArray(sedSpectrums.get(sedSpectrums.size()-1)));
		}

		if(!sedFastCounts.isEmpty()) {
			sedFastCounts.add(new Long(sedFastCounts.get(sedFastCounts.size()-1)));
		}
		
		if(!sedSlowCounts.isEmpty()) {
			sedSlowCounts.add(new Long(sedSlowCounts.get(sedSlowCounts.size()-1)));
		}
		
		if(!sedDeadTimePcts.isEmpty()) {
			sedDeadTimePcts.add(new Double(sedDeadTimePcts.get(sedDeadTimePcts.size()-1)));
		}
		
		if(!sedElapsedRealTimes.isEmpty()) {
			sedElapsedRealTimes.add(new Double(sedElapsedRealTimes.get(sedElapsedRealTimes.size()-1)));
		}
		
		if(!sedElapsedLiveTimes.isEmpty()) {
			sedElapsedLiveTimes.add(new Double(sedElapsedLiveTimes.get(sedElapsedLiveTimes.size()-1)));
		}
		
		if(!fedSumSpectrums.isEmpty()) {
			fedSumSpectrums.add(copyToLongArray(fedSumSpectrums.get(fedSumSpectrums.size()-1)));
		}
		
		if(!fedSpectrums.isEmpty()) {
			for(List<long[]> s : fedSpectrums) { s.add(copyToLongArray(s.get(s.size()-1))); }
		}
		
		if(!fedFastCounts.isEmpty()) {
			for(List<Long> c : fedFastCounts) { c.add(new Long(c.get(c.size()-1))); }
		}
		
		if(!fedSlowCounts.isEmpty()) {
			for(List<Long> c : fedSlowCounts) { c.add(new Long(c.get(c.size()-1))); }
		}
		
		if(!fedDeadTimePcts.isEmpty()) {
			for(List<Double> c : fedDeadTimePcts) { c.add(new Double(c.get(c.size()-1))); }
		}
		
		if(!fedElapsedRealTimes.isEmpty()) {
			for(List<Double> c : fedElapsedRealTimes) { c.add(new Double(c.get(c.size()-1))); }
		}
		
		if(!fedElapsedLiveTimes.isEmpty()) {
			for(List<Double> c : fedElapsedLiveTimes) { c.add(new Double(c.get(c.size()-1))); }
		}
	}

	private float[] copyToFloatArray(long[] array) {
		int length = array.length;
		float[] temp = new float[length];
		for(int idx=0; idx<length; idx++) {
			temp[idx] = array[idx];
		}
		return temp;
	}
	
	private long[] copyToLongArray(long[] array) {
		int length = array.length;
		long[] temp = new long[length];
		System.arraycopy(array, 0, temp, 0, length);
		return temp;
	}
	
	// DataSource //
	
	@Override
	public Metadata getMetadata() {
		return this;
	}
	

	@Override
	public boolean hasDataSize() {
		return (hasStartX && hasStartY && hasEndX && hasEndY);
	}

	@Override
	public DataSize getDataSize() {
		return this;
	}

	
	
	@Override
	public List<String> getFileExtensions() {
		return Collections.emptyList();
	}

	@Override
	public boolean canRead(String filename) {
		return false;
	}

	@Override
	public boolean canRead(List<String> filenames) {
		return false;
	}

	@Override
	public void read(String filename) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void read(List<String> filenames) throws Exception {
		throw new OperationNotSupportedException();
	}
	
	// DSScanData //
	
	@Override
	public Spectrum get(int index) {
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
		return datasetName;
	}

	// DSRealDimensions //
	
	@Override
	public Coord<Bounds<Number>> getPhysicalDimensions() {
		return new Coord<Bounds<Number>>(new Bounds<Number>(startX, endX), new Bounds<Number>(startY, endY));
	}
	
	@Override
	public Coord<Number> getPhysicalCoordinatesAtIndex(int index) {
		Coord<Integer> dataCoords = getDataCoordinatesAtIndex(index);
		double realCoordX = startX;
		if(sizeX > 1) {
			realCoordX += (dataCoords.x * ((endX - startX) / (sizeX - 1)));
		}
		double realCoordY = startY; 
		if(sizeY > 1) {
			realCoordY += (dataCoords.y * ((endY - startY) / (sizeY - 1)));
		}
		return new Coord<Number>(realCoordX, realCoordY);
	}

	@Override
	public SISize getPhysicalUnit() {
		return SISize.mm;
	}

	@Override
	public Coord<Integer> getDataDimensions() {
		return new Coord<Integer>(sizeX, sizeY);
	}
	
	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) {
		int dataCoordX = 0, dataCoordY = 0;
		if(scanOrderX) {
			if(sizeX > 0) {
				dataCoordY = index / sizeX;
				dataCoordX = index - (dataCoordY * sizeX);
			}
		} else {
			if(sizeY > 0) {
				dataCoordX = index / sizeY;
				dataCoordY = index - (dataCoordX * sizeY);
			}
		}
		return new Coord<Integer>(dataCoordX, dataCoordY);
	}

	// DSMetadata // 
	
	@Override
	public String getCreator() {
		return creator;
	}

	@Override
	public String getCreationTime() {
		return creationTime;
	}
	
	@Override
	public String getStartTime() {
		return startTime;
	}

	@Override
	public String getEndTime() {
		return endTime;
	}

	@Override
	public FileFormat getFileFormat() {
		return this;
	}

	@Override
	public String getFormatName() {
		return "";
	}

	@Override
	public String getFormatDescription() {
		return "";
	}

	
	
	
	
	private Interaction interaction = new SimpleInteraction();
	
	@Override
	public void setInteraction(Interaction interaction) {
		this.interaction = interaction;
	}

	@Override
	public Interaction getInteraction() {
		return interaction;
	}

	@Override
	public ScanData getScanData() {
		return this;
	}

	@Override
	public PhysicalSize getPhysicalSize() {
		return this;
	}



	// Others DSMetadata methods implemented by super-class //
	
}
