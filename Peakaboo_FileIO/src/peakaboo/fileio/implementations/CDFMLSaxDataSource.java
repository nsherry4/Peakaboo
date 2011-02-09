package peakaboo.fileio.implementations;



import java.util.ArrayList;
import java.util.List;


import commonenvironment.AbstractFile;


import fava.datatypes.Bounds;
import fava.datatypes.Range;
import fava.signatures.FnEach;
import fava.signatures.FnGet;


import peakaboo.common.Version;
import peakaboo.fileio.DataSource;
import peakaboo.fileio.DataSourceDimensions;
import peakaboo.fileio.DataSourceExtendedInformation;
import peakaboo.fileio.implementations.support.CDFML;
import scitypes.Coord;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;
import scitypes.filebacked.FileBackedList;



public class CDFMLSaxDataSource extends CDFMLReader implements DataSource, DataSourceDimensions,
		DataSourceExtendedInformation
{

	FnEach<Integer>								getScanCountCallback;
	FnEach<Integer>								readScanCallback;
	int											scanReadCount;

	//FileBackedList, if it could be created. ArrayList of not
	List<Spectrum>								correctedData;
	Spectrum									iNaughtNormalized;
	

	public CDFMLSaxDataSource(AbstractFile file, FnEach<Integer> getScanCountCallback, FnEach<Integer> readScanCallback, FnGet<Boolean> isAborted) throws Exception
	{
		super();
		
		this.readScanCallback = readScanCallback;
		this.getScanCountCallback = getScanCountCallback;
		
		read(file, isAborted);
		
		correctedData = FileBackedList.<Spectrum>create(Version.program_name + " - Corrected Spectrum");

	}
	
	
	private boolean isNewVersion()
	{
		
		if (hasVar(CDFML.VAR_MCA_SPECTRUM + "0")) return true;
		return false;
	}
	
	private int numElements()
	{
		if (isNewVersion())
			return getAttrInt(CDFML.ATTR_MCA_NUM_ELEMENTS, 0);
		else
			return 1;
	}
	
	private Spectrum getScan(int element, int index)
	{	
		
		if (hasVar(CDFML.VAR_MCA_SPECTRUM + "0")) {
			return getVarSpectra(CDFML.VAR_MCA_SPECTRUM + element).get(index);	
		} else if (hasVar(CDFML.VAR_XRF_SPECTRUMS)) {
			return getVarSpectra(CDFML.VAR_XRF_SPECTRUMS).get(index);
		} else {
			return null;
		}
		
	}
	
	private Spectrum getScan(int index)
	{
		return getScan(0, index);
	}
	
	private Float getDeadtime(int index)
	{
		return getDeadtime(0, index);
	}
	private Float getDeadtime(int element, int index)
	{
		if (hasVar(CDFML.VAR_MCA_DEADTIME + "0")) {
			if (numElements() == 1 && element == 1){
				return Math.max(0f, getVarFloats(CDFML.VAR_MCA_DEADTIME).get(index) / 100f);
			}
			return Math.max(0f, getVarFloats(CDFML.VAR_MCA_DEADTIME + element).get(index) / 100f);	
		} else {
			return 0f;
		}
		
	}
	
	private Float getINaught(int index)
	{
		if (!hasVar(CDFML.VAR_NORMALISE)) return 1f;
		
		if (iNaughtNormalized == null) {
			iNaughtNormalized = SpectrumCalculations.normalize(new Spectrum(getVarFloats(CDFML.VAR_NORMALISE)));
		}
		
		return iNaughtNormalized.get(index);
		
	}
	
	private int numScans()
	{
		if (hasVar(CDFML.VAR_MCA_SPECTRUM + "0"))
		{
			return getVarAttrInt(CDFML.VAR_MCA_SPECTRUM + "0", CDFML.XML_ATTR_NUMRECORDS);
			
		} else if (hasVar(CDFML.VAR_XRF_SPECTRUMS)) 
		{
			return getVarAttrInt(CDFML.VAR_XRF_SPECTRUMS, CDFML.XML_ATTR_NUMRECORDS);
			
		} else 
		{
			return 0;
		}
	}
	

	
	

	////////////////////////////////////////////////////////////
	// VARIABLES DATA
	////////////////////////////////////////////////////////////

	
	public int estimateDataSourceSize()
	{
		// TODO Auto-generated method stub
		return 0;
	}


	
	public Spectrum getScanAtIndex(int index)
	{
		Spectrum s, s2;
		
		//if this is a multi-element data set, we store the averaged data the 'correctedData' list
		//and the individual spectra in indices 0->N-1. The first time this data is accessed, the
		//'correctedData' index value will be empty, because we won't have calcualted it yet.
		if ( (correctedData.size() <= index || correctedData.get(index) == null) && numElements() > 1)
		{
			
			
			s = new Spectrum(getScan(0, 0).size(), 0f);
			for (Integer i : new Range(0, numElements()-1))
			{
				
				s2 = getScan(i, index);
				
				//divide by deadtime percent if not 0
				if (getDeadtime(i, index) != 0) {
					SpectrumCalculations.multiplyBy_inplace(s2, 1f - getDeadtime(i, index));
				}
				//add the adjusted value to the total
				SpectrumCalculations.addLists_inplace(s, s2);
				
				
			}
			
			float iNaught = getINaught(index);

			if (iNaught != 0) SpectrumCalculations.divideBy_inplace(s, iNaught);
			else SpectrumCalculations.multiplyBy(s, 0);
			
			//commit the newly calculated value to the dataset
			correctedData.set(index, s);
			
			
			
		} else if ( (correctedData.size() <= index || correctedData.get(index) == null) && numElements() == 1) {
			
			
			s = new Spectrum(getScan(index));
			
			//adjust for deadtime
			if (getDeadtime(index) != 0){
				SpectrumCalculations.multiplyBy_inplace(s, 1f - getDeadtime(index));
			}
			
			
			float iNaught = getINaught(index);

			if (iNaught != 0) SpectrumCalculations.divideBy_inplace(s, iNaught);
			else SpectrumCalculations.multiplyBy(s, 0);
			
			//commit the newly calculated value to the dataset
			correctedData.set(index, s);	
			
		}
		

		return correctedData.get(index);
		
	}

	
	public int getScanCount()
	{
		return numScans();
	}
	
	public int getExpectedScanCount()
	{
		Coord<Integer> dims = getDataDimensions();
		return dims.x * dims.y;
	}
	
	
	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{
		Coord<Number> dims = new Coord<Number>(0, 0);

		
		
		dims.x =  getVarFloats(CDFML.VAR_X_POSITONS).get(index);
		dims.y = getVarFloats(CDFML.VAR_Y_POSITONS).get(index);
		return dims;

	}


	
	
	
	
	
	
	
	

	////////////////////////////////////////////////////////////
	// ATTRIBUTE DATA
	////////////////////////////////////////////////////////////



	public String getDatasetName()
	{
		String Project = getAttr(CDFML.ATTR_PROJECT_NAME, 0);
		String DatasetName = getAttr(CDFML.ATTR_DATASET_NAME, 0);
		String SampleName = getAttr(CDFML.ATTR_SAMPLE_NAME, 0);

		String name = "";

		if (Project == null) return name;
		name += Project;

		if (DatasetName == null) return name;
		name += ": " + DatasetName;

		if (SampleName == null) return name;
		name += " on " + SampleName;

		return name;

	}


	
	public float getMaxEnergy()
	{
		
		String maxEnergyValue;
		
		if (hasAttr(CDFML.ATTR_MCA_MAX_ENERGY)) {
			maxEnergyValue = getAttr(CDFML.ATTR_MCA_MAX_ENERGY, 0);
		} else if (hasAttr(CDFML.ATTR_XRF_MAX_ENERGY)) {
			maxEnergyValue = getAttr(CDFML.ATTR_XRF_MAX_ENERGY, 0);
		} else {
			return 0f;
		}
		if (maxEnergyValue == null) return 0f;
		
		return Float.parseFloat(maxEnergyValue) / 1000.0f;
	}


	
	public List<String> getScanNames()
	{
		List<String> scannames = new ArrayList<String>();

		for (int i = 0; i < getScanCount(); i++)
		{
			scannames.add("Scan #" + (i + 1));
		}

		return scannames;
	}


	private int getDataWidth()
	{
		int width = Integer.parseInt(getAttr(CDFML.ATTR_DATA_X, 0));
		return width;
	}


	private int getDataHeight()
	{
		int height = Integer.parseInt(getAttr(CDFML.ATTR_DATA_Y, 0));
		return height;
	}


	
	public Coord<Integer> getDataDimensions()
	{
		int width = getDataWidth();
		int height = getDataHeight();
		return new Coord<Integer>(width, height);
	}


	
	public Coord<Bounds<Number>> getRealDimensions()
	{
		float x1, x2, y1, y2;

		x1 = Float.parseFloat(getAttr(CDFML.ATTR_DIM_X_START, 0));
		x2 = Float.parseFloat(getAttr(CDFML.ATTR_DIM_X_END, 0));
		y1 = Float.parseFloat(getAttr(CDFML.ATTR_DIM_Y_START, 0));
		y2 = Float.parseFloat(getAttr(CDFML.ATTR_DIM_Y_END, 0));


		Bounds<Number> xDim = new Bounds<Number>(x1, x2);
		Bounds<Number> yDim = new Bounds<Number>(y1, y2);
		return new Coord<Bounds<Number>>(xDim, yDim);
	}


	
	public String getRealDimensionsUnit()
	{
		return getAttr(CDFML.ATTR_DIM_X_START, 1);
	}



	
	public String getCreationTime()
	{
		return getAttr(CDFML.ATTR_CREATION_TIME, 0);
	}


	public String getCreator()
	{
		return getAttr(CDFML.ATTR_CREATOR, 0);
	}


	public String getEndTime()
	{
		return getAttr(CDFML.ATTR_END_TIME, 0);
	}



	public String getExperimentName()
	{
		return getAttr(CDFML.ATTR_EXPERIMENT_NAME, 0);
	}


	public String getFacilityName()
	{
		return getAttr(CDFML.ATTR_FACILITY, 0);
	}


	public String getInstrumentName()
	{
		return getAttr(CDFML.ATTR_INSTRUMENT, 0);
	}


	public String getLaboratoryName()
	{
		return getAttr(CDFML.ATTR_LABORATORY, 0);
	}


	public String getProjectName()
	{
		return getAttr(CDFML.ATTR_PROJECT_NAME, 0);
	}


	public String getSampleName()
	{
		return getAttr(CDFML.ATTR_SAMPLE_NAME, 0);
	}


	public String getScanName()
	{
		return getAttr(CDFML.ATTR_DATASET_NAME, 0);
	}


	public String getSessionName()
	{
		return getAttr(CDFML.ATTR_SESSION_NAME, 0);
	}


	public String getStartTime()
	{
		return getAttr(CDFML.ATTR_START_TIME, 0);
	}


	public String getTechniqueName()
	{
		return getAttr(CDFML.ATTR_TECHNIQUE, 0);
	}


	
	public boolean hasExtendedInformation()
	{
		return true;
	}


	public boolean hasRealDimensions()
	{
		return true;
	}

	
	public static boolean filesMatchCriteria(List<AbstractFile> files)
	{
		if (files.size() != 1) return false;
		String ext = files.get(0).getFileName().toLowerCase();
		if (!   (ext.endsWith(".xml") || ext.endsWith(".cdfml"))  ) return false;
		return true;
	}


	@Override
	protected void processedSpectrum(String varname)
	{
		
		//if this is the first scan we're looking at
		if (scanReadCount == 0) {
		
			int totalScanCount = 0;
			
			
			//new version and old version have different criteria for determining how many scans
			if (hasVarAttr(varname, CDFML.XML_ATTR_NUMRECORDS) && hasAttr(CDFML.ATTR_MCA_NUM_ELEMENTS)) {
						
				totalScanCount = getVarAttrInt(varname, CDFML.XML_ATTR_NUMRECORDS) * getAttrInt(CDFML.ATTR_MCA_NUM_ELEMENTS, 0);

			}
			//we assume that in the older version, there will be only one spectrum recordset
			else if (hasVarAttr(varname, CDFML.XML_ATTR_NUMRECORDS)) {
				
				totalScanCount = getVarAttrInt(varname, CDFML.XML_ATTR_NUMRECORDS);
				
			}
			
			getScanCountCallback.f(totalScanCount);
			
			
		}
				
		scanReadCount++;
		readScanCallback.f(1);
		
	}
	
}
