package peakaboo.fileio;



import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import commonenvironment.AbstractFile;

import fava.datatypes.Bounds;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.fileio.support.CDFML;
import scitypes.Coord;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class CDFMLDataSource implements DataSource, DataSourceDimensions, DataSourceExtendedInformation
{


	// Various sections of the XML file
	private Element				root;
	private Element				positionX;//, positionXData;
	private Element				positionY;//, positionYData;
	private Element				scanValues, scanValuesData;
	
	private boolean				isNormalised;
	private Element				normaliseData;
	private Spectrum			iNaught;


	//private String				cdfFileName;


	public static int isCDFML(AbstractFile filename)
	{

		return CDFML.isCDFML(filename, CDFML.CDF_TECHNIQUE_XRF);
		
	}


	public static CDFMLDataSource getCDFMLFromFile(AbstractFile file)
	{
				
		Document dom;
		try {
			dom = CDFML.createCDFMLDocument(file);
		} catch (Exception e) {
			return null;
		}

		
		
		CDFMLDataSource cdfmlDS = new CDFMLDataSource();
		

		//get the root element for the document
		cdfmlDS.root = dom.getDocumentElement();

		//get the elements for the variables PositionX, PositionY, and Spectrums
		//as well as the sub-element which contains the recordset.
		cdfmlDS.positionX = CDFML.getVariableByName(cdfmlDS.root, CDFML.X_POSITONS);
		//cdfmlDS.positionXData = CDFML.getDataElementFromVariableElement(cdfmlDS.positionX);
		
		cdfmlDS.positionY = CDFML.getVariableByName(cdfmlDS.root, CDFML.Y_POSITONS);
		//cdfmlDS.positionYData = CDFML.getDataElementFromVariableElement(cdfmlDS.positionY);
		
		cdfmlDS.scanValues = CDFML.getVariableByName(cdfmlDS.root, CDFML.SPECTRUMS);
		cdfmlDS.scanValuesData = CDFML.getDataElementFromVariableElement(cdfmlDS.scanValues);
		
		Element normalise = CDFML.getVariableByName(cdfmlDS.root, CDFML.NORMALISE);
		if (normalise != null){
			cdfmlDS.normaliseData = CDFML.getDataElementFromVariableElement(normalise);
			cdfmlDS.isNormalised = true;
		} else {
			cdfmlDS.isNormalised = false;
		}
		
		//cdfmlDS.cdfFileName = filename;

		return cdfmlDS;


	}


	protected CDFMLDataSource()
	{

	}


	/**
	 * Returns the values for the scan at the given index
	 * 
	 * @param index
	 *            the index of the scan to retrieve
	 * @return the values for the given scan
	 */
	public Spectrum getScanAtIndex(int index)
	{

		if (iNaught == null && isNormalised) {
			iNaught = getNormalisationData();
			//Common.listToFile("/home/nathaniel/Desktop/iNaught", iNaught);
		}
		
		String scanString = CDFML.getStringRecordFromVariableData(scanValuesData, index);
		if (scanString == null) return null;

		String[] scanPoints = scanString.split(" ");
		Spectrum results = new Spectrum(scanPoints.length);
		for (int i = 0; i < scanPoints.length; i++) {
			results.set(i, Float.parseFloat(scanPoints[i]));
		}
		
		if (iNaught != null && isNormalised) {
			
			if (iNaught.get(index) != 0)
				results = SpectrumCalculations.divideBy(results, iNaught.get(index));
			else
				results = SpectrumCalculations.multiplyBy(results, 0);
		}

		return results;
	}


	/**
	 * Returns the coordinates for the scan of the given index
	 * 
	 * @param index
	 *            which scan to get coordinates for
	 * @return a {@link Coord} containing the coordinates for the scan
	 */
	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{

		float x = Float.parseFloat(CDFML.getStringRecordFromVariableData(positionX, index));
		float y = Float.parseFloat(CDFML.getStringRecordFromVariableData(positionY, index));

		return new Coord<Number>(x, y);

	}


	public Coord<Bounds<Number>> getRealDimensions()
	{
		float x1, x2, y1, y2;

		x1 = Float.parseFloat(CDFML.getAttributeValue(root, CDFML.ATTR_DIM_X_START, 0));
		x2 = Float.parseFloat(CDFML.getAttributeValue(root, CDFML.ATTR_DIM_X_END, 0));
		y1 = Float.parseFloat(CDFML.getAttributeValue(root, CDFML.ATTR_DIM_Y_START, 0));
		y2 = Float.parseFloat(CDFML.getAttributeValue(root, CDFML.ATTR_DIM_Y_END, 0));
		

		Bounds<Number> xDim = new Bounds<Number>(x1, x2);
		Bounds<Number> yDim = new Bounds<Number>(y1, y2);
		return new Coord<Bounds<Number>>(xDim, yDim);
	}


	public String getRealDimensionsUnit()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_DIM_X_START, 1);
	}


	public int getScanCount()
	{

		String attribute = CDFML.getTagAttribute(scanValues, CDFML.VAR_INFO_TAG, CDFML.ATTR_NUMRECORDS_ATTR);
		if (attribute == null) return -1;
		return Integer.parseInt(attribute);

	}
	
	public int getExpectedScanCount()
	{
		Coord<Integer> dims = getDataDimensions();
		return dims.x * dims.y;
	}


	private int getDataWidth()
	{
		int width = Integer.parseInt(CDFML.getAttributeValue(root, CDFML.ATTR_DATAX, 0));
		return width;
	}


	private int getDataHeight()
	{
		int height = Integer.parseInt(CDFML.getAttributeValue(root, CDFML.ATTR_DATAY, 0));
		return height;
	}


	public Coord<Integer> getDataDimensions()
	{
		int width = getDataWidth();
		int height = getDataHeight();
		return new Coord<Integer>(width, height);
	}


	public float getMaxEnergy()
	{
		String maxEnergyValue = CDFML.getAttributeValue(root, CDFML.ATTR_MAX_ENERGY, 0);
		if (maxEnergyValue == null) return 20.48f;
		return Float.parseFloat(maxEnergyValue) / 1000.0f;
	}


	public List<String> getScanNames()
	{
		List<String> scannames = DataTypeFactory.<String> list();

		for (int i = 0; i < getScanCount(); i++) {
			scannames.add("Scan #" + (i + 1));
		}

		return scannames;
	}


	public void markScanAsBad(int index)
	{
		// scans in CDFML aren't bad -- if this changes, then this feature can be implemented
	}


	public String getDatasetName()
	{
		String Project = CDFML.getAttributeValue(root, CDFML.ATTR_PROJECT_NAME, 0);
		String DatasetName = CDFML.getAttributeValue(root, CDFML.ATTR_DATASET_NAME, 0);
		String SampleName = CDFML.getAttributeValue(root, CDFML.ATTR_SAMPLE_NAME, 0);
		
		String name = "";
		
		if (Project == null) return name;
		name += Project;
		
		if (DatasetName == null) return name;
		name += ": " + DatasetName;
		
		if (SampleName == null) return name;
		name += " on " + SampleName;
		
		return name;
		
		//return CDFML.getAttributeValue(root, CDFML.ATTR_PROJECT_NAME, 0) + ": " + CDFML.getAttributeValue(root, CDFML.ATTR_DATASET_NAME, 0) + " on " + CDFML.getAttributeValue(root, CDFML.ATTR_SAMPLE_NAME, 0);
	}


	public String getCreationTime()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_CREATION_TIME, 0);
	}


	public String getCreator()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_CREATOR, 0);
	}


	public String getEndTime()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_END_TIME, 0);
	}


	public String getExperimentName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_EXPERIMENT_NAME, 0);
	}


	public String getFacilityName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_FACILITY, 0);
	}


	public String getInstrumentName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_INSTRUMENT, 0);
	}


	public String getLaboratoryName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_LABORATORY, 0);
	}


	public String getProjectName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_PROJECT_NAME, 0);
	}


	public String getSampleName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_SAMPLE_NAME, 0);
	}


	public String getScanName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_DATASET_NAME, 0);
	}


	public String getSessionName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_SESSION_NAME, 0);
	}


	public String getStartTime()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_START_TIME, 0);
	}


	public String getTechniqueName()
	{
		return CDFML.getAttributeValue(root, CDFML.ATTR_TECHNIQUE, 0);
	}


	/**
	 * Returns normalisation data for this data set. If beam intensity fluxuated, this should correct for that.
	 * @return a list of beam intensites -- one for each scan
	 */
	public Spectrum getNormalisationData()
	{
		Spectrum normaliser = new Spectrum(getScanCount());
			
		for (int i = 0; i < getScanCount(); i++){
			normaliser.set(i, Float.parseFloat(CDFML.getStringRecordFromVariableData(normaliseData, i)) );
		}
				
		SpectrumCalculations.normalize_inplace(normaliser);
				
		return normaliser;
	}


	public boolean hasExtendedInformation()
	{
		List<String> components = CDFML.getAttributeValues(root, CDFML.ATTR_TOC);
		if (components.contains(CDFML.TOC_MODEL)) return true;
		return false;
	}


	public int estimateDataSourceSize()
	{
		Spectrum s = getScanAtIndex(0);
		return s.size() * getScanCount();
	}


	public boolean hasRealDimensions()
	{
		return true;
	}

	
}
