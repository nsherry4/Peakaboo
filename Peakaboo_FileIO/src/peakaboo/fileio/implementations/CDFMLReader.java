package peakaboo.fileio.implementations;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.UIDefaults;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import peakaboo.common.DataTypeFactory;
import peakaboo.fileio.implementations.support.CDFML;
import scitypes.Spectrum;
import scitypes.filebacked.FileBackedList;

import commonenvironment.AbstractFile;

import fava.Fn;
import fava.Functions;
import fava.datatypes.Pair;
import fava.datatypes.Range;
import fava.signatures.FnEach;
import fava.signatures.FnGet;
import fava.signatures.FnMap;


public abstract class CDFMLReader extends DefaultHandler2
{


	private static enum CDF_DATATYPE{
		REAL, INTEGER, SPECTRUM
	}
	
	
	private static final String ABORT_MESSAGE = "Aborted by User"; 
	
	private XMLReader									xr;

	//store each tag and its attributes on the stack
	private Stack<Pair<String, Map<String, String>>>	tagStack;
	
	//attrName -> (entryNo -> contents)
	private Map<String, Map<Integer, String>>			attrEntries;
	//varName -> [record]
	private Map<String, List<?>>						variableEntries;
	//varName -> (attribute -> value)
	private Map<String, Map<String, String>>			variableAttributes;

	
	private FnGet<Boolean> 								isAborted;
	
	private boolean										isEntry, isRecord;
	private int											entryNo;

	private StringBuilder								sb;
	

	private String										attrPath		= CDFML.CDF_ROOT_NAME + "/" + CDFML.TAG_ATTRS + "/"
																		+ CDFML.TAG_ATTR;
	private String										attrEntryPath	= attrPath + "/" + CDFML.TAG_ENTRY;

	private String										varPath			= CDFML.CDF_ROOT_NAME + "/" + CDFML.TAG_VARS + "/"
																		+ CDFML.TAG_VAR;
	private String										varDataPath		= varPath + "/" + CDFML.TAG_VAR_DATA;
	private String										varRecordPath	= varDataPath + "/" + CDFML.TAG_VAR_RECORD;

	private String										varSpectrumInfo = varPath + "/" + CDFML.TAG_VAR_INFO;
		

	public CDFMLReader()
	{
		super();
	}
	
	public void read(AbstractFile file, FnGet<Boolean> isAborted) throws Exception
	{
		
		this.isAborted = isAborted;
		
		attrEntries = DataTypeFactory.<String, Map<Integer, String>> map();
		variableEntries = DataTypeFactory.<String, List<?>>map();
		variableAttributes = DataTypeFactory.<String, Map<String, String>>map();
		tagStack = new Stack<Pair<String, Map<String, String>>>();
		
		try
		{

			xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);

			xr.parse(new InputSource(file.getInputStream()));

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

		if (tag.equals(attrEntryPath))
		{
			//this is an attribute entry
			isEntry = true;
			entryNo = Integer.parseInt(atts.getValue(CDFML.XML_ATTR_ENTRYNUM));
		}
		else if (tag.equals(varRecordPath))
		{
			//this is a variable record
			isRecord = true;
			entryNo = Integer.parseInt(atts.getValue(CDFML.XML_ATTR_RECORD_NUMBER));
		} else if (tag.equals(varSpectrumInfo)){
			
			//this is a cdfVarInfo tag
			
			String varName = getTagAttribute(1, CDFML.XML_ATTR_NAME);
			
			Map<String, String> attsMap = DataTypeFactory.<String, String>map();
			for (Integer i : new Range(0, atts.getLength()-1))
			{
				attsMap.put(atts.getQName(i), atts.getValue(i));
			}
			variableAttributes.put(varName, attsMap);
			
		}

		//create a new string builder for the data we are about to receive
		sb = new StringBuilder();

	}

