package org.peakaboo.curvefit.peak.detector;

import org.peakaboo.common.SelfDescribing;

/**
 * Describes the kind of properties that would be expected from different kinds of detectors.
 * @author Nathaniel Sherry, 2010-2019
 *
 */


public enum DetectorMaterialType
{
	SILICON,
	GERMANIUM,
	
	;
		

	private DetectorMaterial silicon = new SiliconDetectorMaterial();
	private DetectorMaterial germanium = new GermaniumDetectorMaterial();
	public DetectorMaterial get() {
		switch (this) {
		case SILICON: return silicon;
		case GERMANIUM: return germanium;
		default: return silicon;
		}
	}
		
	public static DetectorMaterialType getDefault() {
		return SILICON;
	}

	
}
