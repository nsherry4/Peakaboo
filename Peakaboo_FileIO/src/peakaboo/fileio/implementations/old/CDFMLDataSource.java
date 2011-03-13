package peakaboo.fileio.implementations.old;



import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import commonenvironment.AbstractFile;

import fava.datatypes.Bounds;

import peakaboo.fileio.DataSource;
import peakaboo.fileio.DataSourceDimensions;
import peakaboo.fileio.DataSourceExtendedInformation;
import scitypes.Coord;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

import static peakaboo.fileio.implementations.support.CDFML.*;

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



	public static CDFMLDataSource getCDFMLFromFile(AbstractFile file)
	{
				
		Document dom;
		try {
			dom = createCDFMLDocument(file);
		} catch (Exception e) {
			return null;
		}

		
		
		CDFMLDataSource cdfmlDS = new CDFMLDataSource();
		

		//get the root element for the document
		cdfmlDS.root = dom.getDocumentElement();

		//get the elements for the variables PositionX, PositionY, and Spectrums
		//as well as the sub-element which contains the recordset.
		cdfmlDS.positionX = getVariableByName(cdfmlDS.root, VAR_X_POSITONS);
		//cdfmlDS.positionXData = getDataElementFromVariableElement(cdfmlDS.positionX);
		
		cdfmlDS.positionY = getVariableByName(cdfmlDS.root, VAR_Y_POSITONS);
		//cdfmlDS.positionYData = getDataElementFromVariableElement(cdfmlDS.positionY);
		
		cdfmlDS.scanValues = getVariableByName(cdfmlDS.root, VAR_XRF_SPECTRUMS);
		cdfmlDS.scanValuesData = getDataElementFromVariableElement(cdfmlDS.scanValues);
		
		Element normalise = getVariableByName(cdfmlDS.root, VAR_NORMALISE);
		if (normalise != null){
			cdfmlDS.normaliseData = getDataElementFromVariableElement(normalise);
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
		
		String scanString = getStringRecordFromVariableData(scanValuesData, index);
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

		float x = Float.parseFloat(getStringRecordFromVariableData(positionX, index));
		float y = Float.parseFloat(getStringRecordFromVariableData(positionY, index));

		return new Coord<Number>(x, y);

	}


	public Coord<Bounds<Number>> getRealDimensions()
	{
		float x1, x2, y1, y2;

		x1 = Float.parseFloat(getAttributeValue(root, ATTR_DIM_X_START, 0));
		x2 = Float.parseFloat(getAttributeValue(root, ATTR_DIM_X_END, 0));
		y1 = Float.parseFloat(getAttributeValue(root, ATTR_DIM_Y_START, 0));
		y2 = Float.parseFloat(getAttributeValue(root, ATTR_DIM_Y_END, 0));
		

		Bounds<Number> xDim = new Bounds<Number>(x1, x2);
		Bounds<Number> yDim = new Bounds<Number>(y1, y2);
		return new Coord<Bounds<Number>>(xDim, yDim);
	}


	public String getRealDimensionsUnit()
	{
		return getAttributeValue(root, ATTR_DIM_X_START, 1);
	}


	public int getScanCount()
	{

		String attribute = getTagAttribute(scanValues, TAG_VAR_INFO, XML_ATTR_NUMRECORDS);
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
		int width = Integer.parseInt(getAttributeValue(root, ATTR_DATA_X, 0));
		return width;
	}


	private int getDataHeight()
	{
		int height = Integer.parseInt(getAttributeValue(root, ATTR_DATA_Y, 0));
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
		String maxEnergyValue = getAttributeValue(root, ATTR_XRF_MAX_ENERGY, 0);
		if (maxEnergyValue == null) return 20.48f;
		return Float.parseFloat(maxEnergyValue) / 1000.0f;
	}


	public List<String> getScanNames()
	{
		List<String> scannames = new ArrayList<String>();

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
		String Project = getAttributeValue(root, ATTR_PROJECT_NAME, 0);
		String DatasetName = getAttributeValue(root, ATTR_DATASET_NAME, 0);
		String SampleName = getAttributeValue(root, ATTR_SAMPLE_NAME, 0);
		
		String name = "";
		
		if (Project == null) return name;
		name += Project;
		
		if (DatasetName == null) return name;
		name += ": " + DatasetName;
		
		if (SampleName == null) return name;
		name += " on " + SampleName;
		
		return name;
		
		//return getAttributeValue(root, ATTR_PROJECT_NAME, 0) + ": " + getAttributeValue(root, ATTR_DATASET_NAME, 0) + " on " + getAttributeValue(root, ATTR_SAMPLE_NAME, 0);
	}


	public String getCreationTime()
	{
		return getAttributeValue(root, ATTR_CREATION_TIME, 0);
	}


	public String getCreator()
	{
		return getAttributeValue(root, ATTR_CREATOR, 0);
	}


	public String getEndTime()
	{
		return getAttributeValue(root, ATTR_END_TIME, 0);
	}


	public String getExperimentName()
	{
		return getAttributeValue(root, ATTR_EXPERIMENT_NAME, 0);
	}


	public String getFacilityName()
	{
		return getAttributeValue(root, ATTR_FACILITY, 0);
	}


	public String getInstrumentName()
	{
		return getAttributeValue(root, ATTR_INSTRUMENT, 0);
	}


	public String getLaboratoryName()
	{
		return getAttributeValue(root, ATTR_LABORATORY, 0);
	}


	public String getProjectName()
	{
		return getAttributeValue(root, ATTR_PROJECT_NAME, 0);
	}


	public String getSampleName()
	{
		return getAttributeValue(root, ATTR_SAMPLE_NAME, 0);
	}


	public String getScanName()
	{
		return getAttributeValue(root, ATTR_DATASET_NAME, 0);
	}


	public String getSessionName()
	{
		return getAttributeValue(root, ATTR_SESSION_NAME, 0);
	}


	public String getStartTime()
	{
		return getAttributeValue(root, ATTR_START_TIME, 0);
	}


	public String getTechniqueName()
	{
		return getAttributeValue(root, ATTR_TECHNIQUE, 0);
	}


	/**
	 * Returns normalisation data for this data set. If beam intensity fluxuated, this should correct for that.
	 * @return a list of beam intensites -- one for each scan
	 */
	public Spectrum getNormalisationData()
	{
		Spectrum normaliser = new Spectrum(getScanCount());
			
		for (int i = 0; i < getScanCount(); i++){
			normaliser.set(i, Float.parseFloat(getStringRecordFromVariableData(normaliseData, i)) );
		}
				
		SpectrumCalculations.normalize_inplace(normaliser);
				
		return normaliser;
	}


	public boolean hasExtendedInformation()
	{
		List<String> components = getAttributeValues(root, ATTR_TOC);
		if (components.contains(TOC_MODEL)) return true;
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	public static Element getAttributeElement(Element root, String attributeName){
		
		Element attrs = (Element) root.getElementsByTagName(TAG_ATTRS).item(0);
		
		Element pxAttr = getElementFromSetByAttribute(attrs.getElementsByTagName(TAG_ATTR), XML_ATTR_NAME,
				attributeName);
		if (pxAttr == null) return null;
		
		return pxAttr;
		
	}

	public static String getAttributeValue(Element root, String attributeName, int entryNumber)
	{

		Element pxAttr = getAttributeElement(root, attributeName);
		if (pxAttr == null) return null;
		
		NodeList entries = pxAttr.getElementsByTagName(TAG_ENTRY);

		Node entry = entries.item(entryNumber);
		if (entry == null) return null;

		Node value = entry.getChildNodes().item(0);
		if (value == null) return null;

		return value.getNodeValue();
	}
	
	public static List<String> getAttributeValues(Element root, String attributeName)
	{
		
		Element pxAttr = getAttributeElement(root, attributeName);
		if (pxAttr == null) return null;
		
		NodeList entries = pxAttr.getElementsByTagName(TAG_ENTRY);
		
		Node entry, value;
		List<String> values = new ArrayList<String>();
		
		for (int i = 0; i < entries.getLength(); i++){
			
			entry = entries.item(i);
			if (entry == null) continue;
			
			value = entry.getChildNodes().item(0);
			if (value == null) continue;
			
			values.add(value.getNodeValue());
			
		}
		
		return values;
		
	}
	



	/**
	 * Returns the string held in a given record number
	 * 
	 * @param variableData
	 *            the recordset (aka cdfVarData)
	 * @param recordNumber
	 *            the record number
	 * @return a String representation of the value for the record
	 */
	public static String getStringRecordFromVariableData(Element variableData, int recordNumber)
	{

		NodeList records = variableData.getElementsByTagName(TAG_VAR_RECORD);

		Element recElement;
		int curRecNum;

		for (int i = 0; i < records.getLength(); i++) {
			recElement = (Element) records.item(i);
			curRecNum = Integer.parseInt(recElement.getAttribute(XML_ATTR_RECORD_NUMBER));
			if (curRecNum == recordNumber) {

				Node child = recElement.getFirstChild();
				String result = child.getNodeValue();

				return result;
			}
		}

		return null;
	}


	public static String getTagAttribute(Element element, String tag, String attribute)
	{
		Element varInfo = (Element) element.getElementsByTagName(tag).item(0);
		return varInfo.getAttribute(attribute);
	}



	public static Element getElementFromSetByAttribute(NodeList nodes, String attribute, String match)
	{

		Element e;

		for (int i = 0; i < nodes.getLength(); i++) {
			e = (Element) nodes.item(i);
			if (match.equals(e.getAttribute(attribute))) {
				return e;
			}
		}

		return null;

	}


	public static Element getVariableByName(Element root, String variableName)
	{

		// get the <cdfVariables> tag
		Element variableSetTag = (Element) root.getElementsByTagName(TAG_VARS).item(0);

		// get the list of <variable> tags
		NodeList variableTagList = variableSetTag.getElementsByTagName(TAG_VAR);

		Element variableElement;
		for (int i = 0; i < variableTagList.getLength(); i++) {

			variableElement = (Element) variableTagList.item(i);

			if (variableName.equals(variableElement.getAttribute(XML_ATTR_NAME))) {
				return variableElement;
			}

		}

		return null;

	}


	public static Element getDataElementFromVariableElement(Element variableElement)
	{
		return (Element) variableElement.getElementsByTagName(TAG_VAR_DATA).item(0);
	}


	public static int isCDFML(AbstractFile filename, String technique)
	{

		Document dom;
		try {
			dom = createCDFMLDocument(filename);
		} catch (Exception e) {
			return -1;
		}

		if (dom == null) return -2;

		Element root = dom.getDocumentElement();
		if (root == null) return -3;

		//make sure root name matches
		if (!CDF_ROOT_NAME.equals(root.getNodeName())) return -4;
		
		//check the TOC
		if (getAttributeElement(root, ATTR_TOC) == null) return -5;
		List<String> components = getAttributeValues(root, ATTR_TOC);
		if (! components.contains(TOC_MAP)) return -6;
		if (! components.contains(TOC_XRF)) return -7;
		
		//check that variables we need exist
		if (getVariableByName(root, VAR_XRF_SPECTRUMS) == null) return -8;
		if (getVariableByName(root, VAR_X_POSITONS) == null) return -9;
		if (getVariableByName(root, VAR_Y_POSITONS) == null) return -10;
		
		//check that attributes we need exist
		if (getAttributeElement(root, ATTR_DATA_X) == null) return -11;
		if (getAttributeElement(root, ATTR_DATA_Y) == null) return -12;
		
		if (getAttributeElement(root, ATTR_DIM_X_START) == null) return -13;
		if (getAttributeElement(root, ATTR_DIM_X_END) == null) return -14;
		
		if (getAttributeElement(root, ATTR_DIM_Y_START) == null) return -15;
		if (getAttributeElement(root, ATTR_DIM_Y_END) == null) return -16;
		
		
		//String givenTechnique = getAttributeValue(root, ATTR_TECHNIQUE, 0);
		//if (!technique.equals(givenTechnique)) return false;

		return 0;

	}


	public static Document createCDFMLDocument(AbstractFile file) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


		DocumentBuilder db;

		
		db = dbf.newDocumentBuilder();
				
		//File f = new File(filename);
		Document dom = db.parse(file.getInputStream());
		
		return dom;

	}
	
	
}
