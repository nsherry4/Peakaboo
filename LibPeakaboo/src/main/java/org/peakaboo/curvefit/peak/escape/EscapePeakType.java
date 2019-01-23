package org.peakaboo.curvefit.peak.escape;

/**
 * Describes the kind of escape peaks that would be expected from different kinds of detectors.
 * @author Nathaniel Sherry, 2010
 *
 */


public enum EscapePeakType
{
	NONE,
	SILICON,
	GERMANIUM,
	
	;
		

	private EscapePeak none = new NoneEscapePeak();
	private EscapePeak silicon = new SiliconEscapePeak();
	private EscapePeak germanium = new GermaniumEscapePeak();
	public EscapePeak get() {
		switch (this) {
		case NONE: return none;
		case SILICON: return silicon;
		case GERMANIUM: return germanium;
		default: return silicon;
		}
	}
	
	
	public static EscapePeakType getDefault()
	{
		return SILICON;
	}
	
	
}
