package peakaboo.mapping.calibration;

import java.util.HashMap;
import java.util.Map;

import peakaboo.common.YamlSerializer;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

public class CalibrationProfile {

	private CalibrationReference reference;
	private Map<TransitionSeries, Float> calibrations;
	
	/**
	 * Create an empty CalibrationProfile
	 */
	public CalibrationProfile() {
		this.reference = CalibrationReferenceManager.empty();
		calibrations = new HashMap<>();
	}
	
	public CalibrationProfile(CalibrationReference reference, FittingResultSet sample) {
		this.reference = reference;
		calibrations = new HashMap<>();
		
		//Build profile
		for (FittingResult fit : sample) {
			TransitionSeries ts = fit.getTransitionSeries();
			if (! reference.contains(ts)) { continue; }
			//TODO: Is this the right way to measure sample intensity
			float sampleIntensity = fit.getFit().sum();
			float referenceValue = reference.getConcentration(ts);
			float calibration = (sampleIntensity / referenceValue) * 1000f;
			calibrations.put(ts, calibration);
		}
		
	}
	
	public Map<TransitionSeries, Float> getCalibrations() {
		return new HashMap<>(calibrations);
	}

	public boolean contains(TransitionSeries ts) {
		return calibrations.keySet().contains(ts);
	}
	
	public float getCalibration(TransitionSeries ts) {
		return calibrations.get(ts);
	}
	
	public CalibrationReference getReference() {
		return reference;
	}
	
	public float calibratedSum(FittingResult fittingResult) {
		TransitionSeries ts = fittingResult.getTransitionSeries();
		float rawfit = fittingResult.getFit().sum();
		return calibrate(rawfit, ts);
	}
	
	public float calibrate(float value, TransitionSeries ts) {
		if (calibrations.keySet().contains(ts)) {
			float calibration = calibrations.get(ts);
			return value * calibration;
		} else {
			return value;
		}
	}
	
	public ReadOnlySpectrum calibrateMap(ReadOnlySpectrum data, TransitionSeries ts) {
		if (!contains(ts)) {
			return data;
		}
		float calibration = getCalibration(ts);
		return SpectrumCalculations.multiplyBy(data, calibration);
	}
	
	public boolean isEmpty() {
		return calibrations.size() == 0;
	}
	
	
	public static String save(CalibrationProfile profile) {
		SerializedCalibrationProfile serialized = new SerializedCalibrationProfile();
		serialized.referenceUUID = profile.reference.getUuid();
		serialized.referenceName = profile.reference.getName();
		for (TransitionSeries ts : profile.calibrations.keySet()) {
			serialized.calibrations.put(ts.toIdentifierString(), profile.calibrations.get(ts));
		}
		return YamlSerializer.serialize(serialized);
	}
	
	
	public static CalibrationProfile load(String yaml) {
		CalibrationProfile profile = new CalibrationProfile();
		SerializedCalibrationProfile serialized = YamlSerializer.deserialize(yaml);
		for (String tsidentifier : serialized.calibrations.keySet()) {
			TransitionSeries ts = PeakTable.SYSTEM.get(tsidentifier);
			profile.calibrations.put(ts, serialized.calibrations.get(tsidentifier));
		}
		
		profile.reference = CalibrationReferenceManager.byUUID(serialized.referenceUUID);
		if (profile.reference == null) {
			throw new RuntimeException("Cannot find Calibration Reference '" + serialized.referenceName + "' (" + serialized.referenceUUID + ")");
		}
		
		return profile;
	}


	
	
	
}


class SerializedCalibrationProfile {
	public String referenceUUID = null;
	public String referenceName = null;
	public Map<String, Float> calibrations = new HashMap<>();
}
 