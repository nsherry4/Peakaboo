package peakaboo.datasource.plugins.sciencestudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import ca.sciencestudio.data.converter.Converter;
import ca.sciencestudio.data.converter.ConverterMap;
import ca.sciencestudio.data.converter.LinkedHashConverterMap;
import ca.sciencestudio.data.converter.factory.ConverterFactory;
import ca.sciencestudio.data.converter.factory.DelegatingConverterFactory;
import ca.sciencestudio.data.daf.DAFEventElementOptions;
import ca.sciencestudio.data.daf.DAFRecordParser;
import ca.sciencestudio.data.daf.DAFRegexElementParser;
import ca.sciencestudio.data.daf.DAFRegexRecordParser;
import ca.sciencestudio.data.standard.StdConverter;
import ca.sciencestudio.data.support.ConverterException;
import ca.sciencestudio.data.support.ConverterFactoryException;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.internal.DelegatingDataSource;
import peakaboo.datasource.plugins.sciencestudio.vespers.data.converter.factory.MapXYVespersToPDSConverterFactory;

/**
 * @author maxweld
 *
 */
public abstract class ConverterFactoryDelegatingDSP extends DelegatingDataSource implements StdConverter {

	private static final String DATA_FILE_FIRST_LINE = "# CLS Data Acquisition";

	private static final DelegatingConverterFactory CONVERTER_FACTORY = new DelegatingConverterFactory();
	
	public static final String RESPONSE_KEY_PEAKABOO_DATA_SOURCE = ConverterFactoryDelegatingDSP.class.getName() + ".DataSource";
		
