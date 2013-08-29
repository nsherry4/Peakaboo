package peakaboo.datasource.plugin.plugins.cdfml;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import peakaboo.common.DataTypeFactory;
import peakaboo.common.Version;
import peakaboo.datasource.SpectrumList;
import scitypes.Spectrum;
import fava.Functions;
import fava.datatypes.Pair;
import fava.functionable.FList;
import fava.functionable.Range;
import fava.signatures.FnGet;
import fava.signatures.FnMap;


public abstract class CDFMLReader extends DefaultHandler2
{


	protected static enum CDF_DATATYPE{
		REAL, INTEGER, SPECTRUM
	}
	
	
	private static final String ABORT_MESSAGE = "Aborted by User"; 
	public String sourceFile;
	
	private XMLReader									xr;

	//store each tag and its attributes on the stack
	private Stack<Pair<String, Map<String, String>>>	tagStack;
	
	//attrName -> (entryNo -> contents)
	private Map<String, List<String>>					attrEntries;
	
	//varName -> [record]
	private Map<String, List<?>>						variableEntries;
	//varName -> (attribute -> value)
	private Map<String, Map<String, String>>			variableAttributes;
	//varName -> CDF_DATATYPE
	private Map<String, CDF_DATATYPE>					varTypes;
	
	//when we parse a new tag, we place a 
	//(name, map:(attr -> value)) data structure
	//on tagStack. In order to not need to keep 
	//recreating that data structure, we 
	//create them as needed 1 per entry on the stack
	//and then reuse them later by storing them 
	//here, too
	private List<Pair<String, Map<String, String>>>		tagStackMapsList;
	
	
	private FnGet<Boolean> 								isAborted;
	
	private boolean										isEntry, isRecord;
	private String										currentVarName;
	private int											entryNo;

	private StringBuilder								sb;
	

	private String										attrPath		= CDFMLStrings.CDF_ROOT_NAME + "/" + CDFMLStrings.TAG_ATTRS + "/"
																		+ CDFMLStrings.TAG_ATTR;
	private String										attrEntryPath	= attrPath + "/" + CDFMLStrings.TAG_ENTRY;

	private String										varPath			= CDFMLStrings.CDF_ROOT_NAME + "/" + CDFMLStrings.TAG_VARS + "/"
																		+ CDFMLStrings.TAG_VAR;
	private String										varDataPath		= varPath + "/" + CDFMLStrings.TAG_VAR_DATA;
	private String										varRecordPath	= varDataPath + "/" + CDFMLStrings.TAG_VAR_RECORD;

	private String										varVarInfo 		= varPath + "/" + CDFMLStrings.TAG_VAR_INFO;
		

	public CDFMLReader()
	{
		super();
	}
	
