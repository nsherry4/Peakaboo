package peakaboo.dataset.analysis;

import static java.util.stream.Collectors.toList;

import java.util.List;

import peakaboo.dataset.DataSet;
import peakaboo.datasource.model.DataSource;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

public class DataSourceAnalysis implements Analysis {

	private DataSet dataSet;
	private DataSource dataSource;
	
	
	protected Spectrum				averagedSpectrum;
	protected int					averageScanCount;
	protected Spectrum				maximumSpectrum;
	protected float					maxValue;
	
	
	public DataSourceAnalysis(DataSet dataSet, DataSource dataSource) {
		this.dataSet = dataSet;
		this.dataSource = dataSource;
		
		int size = this.dataSet.channelsPerScan();
		averagedSpectrum = new ISpectrum(size);
		averageScanCount = 0;
		maximumSpectrum = new ISpectrum(size);
		maxValue = 0;
		
	}
	
	
	@Override
	public void process(int index, ReadOnlySpectrum spectrum) {
		if (spectrum == null) return;
		SpectrumCalculations.addLists_inplace(averagedSpectrum, spectrum);
		averageScanCount++;
		SpectrumCalculations.maxLists_inplace(maximumSpectrum, spectrum);
		maxValue = Math.max(maxValue, spectrum.max());
	}
	
	
	@Override
	public int firstNonNullScanIndex()
	{
		return DataSet.firstNonNullScanIndex(dataSource, 0);
	}
	
	@Override
	public int firstNonNullScanIndex(int start)
	{
		return DataSet.firstNonNullScanIndex(dataSource, start);
	}
	
	@Override
	public int lastNonNullScanIndex()
	{
		return DataSet.lastNonNullScanIndex(dataSource, dataSource.getScanData().scanCount()-1);
	}
	
	@Override
	public int lastNonNullScanIndex(int upto)
	{
		return DataSet.lastNonNullScanIndex(dataSource, upto);
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
		return SpectrumCalculations.divideBy(averagedSpectrum, averageScanCount);
	}

	
}
