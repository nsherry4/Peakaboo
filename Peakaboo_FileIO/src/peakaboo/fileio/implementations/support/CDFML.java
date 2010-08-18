package peakaboo.fileio.implementations.support;


import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import commonenvironment.AbstractFile;

import peakaboo.common.DataTypeFactory;


public class CDFML
{

	// CDFML Markers
	public static final String	CDF_ROOT_NAME			= "CDF";
	public static final String	CDF_TECHNIQUE_XRF		= "XRF";

	//Attributes
	public static final String	XML_ATTR_NAME			= "name";
	public static final String	XML_ATTR_NUMRECORDS		= "numRecordsAllocate";
	public static final String  XML_ATTR_ENTRYNUM		= "entryNum";
	public static final String	XML_ATTR_RECORD_NUMBER	= "recNum";

	
	// CDFML Tags for CDF Attributes
	public static final String	TAG_ATTRS				= "cdfGAttributes";
	public static final String	TAG_ATTR				= "attribute";
	public static final String	TAG_ENTRY				= "entry";
	

	// CDFML Tags for CDF Variables
	public static final String	TAG_VARS				= "cdfVariables";
	public static final String	TAG_VAR					= "variable";
	public static final String	TAG_VAR_DATA			= "cdfVarData";
	public static final String	TAG_VAR_INFO			= "cdfVarInfo";
	public static final String	TAG_VAR_RECORD			= "record";



	// CDF Variable & Attribute Names

	// Variables
	public static final String	VAR_X_POSITONS			= "MapXY:X";
	public static final String	VAR_Y_POSITONS			= "MapXY:Y";
	public static final String	VAR_X_INDEX				= "MapXY:I";
	public static final String	VAR_Y_INDEX				= "MapXY:J";
	public static final String  VAR_NORMALISE			= "BeamI:I0";

	//Old "XRF" Spectrum Data
	public static final String	TOC_XRF					= "XRF:1.0";
	public static final String	ATTR_XRF_MAX_ENERGY		= "XRF:MaxEnergy";
	public static final String	VAR_XRF_SPECTRUMS		= "XRF:Spectrum";
	
	//new "MCA" Spectrum Data
	public static final String 	TOC_MCA					= "MCA:1.0";
	public static final String	ATTR_MCA_MAX_ENERGY		= "MCA:MaxEnergy";
	public static final String	ATTR_MCA_NUM_ELEMENTS	= "MCA:NElements";
	public static final String	VAR_MCA_DEADTIME		= "MCA:DeadTimePct";
	public static final String	VAR_MCA_SPECTRUM		= "MCA:Spectrum";
	
	
	// Attributes
	public static final String	ATTR_TOC				= "ScienceStudio";
	public static final String	TOC_MAP					= "MapXY:1.0";
	public static final String	TOC_MODEL				= "SSModel:1.0";
	
	public static final String	ATTR_DATA_X				= "MapXY:SizeX";
	public static final String	ATTR_DATA_Y				= "MapXY:SizeY";
	public static final String	ATTR_DIM_X_START		= "MapXY:StartX";
	public static final String	ATTR_DIM_X_END			= "MapXY:EndX";
	public static final String	ATTR_DIM_Y_START		= "MapXY:StartY";
	public static final String	ATTR_DIM_Y_END			= "MapXY:EndY";
	
	public static final String	ATTR_CREATION_TIME		= "SS:Created";
	public static final String	ATTR_CREATOR			= "SS:CreatedBy";
	
	public static final String	ATTR_PROJECT_NAME		= "SSModel:ProjectName";
	public static final String	ATTR_SESSION_NAME		= "SSModel:SessionName";
	public static final String	ATTR_FACILITY			= "SSModel:Facility";
	public static final String	ATTR_LABORATORY			= "SSModel:Laboratory";
	public static final String	ATTR_EXPERIMENT_NAME	= "SSModel:ExperimentName";
	public static final String	ATTR_INSTRUMENT			= "SSModel:Instrument";
	public static final String	ATTR_TECHNIQUE			= "SSModel:Technique";
	public static final String	ATTR_DATASET_NAME		= "SSModel:ScanName";
	public static final String	ATTR_SAMPLE_NAME		= "SSModel:SampleName";
	
	public static final String	ATTR_START_TIME			= "Scan:StartTime";
	public static final String	ATTR_END_TIME			= "Scan:EndTime";

	
	

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
