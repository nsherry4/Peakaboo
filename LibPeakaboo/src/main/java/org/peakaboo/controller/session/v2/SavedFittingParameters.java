package org.peakaboo.controller.session.v2;

public class SavedFittingParameters {
	
	public float min;
	public float max;
	public String detectorMaterial;
	public float fwhmbase;
	public boolean escapes;
	
	public SavedFittingParameters() {};
	
	public SavedFittingParameters(
			float min,
			float max,
			String detectorMaterial,
			float fwhmbase,
			boolean escapes
	) {
		this.min = min;
		this.max = max;
		this.detectorMaterial = detectorMaterial;
		this.fwhmbase = fwhmbase;
		this.escapes = escapes;
	}

}