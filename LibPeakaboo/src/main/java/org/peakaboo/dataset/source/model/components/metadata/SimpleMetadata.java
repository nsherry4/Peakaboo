package org.peakaboo.dataset.source.model.components.metadata;

public class SimpleMetadata implements Metadata {

	public String creationTime;
	public String creator;
	public String projectName;
	public String sessionName;
	public String facilityName;
	public String laboratoryName;
	public String experimentName;
	public String instrumentName;
	public String techniqueName;
	public String sampleName;
	public String scanName;
	public String startTime;
	public String endTime;
	
	
	@Override
	public String getCreationTime() {
		return creationTime;
	}

	@Override
	public String getCreator() {
		return creator;
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public String getSessionName() {
		return sessionName;
	}

	@Override
	public String getFacilityName() {
		return facilityName;
	}

	@Override
	public String getLaboratoryName() {
		return laboratoryName;
	}

	@Override
	public String getExperimentName() {
		return experimentName;
	}

	@Override
	public String getInstrumentName() {
		return instrumentName;
	}

	@Override
	public String getTechniqueName() {
		return techniqueName;
	}

	@Override
	public String getSampleName() {
		return sampleName;
	}

	@Override
	public String getScanName() {
		return scanName;
	}

	@Override
	public String getStartTime() {
		return startTime;
	}

	@Override
	public String getEndTime() {
		return endTime;
	}

}
