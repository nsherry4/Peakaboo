package peakaboo.filter.model;

import java.io.File;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.IBoltPluginSet;
import net.sciencestudio.bolt.plugin.java.BoltPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import net.sciencestudio.bolt.scripting.plugin.IBoltScriptPluginLoader;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.filter.plugins.FilterPlugin;
import peakaboo.filter.plugins.JavaFilterPlugin;
import peakaboo.filter.plugins.JavaScriptFilterPlugin;
import peakaboo.filter.plugins.advanced.IdentityFilter;
import peakaboo.filter.plugins.advanced.InterpolationFilter;
import peakaboo.filter.plugins.advanced.SpectrumNormalizationFilter;
import peakaboo.filter.plugins.advanced.SubFilter;
import peakaboo.filter.plugins.background.BruknerBackgroundFilter;
import peakaboo.filter.plugins.background.LinearTrimBackgroundFilter;
import peakaboo.filter.plugins.background.PolynomialBackgroundFilter;
import peakaboo.filter.plugins.mathematical.AdditionMathFilter;
import peakaboo.filter.plugins.mathematical.DerivativeMathFilter;
import peakaboo.filter.plugins.mathematical.IntegralMathFilter;
import peakaboo.filter.plugins.mathematical.MultiplicationMathFilter;
import peakaboo.filter.plugins.mathematical.SubtractionMathFilter;
import peakaboo.filter.plugins.noise.SavitskyGolayNoiseFilter;
import peakaboo.filter.plugins.noise.FourierNoiseFilter;
import peakaboo.filter.plugins.noise.LowStatisticsNoiseFilter;
import peakaboo.filter.plugins.noise.SpringNoiseFilter;
import peakaboo.filter.plugins.noise.WaveletNoiseFilter;
import peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;


public class FilterLoader
{

	private static boolean loaded = false; 
	private static BoltPluginSet<FilterPlugin> plugins = new IBoltPluginSet<>();
	
	
	public static void load() {
		try {
			if (!loaded) {
				initLoader();
			}
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.WARNING, "Failed to load filter plugins", e);
		}
	}
	
	
	public static BoltPluginSet<FilterPlugin> getPluginSet() {
		load();
		return plugins;
	}
	
	private static void initLoader() throws ClassInheritanceException, ClassInstantiationException {
		BoltPluginLoader<JavaFilterPlugin> newPluginLoader = new BoltPluginLoader<>(plugins, JavaFilterPlugin.class);
		

		//register built-in plugins
		newPluginLoader.registerPlugin(IdentityFilter.class);
		newPluginLoader.registerPlugin(SubFilter.class);
		newPluginLoader.registerPlugin(SpectrumNormalizationFilter.class);
		
		newPluginLoader.registerPlugin(BruknerBackgroundFilter.class);
		newPluginLoader.registerPlugin(LinearTrimBackgroundFilter.class);
		newPluginLoader.registerPlugin(PolynomialBackgroundFilter.class);
		
		newPluginLoader.registerPlugin(AdditionMathFilter.class);
		newPluginLoader.registerPlugin(DerivativeMathFilter.class);
		newPluginLoader.registerPlugin(IntegralMathFilter.class);
		newPluginLoader.registerPlugin(MultiplicationMathFilter.class);
		newPluginLoader.registerPlugin(SubtractionMathFilter.class);
		
		newPluginLoader.registerPlugin(FourierNoiseFilter.class);
		newPluginLoader.registerPlugin(WeightedAverageNoiseFilter.class);
		newPluginLoader.registerPlugin(SavitskyGolayNoiseFilter.class);
		newPluginLoader.registerPlugin(SpringNoiseFilter.class);
		newPluginLoader.registerPlugin(WaveletNoiseFilter.class);
		newPluginLoader.registerPlugin(InterpolationFilter.class);
		newPluginLoader.registerPlugin(LowStatisticsNoiseFilter.class);
		
		
		
		//load plugins from local
		newPluginLoader.register();
		
		//load plugins from the application data directory
		File appDataDir = Configuration.appDir("Plugins/Filter");
		appDataDir.mkdirs();
		newPluginLoader.register(appDataDir);


		
		IBoltScriptPluginLoader<JavaScriptFilterPlugin> jsLoader = new IBoltScriptPluginLoader<>(plugins, JavaScriptFilterPlugin.class);
		jsLoader.scanDirectory(appDataDir, ".js");
		
		
		
		loaded = true;
		
		
	}
	
	
	public synchronized static void registerPlugin(Class<? extends JavaFilterPlugin> clazz) {
		try {
			BoltPluginLoader<JavaFilterPlugin> javaLoader = new BoltPluginLoader<JavaFilterPlugin>(plugins, JavaFilterPlugin.class);
			javaLoader.registerPlugin(clazz);
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.WARNING, "Failed to register filter plugin " + clazz.getName(), e);
		}
	}
	
	
}
