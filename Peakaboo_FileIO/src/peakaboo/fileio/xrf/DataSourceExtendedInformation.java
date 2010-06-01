package peakaboo.fileio.xrf;


public interface DataSourceExtendedInformation
{

	public boolean hasExtendedInformation();
	
	//SS Namespace
	public String getCreationTime();
	public String getCreator();

	//SSModel Namespace
	public String getProjectName();
	public String getSessionName();
	public String getFacilityName();
	public String getLaboratoryName();
	public String getExperimentName();
	public String getInstrumentName();
	public String getTechniqueName();
	public String getSampleName();
	public String getScanName();
	
	//Scan Namespace
	public String getStartTime();
	public String getEndTime();
	
}
