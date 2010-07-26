package peakaboo.fileio.support;


import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import commonenvironment.AbstractFile;

import peakaboo.datatypes.DataTypeFactory;


public class CDFML
{

	// CDFML Markers
	public static final String	CDF_ROOT_NAME				= "CDF";
	public static final String	CDF_TECHNIQUE_XRF			= "XRF";


	// CDFML Tags/Attributes for CDF Attributes
	public static final String	ATTRS_TAG					= "cdfGAttributes";
	public static final String	ATTR_TAG					= "attribute";
	public static final String	ATTR_ENTRY_TAG				= "entry";
	public static final String	ATTR_NAME_ATTR				= "name";
	public static final String	ATTR_NUMRECORDS_ATTR		= "numRecordsAllocate";
	public static final String  ATTR_ENTRY_TAG_NUM			= "entryNum";


	// CDFML Tags/Attributes for CDF Variables
	public static final String	VARS_TAG					= "cdfVariables";
	public static final String	VAR_TAG						= "variable";
	public static final String	VAR_DATA_TAG				= "cdfVarData";
	public static final String	VAR_INFO_TAG				= "cdfVarInfo";
	public static final String	VAR_TAG_ATTR_NAME			= "name";
	public static final String	VAR_DATA_RECORD_TAG			= "record";
	public static final String	VAR_DATA_RECORD_NAME_ATTR	= "recNum";



	// CDF Variable & Attribute Names

	// Variables
	public static final String	X_POSITONS					= "MapXY:X";
	public static final String	Y_POSITONS					= "MapXY:Y";
	public static final String	X_INDEX						= "MapXY:I";
	public static final String	Y_INDEX						= "MapXY:J";
	public static final String	SPECTRUMS					= "XRF:Spectrum";
	public static final String  NORMALISE					= "BeamI:I0";

	// Attributes
	public static final String	ATTR_TOC					= "ScienceStudio";
	public static final String	TOC_MAP						= "MapXY:1.0";
	public static final String	TOC_XRF						= "XRF:1.0";
	public static final String	TOC_MODEL					= "SSModel:1.0";
	
	public static final String	ATTR_DATAX					= "MapXY:SizeX";
	public static final String	ATTR_DATAY					= "MapXY:SizeY";
	public static final String	ATTR_DIM_X_START			= "MapXY:StartX";
	public static final String	ATTR_DIM_X_END				= "MapXY:EndX";
	public static final String	ATTR_DIM_Y_START			= "MapXY:StartY";
	public static final String	ATTR_DIM_Y_END				= "MapXY:EndY";
	
	public static final String	ATTR_CREATION_TIME			= "SS:Created";
	public static final String	ATTR_CREATOR				= "SS:CreatedBy";
	
	public static final String	ATTR_PROJECT_NAME			= "SSModel:ProjectName";
	public static final String	ATTR_SESSION_NAME			= "SSModel:SessionName";
	public static final String	ATTR_FACILITY				= "SSModel:Facility";
	public static final String	ATTR_LABORATORY				= "SSModel:Laboratory";
	public static final String	ATTR_EXPERIMENT_NAME		= "SSModel:ExperimentName";
	public static final String	ATTR_INSTRUMENT				= "SSModel:Instrument";
	public static final String	ATTR_TECHNIQUE				= "SSModel:Technique";
	public static final String	ATTR_DATASET_NAME			= "SSModel:ScanName";
	public static final String	ATTR_SAMPLE_NAME			= "SSModel:SampleName";
	
	public static final String	ATTR_MAX_ENERGY				= "XRF:MaxEnergy";
	
	public static final String	ATTR_START_TIME				= "Scan:StartTime";
	public static final String	ATTR_END_TIME				= "Scan:EndTime";

	
	

	public static Element getAttributeElement(Element root, String attributeName){
		
		Element attrs = (Element) root.getElementsByTagName(ATTRS_TAG).item(0);
		
		Element pxAttr = getElementFromSetByAttribute(attrs.getElementsByTagName(ATTR_TAG), ATTR_NAME_ATTR,
				attributeName);
		if (pxAttr == null) return null;
		
		return pxAttr;
		
	}

	public static String getAttributeValue(Element root, String attributeName, int entryNumber)
	{

		Element pxAttr = getAttributeElement(root, attributeName);
		if (pxAttr == null) return null;
		
		NodeList entries = pxAttr.getElementsByTagName(ATTR_ENTRY_TAG);

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
		
		NodeList entries = pxAttr.getElementsByTagName(ATTR_ENTRY_TAG);
		
		Node entry, value;
		List<String> values = DataTypeFactory.<String>list();
		
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

		NodeList records = variableData.getElementsByTagName(VAR_DATA_RECORD_TAG);

		Element recElement;
		int curRecNum;

		for (int i = 0; i < records.getLength(); i++) {
			recElement = (Element) records.item(i);
			curRecNum = Integer.parseInt(recElement.getAttribute(VAR_DATA_RECORD_NAME_ATTR));
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
		Element variableSetTag = (Element) root.getElementsByTagName(VARS_TAG).item(0);

		// get the list of <variable> tags
		NodeList variableTagList = variableSetTag.getElementsByTagName(VAR_TAG);

		Element variableElement;
		for (int i = 0; i < variableTagList.getLength(); i++) {

			variableElement = (Element) variableTagList.item(i);

			if (variableName.equals(variableElement.getAttribute(VAR_TAG_ATTR_NAME))) {
				return variableElement;
			}

		}

		return null;

	}


	public static Element getDataElementFromVariableElement(Element variableElement)
	{
		return (Element) variableElement.getElementsByTagName(VAR_DATA_TAG).item(0);
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
		if (getVariableByName(root, SPECTRUMS) == null) return -8;
		if (getVariableByName(root, X_POSITONS) == null) return -9;
		if (getVariableByName(root, Y_POSITONS) == null) return -10;
		
		//check that attributes we need exist
		if (getAttributeElement(root, ATTR_DATAX) == null) return -11;
		if (getAttributeElement(root, ATTR_DATAY) == null) return -12;
		
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
