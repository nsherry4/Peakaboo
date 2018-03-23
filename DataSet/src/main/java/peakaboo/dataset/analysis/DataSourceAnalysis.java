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


	@Override
	public Spectrum averagePlot(final List<Integer> excludedIndcies)
	{

		if (excludedIndcies.size() == 0) return averagePlot();

		
		//Filter for *JUST* the scans which have been marked as bad
		List<ReadOnlySpectrum> badScans = excludedIndcies.stream().map(index -> dataSource.getScanData().get(index)).collect(toList());

		Spectrum Ae;
		Spectrum At;
		int Nt, Ne;

		// At - Total average for whole dataset
		// Ae - average for excluded data set (ie just the bad ones)
		// Nt = Total number of scans in whole dataset
		// Ne - number of bad scans
		//
		// In order to figure out what the new average should be once we exclude the bad scans
		// we could recalculate from the good scans, but that would take a long time. We will operate
		// under the assumption that there will be many more good scans than bad scans.
		// so calculating the average of the bad scans should be relatively fast.
		// (At - Ae*(Ne/Nt)) * (Nt/(Nt-Ne))

		Ae = SpectrumCalculations.getDatasetAverage(badScans);
		At = averagedSpectrum;
		Nt = dataSource.getScanData().scanCount();
		Ne = badScans.size();

		// if all scans are marked as bad, lets just return a list of 0s of the same length as the average scan
		if (Nt == Ne)
		{
			return new ISpectrum(new ISpectrum(averagedSpectrum.size(), 0.0f));
		}

		float Net = (float) Ne / (float) Nt;
		float Ntte = Nt / ((float) Nt - (float) Ne);

		Spectrum goodAverage = new ISpectrum(averagedSpectrum.size());
		for (int i = 0; i < averagedSpectrum.size(); i++)
		{
			goodAverage.set(i, (At.get(i) - Ae.get(i) * Net) * Ntte);
		}

		return new ISpectrum(goodAverage);

	}





	
	
}