	static {
				
		MapXYVespersToPDSConverterFactory mapXYVespersToPDSConverterFactory = new MapXYVespersToPDSConverterFactory();
		
		// Configure Converter Factory //
		
		List<String> options;
		ConverterMap request;
		DAFEventElementOptions dafEventElementOptions;
		List<DAFEventElementOptions> dafEventElementOptionsList;
		
		// customRecordParsers //
		
		DAFRegexRecordParser vespersBackgroundRecordParser = new DAFRegexRecordParser();
		vespersBackgroundRecordParser.setPrefixPattern(Pattern.compile("^#\\s*"));
		vespersBackgroundRecordParser.setPostfixPattern(Pattern.compile(""));
		
		List<DAFRegexElementParser> elementParsers = new ArrayList<DAFRegexElementParser>();
		elementParsers.add(new DAFRegexElementParser("Background counts time=\\s*([^,]*)\\s*\\(x \\.1 microseconds\\)|([^,]*)", 1));
		elementParsers.add(new DAFRegexElementParser(",[^,]+=([^,]*)|,([^,]*)" /*, 44 */));
		vespersBackgroundRecordParser.setElementParsers(elementParsers);
		
		mapXYVespersToPDSConverterFactory.setCustomRecordParsers(Collections.singletonList((DAFRecordParser)vespersBackgroundRecordParser));		
		
		// Parameters for the Sample Position //
		
		// posXSetpointOptions //
		
		options = new ArrayList<String>();
		options.add("SVM1607-2-B21-02:mm");
		options.add("TS1607-2-B21-01:H:user:mm");
		options.add("TS1607-2-B21-03:H:user:mm");
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(options);
		
		mapXYVespersToPDSConverterFactory.setPosXSetpointOptions(dafEventElementOptions);
		
		// posYSetpointOptions //
		
		options = new ArrayList<String>();
		options.add("SVM1607-2-B21-01:mm");
		options.add("SVM1607-2-B21-03:mm");
		options.add("TS1607-2-B21-01:V:user:mm");
		options.add("TS1607-2-B21-03:V:user:mm");
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(options);
		
		mapXYVespersToPDSConverterFactory.setPosYSetpointOptions(dafEventElementOptions);
		
		// posXFeedbackOptions //
		
		options = new ArrayList<String>();
		options.add("SVM1607-2-B21-02:mm:fbk");
		options.add("TS1607-2-B21-01:H:user:mm:fbk");
		options.add("TS1607-2-B21-03:H:user:mm:fbk");
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(options);
		
		mapXYVespersToPDSConverterFactory.setPosXFeedbackOptions(dafEventElementOptions);
		
		// posYFeedbackOptions //
		
		options = new ArrayList<String>();
		options.add("SVM1607-2-B21-01:mm:fbk");
		options.add("SVM1607-2-B21-03:mm:fbk");
		options.add("TS1607-2-B21-01:V:user:mm:fbk");
		options.add("TS1607-2-B21-03:V:user:mm:fbk");
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(options);
		
		mapXYVespersToPDSConverterFactory.setPosYFeedbackOptions(dafEventElementOptions);
		
		// Beam Current (electron and x-ray) //
		
		// srCurrentOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("PCT1402-01:mA:fbk"));
		
		mapXYVespersToPDSConverterFactory.setSrCurrentOptions(dafEventElementOptions);
		
		// mcsCurrentOptions //

		dafEventElementOptionsList = new ArrayList<DAFEventElementOptions>();
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("BL1607-B2-1:mcs08:fbk"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("BL1607-B2-1:mcs04:fbk"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("BL1607-B2-1:mcs05:fbk"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("BL1607-B2-1:mcs06:fbk"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("BL1607-B2-1:mcs07:fbk"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("BL1607-B2-1:mcs09:fbk"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		mapXYVespersToPDSConverterFactory.setMcsCurrentOptions(dafEventElementOptionsList);
		
		// Parameters for the Single Element Detector /
		
		// sedNChannelsOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:mca1.NUSE"));
		
		mapXYVespersToPDSConverterFactory.setSedNChannelsOptions(dafEventElementOptions);
		
		// sedMaxEnergyOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:dxp1.EMAX_RBV"));
		
		mapXYVespersToPDSConverterFactory.setSedMaxEnergyOptions(dafEventElementOptions);
		
		// sedEnergyGapTimeOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:dxp1.GAPTIM_RBV"));
		
		mapXYVespersToPDSConverterFactory.setSedEnergyGapTimeOptions(dafEventElementOptions);
		
		// sedPresetRealTimeOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:mca1.PRTM"));
		
		mapXYVespersToPDSConverterFactory.setSedPresetRealTimeOptions(dafEventElementOptions);
		
		// sedEnergyPeakingTimeOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:dxp1.PKTIM_RBV"));
		
		mapXYVespersToPDSConverterFactory.setSedEnergyPeakingTimeOptions(dafEventElementOptions);
		
		// sedSpectrumOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:mca1.VAL"));
		
		mapXYVespersToPDSConverterFactory.setSedSpectrumOptions(dafEventElementOptions);
		
		// sedFastCountOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:dxp1.FAST_PEAKS"));
		
		mapXYVespersToPDSConverterFactory.setSedFastCountOptions(dafEventElementOptions);
		
		// sedSlowCountOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:dxp1.SLOW_PEAKS"));
		
		mapXYVespersToPDSConverterFactory.setSedSlowCountOptions(dafEventElementOptions);
		
		// sedDeadTimePctOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:mca1.DTIM"));
		
		mapXYVespersToPDSConverterFactory.setSedDeadTimePctOptions(dafEventElementOptions);
		
		// sedElapsedRealTimeOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:mca1.ERTM"));
		
		mapXYVespersToPDSConverterFactory.setSedElapsedRealTimeOptions(dafEventElementOptions);
			
		// sedElapsedLiveTimeOptions //
			
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("IOC1607-004:mca1.ELTM"));
		
		mapXYVespersToPDSConverterFactory.setSedElpasedLiveTimeOptions(dafEventElementOptions);
		
		// sedDefaultNChannels //
		
		mapXYVespersToPDSConverterFactory.setSedDefaultNChannels(2048);
		
		// Parameters for the Four Element Detector //
		
		// fedNChannelsOptions //
			
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mcaCorrected.NUSE"));
		
		mapXYVespersToPDSConverterFactory.setFedNChannelsOptions(dafEventElementOptions);
		
		// fedMaxEnergyOptions //
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mcaEMax"));
		
		mapXYVespersToPDSConverterFactory.setFedMaxEnergyOptions(dafEventElementOptions);
		
		// fedEnergyGapTimeOptions //
			
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp1.GAPTIM"));
		
		mapXYVespersToPDSConverterFactory.setFedEnergyGapTimeOptions(dafEventElementOptions);
					
		// fedPresetRealTimeOptions //
			
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:PresetReal"));
		
		mapXYVespersToPDSConverterFactory.setFedPresetRealTimeOptions(dafEventElementOptions);
			
		// fedEnergyPeakingTimeOptions //
			
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:EnergyPkTime"));
		
		mapXYVespersToPDSConverterFactory.setFedEnergyPeakingTimeOptions(dafEventElementOptions);
			
		// fedSumSpectrumOptions //
			
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mcaCorrected.VAL"));
		
		mapXYVespersToPDSConverterFactory.setFedSumSpectrumOptions(dafEventElementOptions);
			
		// fedSpectrumOptions //
		
		dafEventElementOptionsList = new ArrayList<DAFEventElementOptions>();
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca1.VAL"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca2.VAL"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca3.VAL"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca4.VAL"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		mapXYVespersToPDSConverterFactory.setFedSpectrumOptions(dafEventElementOptionsList);
		
		// fedFastCountOptions //
		
		dafEventElementOptionsList = new ArrayList<DAFEventElementOptions>();
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp1.FAST_PEAKS"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp2.FAST_PEAKS"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp3.FAST_PEAKS"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp4.FAST_PEAKS"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		mapXYVespersToPDSConverterFactory.setFedFastCountOptions(dafEventElementOptionsList);
		
		// fedSlowCountOptions //
		
		dafEventElementOptionsList = new ArrayList<DAFEventElementOptions>();
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp1.SLOW_PEAKS"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp2.SLOW_PEAKS"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp3.SLOW_PEAKS"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp4.SLOW_PEAKS"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		mapXYVespersToPDSConverterFactory.setFedSlowCountOptions(dafEventElementOptionsList);

		// fedDeadTimePctOptions //
		
		dafEventElementOptionsList = new ArrayList<DAFEventElementOptions>();
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp1:SlowDTP"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp2:SlowDTP"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp3:SlowDTP"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:dxp4:SlowDTP"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		mapXYVespersToPDSConverterFactory.setFedDeadTimePctOptions(dafEventElementOptionsList);
		
		// fedElapsedRealTimeOptions //
	
		dafEventElementOptionsList = new ArrayList<DAFEventElementOptions>();
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca1.ERTM"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca2.ERTM"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca3.ERTM"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca4.ERTM"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		mapXYVespersToPDSConverterFactory.setFedElapsedRealTimeOptions(dafEventElementOptionsList);
		
		// fedElapsedLiveTimeOptions //
		
		dafEventElementOptionsList = new ArrayList<DAFEventElementOptions>();
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca1.ELTM"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca2.ELTM"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca3.ELTM"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		dafEventElementOptions = new DAFEventElementOptions();
		dafEventElementOptions.setOptionsExact(Collections.singletonList("dxp1607-B21-04:mca4.ELTM"));
		dafEventElementOptionsList.add(dafEventElementOptions);
		
		mapXYVespersToPDSConverterFactory.setFedElpasedLiveTimeOptions(dafEventElementOptionsList);
		
		// fedDefaultNChannels //
		
		mapXYVespersToPDSConverterFactory.setFedDefaultNChannels(2048);
		
		// setup partial request // 
		
		request = new LinkedHashConverterMap("DAF", "PDS");
		request.put(REQUEST_KEY_INSTRUMENT_NAME, "Microprobe");
		request.put(REQUEST_KEY_LABORATORY_NAME, "VESPERS");
		request.put(REQUEST_KEY_TECHNIQUE_NAME, "XRF");
		request.put(REQUEST_KEY_FACILITY_NAME, "CLS");
		
		// done config //
		
		CONVERTER_FACTORY.add(new PartialRequestDelegatingConverterFactory(request, mapXYVespersToPDSConverterFactory));
	}

	@Override
	public List<String> getFileExtensions() {
		List<String> rawExts = Collections.singletonList(FILE_NAME_SUFFIX_DAF_DATA);
		List<String> exts = new LinkedList<String>();
		
		String ext;
		for (int i = 0; i < rawExts.size(); i++)
		{
			ext = rawExts.get(i);
			if (ext.startsWith(".")) ext = ext.substring(1);
			exts.add(ext);
		}
		
		return exts;
	}
	
	@Override
	public boolean canRead(String filename) {
		return canRead(Collections.singletonList(filename));
	}

	@Override
	public boolean canRead(List<String> filenames) {
		try {
			doCanRead(filenames);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	@Override
	public void read(String filename) throws Exception {
		read(Collections.singletonList(filename));
	}

	@Override
	public void read(List<String> filenames) throws Exception { 
		Converter converter = CONVERTER_FACTORY.getConverter(doCanRead(filenames));
		Object dataSource =	converter.convert().get(RESPONSE_KEY_PEAKABOO_DATA_SOURCE);
		if(dataSource instanceof DataSource) {
			setDataSource((DataSource)dataSource);
		} else {
			throw new ConverterException("Converter response does not contain a Peakaboo DataSource.");
		}
	}
	
	private ConverterMap doCanRead(List<String> filenames) throws Exception {
		
		String specFilename = null;
		String dataFilename = null;
		
		for(String filename : filenames) {
			if(filename.endsWith(FILE_NAME_SUFFIX_DAF_SPEC)) {
				if(specFilename == null) {
					specFilename = filename;
				}
			}
			else if(filename.endsWith(FILE_NAME_SUFFIX_DAF_DATA)) {
				if(dataFilename == null) {
					dataFilename = filename;
				}
			}
			
			if((specFilename != null) && (dataFilename != null)) {
				break;
			}
		}
		
		if((specFilename != null) && (dataFilename == null)) {
			int index = specFilename.lastIndexOf(FILE_NAME_SUFFIX_DAF_SPEC);
			dataFilename = specFilename.substring(0, index) + FILE_NAME_SUFFIX_DAF_DATA;
		}
		
		if((specFilename == null) && (dataFilename != null)) {
			int index = dataFilename.lastIndexOf(FILE_NAME_SUFFIX_DAF_DATA);
			specFilename = dataFilename.substring(0, index) + FILE_NAME_SUFFIX_DAF_SPEC;
		}
		
		if(specFilename == null) {
			throw new IllegalArgumentException("Spectra file (" + FILE_NAME_SUFFIX_DAF_SPEC + ") must be specified.");
		}
		
		if(dataFilename == null) {
			throw new IllegalArgumentException("Data file (" + FILE_NAME_SUFFIX_DAF_DATA +") must be specified.");
		}
		
		File specFile = new File(specFilename);
		if(!specFile.canRead()) {
			throw new IllegalArgumentException("Spectra file (" + FILE_NAME_SUFFIX_DAF_SPEC +") cannot be read.");
		}
		
		File dataFile = new File(dataFilename);
		if(!dataFile.canRead()) {
			throw new IllegalArgumentException("Data file (" + FILE_NAME_SUFFIX_DAF_DATA + ") cannot be read.");
		}
		
		BufferedReader dataFileReader  = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
		
		String line;
		try {
			line = dataFileReader.readLine().trim();
			while((line != null) && (line.length() == 0)) {
				line = dataFileReader.readLine().trim();
			}
		}
		finally {
			dataFileReader.close();
		}
		
		if(!DATA_FILE_FIRST_LINE.equals(line)) {
			throw new IllegalArgumentException("Data file (" + FILE_NAME_SUFFIX_DAF_DATA + ") has invalid first line.");
		}
		
		ConverterMap request = new LinkedHashConverterMap();
		request.put(REQUEST_KEY_DAF_DATA_FILE, dataFile);
		request.put(REQUEST_KEY_DAF_SPEC_FILE, specFile);
		return request;
	}
	
	private static class PartialRequestDelegatingConverterFactory implements ConverterFactory {
		
		private ConverterMap partialRequest;
		private ConverterFactory converterFactory;
		
		public PartialRequestDelegatingConverterFactory(ConverterMap partialRequest, ConverterFactory converterFactory) {
			this.partialRequest = partialRequest;
			this.converterFactory = converterFactory;
		}

		@Override
		public Converter getConverter(ConverterMap request) throws ConverterFactoryException {
			ConverterMap completeRequest = new LinkedHashConverterMap(partialRequest);
			completeRequest.putAll(request);
			return converterFactory.getConverter(completeRequest);
		}
	}
}
