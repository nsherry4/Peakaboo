package peakaboo.curvefit.peak.search;

import java.util.List;

import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

public class TopHatPeakSearcher implements PeakSearcher {

	float[] coefs;
	int halfW, fullV;
	
	public TopHatPeakSearcher() {
		halfW = 5;
		fullV = 10;
		coefs = new float[1+halfW+fullV];
		float wValue = 1f / (halfW*2f+1f);
		float vValue = -1f / (fullV*2f);
		
		coefs[0] = wValue;
		for (int i = 1; i <= halfW; i++) {
			coefs[i] = wValue;
		}
		for (int i = 1+halfW; i < 1+halfW+fullV; i++) {
			coefs[i] = vValue;
		}
	}
	
	@Override
	public List<Integer> search(ReadOnlySpectrum data) {
		
		Spectrum tophat = tophat(data);
		return new DerivativePeakSearcher().search(tophat);
		
	}

	public Spectrum tophat(ReadOnlySpectrum data) {
		Spectrum tophat = new ISpectrum(data.size());
		
		int range = halfW + fullV;
		for (int i = 0; i < data.size(); i++) {
			float value = 0f;
			for (int j = -range; j <= +range; j++) {
				int index = i+j;
				index = Math.max(index, 0);
				index = Math.min(index, data.size()-1);
				value += data.get(index) * coefs[Math.abs(j)];
			}
			tophat.set(i, value);
		}
		return tophat;
	}
	
}
