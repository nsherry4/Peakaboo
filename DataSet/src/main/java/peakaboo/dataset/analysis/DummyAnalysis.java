package peakaboo.dataset.analysis;

import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

public class DummyAnalysis implements Analysis {

	@Override
	public Spectrum averagePlot()
	{
		// TODO Auto-generated method stub
		return null;
	}
	


	@Override
	public float maximumIntensity()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Spectrum maximumPlot()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public int firstNonNullScanIndex()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int firstNonNullScanIndex(int start)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastNonNullScanIndex(int upto)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastNonNullScanIndex()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void process(int index, ReadOnlySpectrum t) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int channelsPerScan() {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
