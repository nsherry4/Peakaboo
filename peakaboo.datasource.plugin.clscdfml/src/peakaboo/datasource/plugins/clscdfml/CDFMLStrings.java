package peakaboo.datasource.plugins.clscdfml;



public class CDFMLStrings
{

	//ScienceStudio Data Categories
	public static final String CAT_SS_1					= "SS:1.0";
	public static final String CAT_SSMODEL_1			= "SSModel:1.0";
	public static final String CAT_EPICS_1				= "Epics:1.0";
	public static final String CAT_SCAN_1				= "Scan:1.0";
	public static final String CAT_BeamI_1				= "BeamI:1.0";
	public static final String CAT_BeamI_1_1			= "BeamI:1.1";
	public static final String CAT_MapXY_1				= "MapXY:1.0";
	public static final String CAT_XRF_1				= "XRF:1.0";
	public static final String CAT_MCA_1				= "MCA:1.0";
	
	
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
	public static final String	VAR_MCA_SUMSPECTRUM		= "MCA:SumSpectrum";
	
	
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

	
}