	@Override
	public void characters(char ch[], int start, int length)
	{

		if (isRecord || isEntry)
		{
			for (int i = start; i < start + length; i++)
			{
				sb.append(ch[i]);
			}
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
			
			try
			{
				recordRecord();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
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
		
		return (Fn.foldl(Fn.map(tagStack, new FnMap<Pair<String, Map<String, String>>, String>() {

			
			public String f(Pair<String, Map<String, String>> element)
			{
				return element.first;
			}
		}), Functions.strcat("/"))).replace('\n', '\000');
		
	}


	private void addTagToPath(String name, Attributes atts)
	{
		Map<String, String> attMap = DataTypeFactory.<String, String> map();
		for (int i = 0; i < atts.getLength(); i++)
		{
			attMap.put(atts.getLocalName(i), atts.getValue(i));
		}

		tagStack.push(new Pair<String, Map<String, String>>(name, attMap));
	}
	
	
	
	
	private void recordEntry()
	{
		//get the attribute tag data.
		//we store a map of attr-tag-name -> map<integer, string> to keep track of entrynum/value for each attribute 
		Map<String, String> attrTagAttributes = tagStack.get(tagStack.size() - 2).second;
		String attrTagName = attrTagAttributes.get(CDFML.XML_ATTR_NAME);

		//loop up the map of entries for this attr name, create it if it doesnt exist yet
		Map<Integer, String> entryList = attrEntries.get(attrTagName);
		if (entryList == null)
		{
			entryList = DataTypeFactory.<Integer, String> map();
			attrEntries.put(attrTagName, entryList);
		}

		entryList.put(entryNo, sb.toString());
	}

	private void recordRecord() throws IOException
	{

		//get the name of the variable that this record belongs to
		String variableName = getTagAttribute(2, CDFML.XML_ATTR_NAME);
		
		int dimSize = 1;
		if (hasVarAttr(variableName, "dimSizes")) {
			dimSize = getVarAttrInt(variableName, "dimSizes");
		}
		
		boolean isRealNumber = false;
		if (hasVarAttr(variableName, "cdfDatatype")) isRealNumber = "CDF_DOUBLE".equals(getVarAttr(variableName, "cdfDatatype"));
		

		CDF_DATATYPE datatype = CDF_DATATYPE.REAL;
		if (isRealNumber && dimSize == 1) datatype = CDF_DATATYPE.REAL;
		if (!isRealNumber && dimSize == 1) datatype = CDF_DATATYPE.INTEGER;
		if (dimSize > 1) datatype = CDF_DATATYPE.SPECTRUM;		
				
		
		if (variableName != null) {
			
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
					if (svalues == null) variableEntries.put(variableName, new FileBackedList<Spectrum>(variableName));
					svalues = (List<Spectrum>)variableEntries.get(variableName);
					svalues.set(entryNo, getSpectrumFromString(sb.toString()));
					
					processedSpectrum(variableName);
					
					break;
				
			}
			
		}
		
	}
	
	
	
	private String getTagAttribute(int levelsUp, String attrName)
	{
		return tagStack.get(tagStack.size() - 1 - levelsUp).second.get(attrName);
	}
	
	private Integer getTagAttributeInt(int levelsUp, String attrName)
	{
		String val = getTagAttribute(levelsUp, attrName);
		if (val == null) return null;
		return Integer.parseInt(val);
	}
		
	private Spectrum getSpectrumFromString(String scanString)
	{
		String[] scanPoints = scanString.split(" ");
		Spectrum results = new Spectrum(scanPoints.length);
		for (int i = 0; i < scanPoints.length; i++)
		{
			results.set(i, Float.parseFloat(scanPoints[i]));
		}
		return results;
	}
	
	private Map<Integer, String> getEntriesForAttr(String attr)
	{
		return attrEntries.get(attr);
	}
	
	private List<?> getEntriesForVar(String var)
	{
		return variableEntries.get(var);
	}
	
	
	
	
	

	protected boolean hasVar(String var)
	{		
		return variableEntries.containsKey(var);
	}
	
	protected boolean hasAttr(String attr)
	{
		return attrEntries.containsKey(attr);
	}
	
	
	
	

	protected String getAttr(String attr, int entry)
	{
		Map<Integer, String> entries = getEntriesForAttr(attr);
		if (entries == null) return null;
		return entries.get(entry);
	}
	protected Integer getAttrInt(String attr, int entry)
	{
		Map<Integer, String> entries = getEntriesForAttr(attr);
		if (entries == null) return null;
		return Integer.parseInt(entries.get(entry));
	}
	
	
	
	
	
	

	protected List<Float> getVarFloats(String var)
	{
		return (List<Float>)getEntriesForVar(var);
	}
	protected List<Integer> getVarInts(String var)
	{
		return (List<Integer>)getEntriesForVar(var);
	}
	protected List<Spectrum> getVarSpectra(String var)
	{
		return (List<Spectrum>)getEntriesForVar(var);
	}


	
	
	
	protected boolean hasVarAttr(String var, String attrName)
	{
		return getVarAttr(var, attrName) != null;
	}
	protected String getVarAttr(String var, String attrName)
	{
		return variableAttributes.get(var).get(attrName);
	}
	protected Integer getVarAttrInt(String var, String attrName)
	{
		return Integer.parseInt(getVarAttr(var, attrName));
	}
	
	
	protected int varRecordCount(String var)
	{
		return Integer.parseInt(getVarAttr(var, CDFML.XML_ATTR_NUMRECORDS));
	}
	
	protected abstract void processedSpectrum(String varname);
	


}
