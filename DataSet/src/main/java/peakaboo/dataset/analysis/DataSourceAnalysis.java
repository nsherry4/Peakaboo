package peakaboo.dataset.analysis;

import peakaboo.dataset.DataSet;
import peakaboo.datasource.model.DataSource;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

public class DataSourceAnalysis implements Analysis {

	private DataSource dataSource;
	
	
	protected int					channelCount;
	protected Spectrum				summedSpectrum;
	protected int					summedScanCount;
	protected Spectrum				maximumSpectrum;
	protected float					maxValue;
	
	
	public DataSourceAnalysis(DataSet dataSet, DataSource dataSource) {
		this.dataSource = dataSource;
		
		int nonNullScanIndex = firstNonNullScanIndex(0);
		if (nonNullScanIndex == -1) {
			throw new RuntimeException("Cannot find non-null scan");
		}
		ReadOnlySpectrum nonNullScan = dataSource.getScanData().get(nonNullScanIndex);
		channelCount = nonNullScan.size();

		
		summedSpectrum = new ISpectrum(channelCount);
		summedScanCount = 0;
		maximumSpectrum = new ISpectrum(channelCount);
		maxValue = 0;
		
	}
	
	
	@Override
	public void process(int index, ReadOnlySpectrum spectrum) {
		if (spectrum == null) return;
		SpectrumCalculations.addLists_inplace(summedSpectrum, spectrum);
		summedScanCount++;
		SpectrumCalculations.maxLists_inplace(maximumSpectrum, spectrum);
		maxValue = Math.max(maxValue, spectrum.max());
	}
	
	
	@Override
	public int firstNonNullScanIndex()
	{
		return firstNonNullScanIndex(0);
	}
	
	@Override
	public int firstNonNullScanIndex(int start)
	{
		for (int i = start; i < dataSource.getScanData().scanCount(); i++)
		{
			if (dataSource.getScanData().get(i) != null)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	@Override
	public int lastNonNullScanIndex()
	{
		return lastNonNullScanIndex(dataSource.getScanData().scanCount()-1);
	}
	
	@Override
	public int lastNonNullScanIndex(int upto)
	{
		upto = Math.min(upto, dataSource.getScanData().scanCount()-1);
		
		for (int i = upto; i >= 0; i--)
		{
			if (dataSource.getScanData().get(i) != null)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	@Override
	public Spectrum maximumPlot()
	{
		return new ISpectrum(maximumSpectrum);
	}


	@Override
	public float maximumIntensity()
	{
		if (dataSource.getScanData().scanCount() == 0) return 0;
				
		return maxValue;
	}

	

	@Override
	public Spectrum averagePlot()
	{
		return SpectrumCalculations.divideBy(summedSpectrum, summedScanCount);
	}


	@Override
	public int channelsPerScan() {
		return channelCount;
	}

	
}