	public void read(String file, FnGet<Boolean> isAborted) throws Exception
	{
		
		this.isAborted = isAborted;
		this.sourceFile = file;
		
		attrEntries = new HashMap<String, List<String>>();
		variableEntries = new HashMap<String, List<?>>();
		variableAttributes = new HashMap<String, Map<String, String>>();
		tagStack = new Stack<Pair<String, Map<String, String>>>();
		varTypes = new HashMap<String, CDF_DATATYPE>();
		
		tagStackMapsList = new ArrayList<Pair<String, Map<String, String>>>(20);
		
		
		try
		{

			xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);

			xr.parse(new InputSource(new FileInputStream(sourceFile)));

		}
		catch (SAXException e)
		{
			if (! e.getMessage().equals(ABORT_MESSAGE)) throw new Exception(e.getMessage());
		}
	}

	@Override
	public void startDocument()
	{
	}

	@Override
	public void endDocument()
	{
	}


	@Override
	public void startElement(String uri, String name, String qName, Attributes atts)
	{


		addTagToPath(name, atts);
		String tag = currentPath();

		isEntry = false;
		isRecord = false;

		if (tag.equals(varRecordPath))
		{
			//this is a variable record
			isRecord = true;
			entryNo = Integer.parseInt(atts.getValue(CDFMLStrings.XML_ATTR_RECORD_NUMBER));
		}
		else if (tag.equals(attrEntryPath))
		{
			//this is an attribute entry
			isEntry = true;
			entryNo = Integer.parseInt(atts.getValue(CDFMLStrings.XML_ATTR_ENTRYNUM)); 
		} 
		else if (tag.equals(varVarInfo)){
			
			//this is a cdfVarInfo tag
			
			String varName = getTagAttribute(CDFMLStrings.TAG_VAR, CDFMLStrings.XML_ATTR_NAME);
			
			Map<String, String> attsMap = new HashMap<String, String>(12);
			for (Integer i : new Range(0, atts.getLength()-1))
			{
				attsMap.put(atts.getQName(i), atts.getValue(i));
			}
			variableAttributes.put(varName, attsMap);
			
		} else if (tag.equals(varPath)) 
		{
			//we're about to parse a lot of records. rather than looking up the name of the variable each time
			//we can look up the variable name once, and check this instead of a lookup per record
			currentVarName = atts.getValue(CDFMLStrings.XML_ATTR_NAME);
		}

		//create a new string builder for the data we are about to receive
		sb = new StringBuilder();

	}

	@Override
	public void characters(char ch[], int start, int length)
	{

		if (isRecord || isEntry)
		{
			
			sb.append(ch, start, length);
			
		}

	}

	@Override
	public void endElement(String uri, String name, String qName) throws SAXException
	{

		if (isEntry)
		{

			recordEntry();

		}
		else if (isRecord)
		{
			
			recordRecord();

			
		}
			

		if (isAborted != null && isAborted.f())
		{
			throw new SAXException(ABORT_MESSAGE);
		}
		

		tagStack.pop();

		isRecord = false;
		isEntry = false;


	}
	
	
	
	
	private String currentPath()
	{	
		
		StringBuilder tagsb = new StringBuilder(100);
		
		for (int i = 0; i < tagStack.size(); i++)
		{
			if (i > 0) tagsb.append("/");
			tagsb.append(tagStack.get(i).first);
			
		}
		
		return tagsb.toString().replace('\n', '\000');
				
	}


	private void addTagToPath(String name, Attributes atts)
	{
		
		Pair<String, Map<String, String>> tagData;
		Map<String, String> attMap;
		
		if (tagStackMapsList.size() < tagStack.size()+1)
		{
			attMap = new HashMap<String, String>(8);
			tagData	= new Pair<String, Map<String, String>>(name, attMap); 
			tagStackMapsList.add(tagData);
			
		} else {
			tagData = tagStackMapsList.get(tagStack.size());
			attMap = tagData.second;
			tagData.first = name;
			attMap.clear();
		}
		
		for (int i = 0; i < atts.getLength(); i++)
		{
			attMap.put(atts.getLocalName(i), atts.getValue(i));
		}
		
		tagStack.push(tagData);
	}
	
	
	
	//we have parsed a <record><entry ... />...</entry></record> pattern
	//so we record the contents of the entry in a 2 deep map of
	// record name -> (entry number -> entry value) with an entry number
	//determined by a value calculated while parsing the tags
	private void recordEntry()
	{
		//get the tag's name attribute
		String tagAttrName = getTagAttribute(CDFMLStrings.TAG_ATTR, CDFMLStrings.XML_ATTR_NAME);

		//loop up the map of entry numbers to entry values for this attr name, create it if it doesnt exist yet
		List<String> entryList = attrEntries.get(tagAttrName);
		if (entryList == null)
		{
			entryList = new FList<String>();
			attrEntries.put(tagAttrName, entryList);
		}

		entryList.set(entryNo, sb.toString());
	}

	//we have parsed a <record ...>...</record> pattern
	//we first check to see if we have already determined and recorded
	//the data type of this variable by looking up the variable name in the 
	// variable name -> CDF_DATATYPE map, calculating and storing it if we haven't
	//then we look up the variable (list of values) in a variable name -> list of values map
	//creating it if it does not exist yet, and add the current value to that list at
	//a position determined by a value calculated while parsing the tags 
	@SuppressWarnings("unchecked")
	private void recordRecord()
	{

		//get the name of the variable that this record belongs to
		String variableName = currentVarName;//getTagAttribute(CDFML.TAG_VAR, CDFML.XML_ATTR_NAME);

		if (variableName == null) return;

		
		CDF_DATATYPE datatype;

		int dimSize = 1;
		if (hasVarAttr(variableName, "dimSizes")) {
			dimSize = getVarAttrInt(variableName, "dimSizes");
		}
		
		if (varTypes.containsKey(variableName)) {
			datatype = varTypes.get(variableName);
		} else {
			
			datatype = CDF_DATATYPE.REAL;
			
			boolean isRealNumber = false;
			if (hasVarAttr(variableName, "cdfDatatype")) isRealNumber = "CDF_DOUBLE".equals(getVarAttr(variableName, "cdfDatatype"));
			
			if (isRealNumber && dimSize == 1) datatype = CDF_DATATYPE.REAL;
			if (!isRealNumber && dimSize == 1) datatype = CDF_DATATYPE.INTEGER;
			if (dimSize > 1) datatype = CDF_DATATYPE.SPECTRUM;
			
			
			varTypes.put(variableName, datatype);
		}
		
		switch (datatype){
			
			case REAL:
				List<Float> fvalues = (List<Float>)variableEntries.get(variableName);
				if (fvalues == null) variableEntries.put(variableName, DataTypeFactory.<Float>listInit(varRecordCount(variableName)));
				fvalues = (List<Float>)variableEntries.get(variableName);
				fvalues.set(entryNo, Float.parseFloat(sb.toString()));
				break;
			case INTEGER:
				List<Integer> ivalues = (List<Integer>)variableEntries.get(variableName);
				if (ivalues == null) variableEntries.put(variableName, DataTypeFactory.<Integer>listInit(varRecordCount(variableName)));
				ivalues = (List<Integer>)variableEntries.get(variableName);
				ivalues.set(entryNo, Integer.parseInt(sb.toString()));
				break;
			case SPECTRUM:
				
				List<Spectrum> svalues = (List<Spectrum>)variableEntries.get(variableName);
				if (svalues == null) {
					svalues = SpectrumList.create(Version.program_name + " - CDFML - " + variableName);
					variableEntries.put(variableName, svalues);
				}
				svalues.set(entryNo, getSpectrumFromString(dimSize, sb.toString()));
				
				
				processedSpectrum(variableName, entryNo, getSpectrumFromString(dimSize, sb.toString()));
				
				break;
			
		}
		
		
	}
	
	
	//as we parse the XML file, we keep track of all the tags we are in the middle of (ie between <X> and </X>)
	//by placing (tagname, map:(attribute -> value)) values on a stack. If we need to look up an attribute of a parent tag
	//we can look it up here by n-levels-up
	/*
	private String getTagAttribute(int levelsUp, String attrName)
	{
		return tagStack.get(tagStack.size() - 1 - levelsUp).second.get(attrName);
	*/
	
	private String getTagAttribute(String tagname, String attrName)
	{
		for (int i = tagStack.size() - 1; i >= 0; i--)
		{
			if (tagStack.get(i).first.equals(tagname)) return tagStack.get(i).second.get(attrName);
		}
		return null;
	}
	
	/*
	private Integer getTagAttributeInt(String tagname, String attrName)
	{
		String result = getTagAttribute(tagname, attrName);
		if (result == null) return null;
		return Integer.parseInt(result);
	}
	
	private Integer getTagAttributeInt(int levelsUp, String attrName)
	{
		String val = getTagAttribute(levelsUp, attrName);
		if (val == null) return null;
		return Integer.parseInt(val);
	}
	*/
	
	private Spectrum getSpectrumFromString(int size, String scanString)
	{
		
		Spectrum s = new Spectrum(size);
		
		
		int startIndex = 0;
		int endIndex;
		int count = 0;
		
		while (true)
		{
			 endIndex = scanString.indexOf(" ", startIndex+1);
			 if (endIndex == -1) {
				 s.set(count, Float.parseFloat(scanString.substring(startIndex)));
				 break;
			 }
			 
			 s.set(count, Float.parseFloat(scanString.substring(startIndex, endIndex)));
			 
			 startIndex = endIndex;
			 count++;
		}
		
		
		return s;

	}
	
	private List<String> getEntriesForAttr(String attr)
	{
		return attrEntries.get(attr);
	}
	
	private List<?> getEntriesForVar(String var)
	{
		return variableEntries.get(var);
	}
	
	
	
	
	

	public boolean hasVar(String var)
	{	
		return variableEntries.containsKey(var);
	}
	protected List<String> getVars()
	{
		return new FList<String>(variableEntries.keySet());
	}
	
	public boolean hasAttr(String attr)
	{
		return attrEntries.containsKey(attr);
	}
	protected List<String> getAttrs()
	{
		return new FList<String>(attrEntries.keySet());
	}
	
	
	

	public String getAttr(String attr, int entry)
	{
		List<String> entries = getEntriesForAttr(attr);
		if (entries == null) return null;
		return entries.get(entry);
	}
	protected int getAttrCount(String attr)
	{
		return getEntriesForAttr(attr).size();
	}
	public Integer getAttrInt(String attr, int entry)
	{
		List<String> entries = getEntriesForAttr(attr);
		if (entries == null) return null;
		return Integer.parseInt(entries.get(entry));
	}
	public List<String> getAttrEntries(String attr)
	{
		return getEntriesForAttr(attr);
	}
	
	
	
	
	
	

	protected CDF_DATATYPE getVarType(String var)
	{
		return varTypes.get(var);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Float> getVarFloats(String var)
	{
				
		switch (getVarType(var)) {
		
			case REAL:
				return (List<Float>)getEntriesForVar(var);
				
			case INTEGER:
				return FList.<Integer>wrap(  (List<Integer>)getEntriesForVar(var)  ).map(new FnMap<Integer, Float>() {

					public Float f(Integer v)
					{
						return new Float(v);
					}});
				
			case SPECTRUM:
				
				return FList.<Spectrum>wrap(  (List<Spectrum>)getEntriesForVar(var)  ).map(new FnMap<Spectrum, Float>() {

					public Float f(Spectrum v)
					{
						return v.fold(Functions.addf());
					}});
				
		}
		
		return null;
			
		
	}
	@SuppressWarnings("unchecked")
	protected List<Integer> getVarInts(String var)
	{
		
		
		switch (getVarType(var)) {
		
			case REAL:
				return FList.<Float>wrap(  (List<Float>)getEntriesForVar(var)  ).map(new FnMap<Float, Integer>() {

					public Integer f(Float v)
					{
						return v.intValue();
					}});
				
			case INTEGER:
				
				return (List<Integer>)getEntriesForVar(var);
				
			case SPECTRUM:
				
				return FList.<Spectrum>wrap(  (List<Spectrum>)getEntriesForVar(var)  ).map(new FnMap<Spectrum, Integer>() {

					public Integer f(Spectrum v)
					{
						return v.fold(Functions.addf()).intValue();
					}});
				
		}
		
		return null;
		
		
	}
	@SuppressWarnings("unchecked")
	public List<Spectrum> getVarSpectra(String var)
	{
		switch (getVarType(var)) {
			
			case REAL:
				return FList.<Float>wrap((List<Float>)getEntriesForVar(var)).map(new FnMap<Float, Spectrum>() {

					public Spectrum f(Float v)
					{
						return new Spectrum(1, v.floatValue());
					}});
				
			case INTEGER:

				return FList.<Integer>wrap((List<Integer>)getEntriesForVar(var)).map(new FnMap<Integer, Spectrum>() {

					public Spectrum f(Integer v)
					{
						return new Spectrum(1, v.floatValue());
					}});
				
			case SPECTRUM:
				return (List<Spectrum>)getEntriesForVar(var);
				
		}
		
		return null;
	}


	
	
	
	public boolean hasVarAttr(String var, String attrName)
	{
		return getVarAttr(var, attrName) != null;
	}
	protected String getVarAttr(String var, String attrName)
	{
		return variableAttributes.get(var).get(attrName);
	}
	public Integer getVarAttrInt(String var, String attrName)
	{
		return Integer.parseInt(getVarAttr(var, attrName));
	}
	
	
	protected int varRecordCount(String var)
	{
		return Integer.parseInt(getVarAttr(var, CDFMLStrings.XML_ATTR_NUMRECORDS));
	}
	
	protected abstract void processedSpectrum(String varname, int entryNo, Spectrum spectrum);
	


}
