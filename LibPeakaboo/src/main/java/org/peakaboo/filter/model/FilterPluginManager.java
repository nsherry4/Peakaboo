package org.peakaboo.filter.model;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.filter.plugins.JavaFilterPlugin;
import org.peakaboo.filter.plugins.advanced.DatasetNormalizationFilter;
import org.peakaboo.filter.plugins.advanced.IdentityFilter;
import org.peakaboo.filter.plugins.advanced.PeakDetectorFilter;
import org.peakaboo.filter.plugins.advanced.SpectrumNormalizationFilter;
import org.peakaboo.filter.plugins.advanced.SubFilter;
import org.peakaboo.filter.plugins.background.BruknerBackgroundFilter;
import org.peakaboo.filter.plugins.background.ExponentialComptonBackgroundFilter;
import org.peakaboo.filter.plugins.background.LinearTrimBackgroundFilter;
import org.peakaboo.filter.plugins.background.PolynomialBackgroundFilter;
import org.peakaboo.filter.plugins.background.SpectrumBackgroundFilter;
import org.peakaboo.filter.plugins.background.SquareSnipBackgroundFilter;
import org.peakaboo.filter.plugins.mathematical.AdditionMathFilter;
import org.peakaboo.filter.plugins.mathematical.DerivativeMathFilter;
import org.peakaboo.filter.plugins.mathematical.IntegralMathFilter;
import org.peakaboo.filter.plugins.mathematical.MultiplicationMathFilter;
import org.peakaboo.filter.plugins.mathematical.SubtractionMathFilter;
import org.peakaboo.filter.plugins.noise.FourierNoiseFilter;
import org.peakaboo.filter.plugins.noise.LowStatisticsNoiseFilter;
import org.peakaboo.filter.plugins.noise.SavitskyGolayNoiseFilter;
import org.peakaboo.filter.plugins.noise.SpringNoiseFilter;
import org.peakaboo.filter.plugins.noise.WaveletNoiseFilter;
import org.peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class FilterPluginManager extends BoltPluginManager<JavaFilterPlugin> {

	private static FilterPluginManager SYSTEM;
	public static void init(File filterDir) {
		try {
			if (SYSTEM == null) {
				SYSTEM = new FilterPluginManager(filterDir);
				SYSTEM.load();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load filter plugins", e);
		}
	}
	public static FilterPluginManager system() {
		return SYSTEM;
	}

	
	
	private BoltJavaBuiltinLoader<JavaFilterPlugin> builtins;
	
	public FilterPluginManager(File filterDir) {
		super("filter");
		
		addLoader(new BoltJarDirectoryLoader<>(this, JavaFilterPlugin.class, filterDir));
		addLoader(new BoltJarDirectoryLoader<>(this, JavaFilterPlugin.class));
		
		builtins = new BoltJavaBuiltinLoader<>(this, JavaFilterPlugin.class);
		registerCustomPlugins();
		addLoader(builtins);
	}
	
	private void registerCustomPlugins() {

		builtins.load(IdentityFilter.class);
		builtins.load(SubFilter.class);
		builtins.load(SpectrumNormalizationFilter.class);
		builtins.load(DatasetNormalizationFilter.class);
		
		builtins.load(BruknerBackgroundFilter.class);
		builtins.load(LinearTrimBackgroundFilter.class);
		builtins.load(PolynomialBackgroundFilter.class);
		builtins.load(SquareSnipBackgroundFilter.class);
		builtins.load(SpectrumBackgroundFilter.class);
		
		builtins.load(ExponentialComptonBackgroundFilter.class);
		
		builtins.load(AdditionMathFilter.class);
		builtins.load(DerivativeMathFilter.class);
		builtins.load(IntegralMathFilter.class);
		builtins.load(MultiplicationMathFilter.class);
		builtins.load(SubtractionMathFilter.class);
		
		builtins.load(FourierNoiseFilter.class);
		builtins.load(WeightedAverageNoiseFilter.class);
		builtins.load(SavitskyGolayNoiseFilter.class);
		builtins.load(SpringNoiseFilter.class);
		builtins.load(WaveletNoiseFilter.class);
		builtins.load(LowStatisticsNoiseFilter.class);
		
		builtins.load(PeakDetectorFilter.class);
	
	}
	
	public synchronized void registerPlugin(Class<? extends JavaFilterPlugin> clazz) {
		builtins.load(clazz);
		reload();
	}

	@Override
	public String getInterfaceDescription() {
		return "Filters are ways to process or transform spectral data. This can be used to do things like remove background or smooth noise.";
	}
	
	@Override
	public String getInterfaceName() {
		return "Filter";
	}
	
}
