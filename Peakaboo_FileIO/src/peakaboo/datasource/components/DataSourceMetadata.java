package peakaboo.datasource.components;


public interface DataSourceMetadata
{

	
	
	//SS Namespace
	String getCreationTime();
	String getCreator();

	//SSModel Namespace
	String getProjectName();
	String getSessionName();
	String getFacilityName();
	String getLaboratoryName();
	String getExperimentName();
	String getInstrumentName();
	String getTechniqueName();
	String getSampleName();
	String getScanName();
	
	//Scan Namespace
	String getStartTime();
	String getEndTime();
	
}
