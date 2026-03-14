package org.peakaboo.dataset.source.model.components.scandata.analysis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class DataSourceAnalysisTest {

    @Test
    public void testMergeEmptyList() {
        DataSourceAnalysis result = DataSourceAnalysis.merge(new ArrayList<>());
        Assert.assertEquals(-1, result.channelsPerScan());
        Assert.assertEquals(0, result.scanCount());
    }

    @Test
    public void testMergeAllZeroScanCount() {
        // Analyses that never processed any data
        DataSourceAnalysis a = new DataSourceAnalysis();
        DataSourceAnalysis b = new DataSourceAnalysis();
        DataSourceAnalysis result = DataSourceAnalysis.merge(List.of(a, b));
        Assert.assertEquals(-1, result.channelsPerScan());
        Assert.assertEquals(0, result.scanCount());
    }

    @Test
    public void testMergeTwoAnalyses() {
        DataSourceAnalysis a = new DataSourceAnalysis();
        a.process(new ArraySpectrum(new float[]{1.0f, 2.0f, 3.0f}, false));
        a.process(new ArraySpectrum(new float[]{4.0f, 5.0f, 6.0f}, false));

        DataSourceAnalysis b = new DataSourceAnalysis();
        b.process(new ArraySpectrum(new float[]{10.0f, 20.0f, 30.0f}, false));

        DataSourceAnalysis result = DataSourceAnalysis.merge(List.of(a, b));

        Assert.assertEquals(3, result.channelsPerScan());
        Assert.assertEquals(3, result.scanCount());

        // Summed: (1+4+10, 2+5+20, 3+6+30) = (15, 27, 39)
        Spectrum summed = new ArraySpectrum(result.summedPlot());
        Assert.assertEquals(15.0f, summed.get(0), 1e-5f);
        Assert.assertEquals(27.0f, summed.get(1), 1e-5f);
        Assert.assertEquals(39.0f, summed.get(2), 1e-5f);

        // Maximum: max(4,10), max(5,20), max(6,30) = (10, 20, 30)
        Spectrum max = new ArraySpectrum(result.maximumPlot());
        Assert.assertEquals(10.0f, max.get(0), 1e-5f);
        Assert.assertEquals(20.0f, max.get(1), 1e-5f);
        Assert.assertEquals(30.0f, max.get(2), 1e-5f);

        // Max intensity: max(6, 30) = 30
        Assert.assertEquals(30.0f, result.maximumIntensity(), 1e-5f);
    }

    @Test
    public void testSingleAnalysisProcess() {
        DataSourceAnalysis a = new DataSourceAnalysis();
        a.process(new ArraySpectrum(new float[]{2.0f, 4.0f}, false));
        a.process(new ArraySpectrum(new float[]{6.0f, 8.0f}, false));

        Assert.assertEquals(2, a.channelsPerScan());
        Assert.assertEquals(2, a.scanCount());

        // Average: (8/2, 12/2) = (4, 6)
        Spectrum avg = new ArraySpectrum(a.averagePlot());
        Assert.assertEquals(4.0f, avg.get(0), 1e-5f);
        Assert.assertEquals(6.0f, avg.get(1), 1e-5f);
    }

    @Test
    public void testNullSpectrumIgnored() {
        DataSourceAnalysis a = new DataSourceAnalysis();
        a.process(null);
        Assert.assertEquals(-1, a.channelsPerScan());
        Assert.assertEquals(0, a.scanCount());
    }

}
