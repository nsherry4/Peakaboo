package peakaboo.fileio;



import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import commonenvironment.AbstractFile;

import fava.Fn;
import fava.Functions;
import fava.datatypes.Bounds;
import fava.datatypes.Pair;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionMap;


import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.TempFileList;
import peakaboo.fileio.support.CDFML;
import scitypes.Coord;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



public class CDFMLSaxDataSource extends DefaultHandler2 implements DataSource, DataSourceDimensions,
		DataSourceExtendedInformation
{

	XMLReader									xr;

	//store each tag and its attributes on the stack
	Stack<Pair<String, Map<String, String>>>	tagStack;
	//attrName -> (entryNo -> contents)
	Map<String, Map<Integer, String>>			attrEntries;
	//scanIndex -> (x, y, i, j, iNaught)
	Map<Integer, ScanValues>					scanValues;
	//scanIndex -> Spectrum
	TempFileList<Spectrum>						scandata;

	int numScans;
	

	boolean										isEntry, isRecord;
	int											entryNo;

	StringBuilder								sb;


	Spectrum									normaliseSpectrum;


	FunctionMap<Boolean, Boolean>				isAborted;
	FunctionEach<Integer>						getScanCountCallback;
	FunctionEach<Integer>						readScanCallback;
	int											scanReadCount;
	

	String										attrPath		= CDFML.CDF_ROOT_NAME + "/" + CDFML.ATTRS_TAG + "/"
																		+ CDFML.ATTR_TAG;
	String										attrEntryPath	= attrPath + "/" + CDFML.ATTR_ENTRY_TAG;

	String										varPath			= CDFML.CDF_ROOT_NAME + "/" + CDFML.VARS_TAG + "/"
																		+ CDFML.VAR_TAG;
	String										varDataPath		= varPath + "/" + CDFML.VAR_DATA_TAG;
	String										varRecordPath	= varDataPath + "/" + CDFML.VAR_DATA_RECORD_TAG;

	String										varSpectrumInfo = varPath + "/" + CDFML.VAR_INFO_TAG;
	
	

	public CDFMLSaxDataSource(AbstractFile file, FunctionEach<Integer> getScanCountCallback, FunctionEach<Integer> readScanCallback, FunctionMap<Boolean, Boolean> isAborted) throws Exception
	{
		super();
		
		this.getScanCountCallback = getScanCountCallback;
		this.readScanCallback = readScanCallback;
		this.isAborted = isAborted;
		
		attrEntries = DataTypeFactory.<String, Map<Integer, String>> map();
		tagStack = new Stack<Pair<String, Map<String, String>>>();
		scanValues = DataTypeFactory.<Integer, ScanValues> map();

		try
		{

			scandata = new TempFileList<Spectrum>(0, "Peakaboo", Spectrum.getEncoder(), Spectrum.getDecoder());


			xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);

			xr.parse(new InputSource(file.getInputStream()));

		}
		catch (SAXException e)
		{
			throw new Exception();
		}
	}

	@Override
	public void startDocument()
	{
		scanReadCount = 0;
	}

	@Override
	public void endDocument()
	{
		if (readScanCallback != null) readScanCallback.f(scanReadCount);
		calcNormalisationData();
	}


	private String currentPath()
	{	
		
		return (Fn.foldl(Fn.map(tagStack, new FunctionMap<Pair<String, Map<String, String>>, String>() {

			
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
			entryNo = Integer.parseInt(atts.getValue(CDFML.ATTR_ENTRY_TAG_NUM));
		}
		else if (tag.equals(varRecordPath))
		{
			//this is a variable record
			isRecord = true;
			entryNo = Integer.parseInt(atts.getValue(CDFML.VAR_DATA_RECORD_NAME_ATTR));
		} else if (tag.equals(varSpectrumInfo)){
			
			//this is a cdfVarInfo tag
			
			if (CDFML.SPECTRUMS.equals(  tagStack.get(tagStack.size() - 2).second.get(CDFML.ATTR_NAME_ATTR)  ))
			{
				//this is the cdfVarInfo tag for the XRF:Spectrum variable
				numScans = Integer.parseInt(atts.getValue(CDFML.ATTR_NUMRECORDS_ATTR));
				if (getScanCountCallback != null) getScanCountCallback.f(numScans);
			}
			
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

			//get the attribute tag data.
			//we store a map of attr-tag-name -> map<integer, string> to keep track of entrynum/value for each attribute 
			Map<String, String> attrTagAttributes = tagStack.get(tagStack.size() - 2).second;
			String attrTagName = attrTagAttributes.get(CDFML.VAR_TAG_ATTR_NAME);

			//loop up the map of entries for this attr name, create it if it doesnt exist yet
			Map<Integer, String> entryList = attrEntries.get(attrTagName);
			if (entryList == null)
			{
				entryList = DataTypeFactory.<Integer, String> map();
				attrEntries.put(attrTagName, entryList);
			}

			entryList.put(entryNo, sb.toString());

		}
		else if (isRecord)
		{
			//get the name of the variable that this record belongs to
			String variableName = tagStack.get(tagStack.size() - 3).second.get(CDFML.VAR_TAG_ATTR_NAME);

			ScanValues sv = scanValues.get(entryNo);
			if (sv == null)
			{
				sv = new ScanValues();
				scanValues.put(entryNo, sv);
			}

			if (CDFML.X_POSITONS.equals(variableName)) sv.xpos = Float.parseFloat(sb.toString());
			if (CDFML.Y_POSITONS.equals(variableName)) sv.ypos = Float.parseFloat(sb.toString());
			if (CDFML.X_INDEX.equals(variableName)) sv.xind = Integer.parseInt(sb.toString());
			if (CDFML.Y_INDEX.equals(variableName)) sv.yind = Integer.parseInt(sb.toString());
			if (CDFML.NORMALISE.equals(variableName)) sv.iNaught = Float.parseFloat(sb.toString());

			if (CDFML.SPECTRUMS.equals(variableName))
			{
				scandata.set(entryNo, getSpectrumFromString(sb.toString()));
				scanReadCount++;
				if (scanReadCount == 100) 
				{
					if (isAborted != null && isAborted.f(true))
					{
						throw new SAXException("Aborted by User");
					}
					if (readScanCallback != null) readScanCallback.f(scanReadCount);
					scanReadCount = 0;
				}
			}

		}

		tagStack.pop();

		isRecord = false;
		isEntry = false;

		/*
		if ("".equals(uri)) System.out.println("End element: " + qName);
		else System.out.println("End element:   {" + uri + "}" + name);
		*/
	}


	private Map<Integer, String> getEntriesForAttr(String attr)
	{
		return attrEntries.get(attr);
	}


	private String getEntryForAttr(String attr, int entry)
	{
		Map<Integer, String> entries = getEntriesForAttr(attr);
		if (entries == null) return null;
		return entries.get(entry);
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
		Spectrum s = scandata.get(index);

		float iNaught = getNormalisationData().get(index);

		if (iNaught != 0) SpectrumCalculations.divideBy_inplace(s, iNaught);
		else SpectrumCalculations.multiplyBy(s, 0);

		return s;

	}

	
	public void markScanAsBad(int index)
	{
		// TODO Auto-generated method stub

	}

	public int getScanCount()
	{
		return numScans;
	}
	
	public int getExpectedScanCount()
	{
		Coord<Integer> dims = getDataDimensions();
		return dims.x * dims.y;
	}
	
	
	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{
		Coord<Number> dims = new Coord<Number>(0, 0);

		dims.x = scanValues.get(index).xpos;
		dims.y = scanValues.get(index).ypos;
		return dims;

	}



	////////////////////////////////////////////////////////////
	// ATTRIBUTE DATA
	////////////////////////////////////////////////////////////



	public String getDatasetName()
	{
		String Project = getEntryForAttr(CDFML.ATTR_PROJECT_NAME, 0);
		String DatasetName = getEntryForAttr(CDFML.ATTR_DATASET_NAME, 0);
		String SampleName = getEntryForAttr(CDFML.ATTR_SAMPLE_NAME, 0);

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
		String maxEnergyValue = getEntryForAttr(CDFML.ATTR_MAX_ENERGY, 0);
		if (maxEnergyValue == null) return 20.48f;
		return Float.parseFloat(maxEnergyValue) / 1000.0f;
	}


	
	public List<String> getScanNames()
	{
		List<String> scannames = DataTypeFactory.<String> list();

		for (int i = 0; i < getScanCount(); i++)
		{
			scannames.add("Scan #" + (i + 1));
		}

		return scannames;
	}


	private int getDataWidth()
	{
		int width = Integer.parseInt(getEntryForAttr(CDFML.ATTR_DATAX, 0));
		return width;
	}


	private int getDataHeight()
	{
		int height = Integer.parseInt(getEntryForAttr(CDFML.ATTR_DATAY, 0));
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

		x1 = Float.parseFloat(getEntryForAttr(CDFML.ATTR_DIM_X_START, 0));
		x2 = Float.parseFloat(getEntryForAttr(CDFML.ATTR_DIM_X_END, 0));
		y1 = Float.parseFloat(getEntryForAttr(CDFML.ATTR_DIM_Y_START, 0));
		y2 = Float.parseFloat(getEntryForAttr(CDFML.ATTR_DIM_Y_END, 0));


		Bounds<Number> xDim = new Bounds<Number>(x1, x2);
		Bounds<Number> yDim = new Bounds<Number>(y1, y2);
		return new Coord<Bounds<Number>>(xDim, yDim);
	}


	
	public String getRealDimensionsUnit()
	{
		return getEntryForAttr(CDFML.ATTR_DIM_X_START, 1);
	}



	
	public String getCreationTime()
	{
		return getEntryForAttr(CDFML.ATTR_CREATION_TIME, 0);
	}


	public String getCreator()
	{
		return getEntryForAttr(CDFML.ATTR_CREATOR, 0);
	}


	public String getEndTime()
	{
		return getEntryForAttr(CDFML.ATTR_END_TIME, 0);
	}



	public String getExperimentName()
	{
		return getEntryForAttr(CDFML.ATTR_EXPERIMENT_NAME, 0);
	}


	public String getFacilityName()
	{
		return getEntryForAttr(CDFML.ATTR_FACILITY, 0);
	}


	public String getInstrumentName()
	{
		return getEntryForAttr(CDFML.ATTR_INSTRUMENT, 0);
	}


	public String getLaboratoryName()
	{
		return getEntryForAttr(CDFML.ATTR_LABORATORY, 0);
	}


	public String getProjectName()
	{
		return getEntryForAttr(CDFML.ATTR_PROJECT_NAME, 0);
	}


	public String getSampleName()
	{
		return getEntryForAttr(CDFML.ATTR_SAMPLE_NAME, 0);
	}


	public String getScanName()
	{
		return getEntryForAttr(CDFML.ATTR_DATASET_NAME, 0);
	}


	public String getSessionName()
	{
		return getEntryForAttr(CDFML.ATTR_SESSION_NAME, 0);
	}


	public String getStartTime()
	{
		return getEntryForAttr(CDFML.ATTR_START_TIME, 0);
	}


	public String getTechniqueName()
	{
		return getEntryForAttr(CDFML.ATTR_TECHNIQUE, 0);
	}


	
	public boolean hasExtendedInformation()
	{
		return true;
	}


	private void calcNormalisationData()
	{
		normaliseSpectrum = new Spectrum(getScanCount());

		for (int i = 0; i < getScanCount(); i++)
		{
			normaliseSpectrum.set(i, scanValues.get(i).iNaught);
		}

		SpectrumCalculations.normalize_inplace(normaliseSpectrum);

	}


	public Spectrum getNormalisationData()
	{
		return normaliseSpectrum;
	}

}



class ScanValues
{

	public float	xpos, ypos;
	public int		xind, yind;
	public float	iNaught;

}
