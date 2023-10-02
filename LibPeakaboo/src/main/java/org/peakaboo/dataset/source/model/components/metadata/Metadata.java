package org.peakaboo.dataset.source.model.components.metadata;


public interface Metadata {
	
	String getCreationTime();
	String getCreator();

	String getProjectName();
	String getSessionName();
	String getFacilityName();
	String getLaboratoryName();
	String getExperimentName();
	String getInstrumentName();
	String getTechniqueName();
	String getSampleName();
	String getScanName();
	
	String getStartTime();
	String getEndTime();
	
}
