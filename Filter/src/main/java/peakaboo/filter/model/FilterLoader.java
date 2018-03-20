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
import peakaboo.filter.plugins.advanced.DataToWavelet;
import peakaboo.filter.plugins.advanced.Identity;
import peakaboo.filter.plugins.advanced.Interpolation;
import peakaboo.filter.plugins.advanced.SpectrumNormalization;
import peakaboo.filter.plugins.advanced.SubFilter;
import peakaboo.filter.plugins.advanced.WaveletToData;
import peakaboo.filter.plugins.background.BruknerRemoval;
import peakaboo.filter.plugins.background.LinearTrimRemoval;
import peakaboo.filter.plugins.background.PolynomialRemoval;
import peakaboo.filter.plugins.mathematical.Addition;
import peakaboo.filter.plugins.mathematical.Derivative;
import peakaboo.filter.plugins.mathematical.Integrate;
import peakaboo.filter.plugins.mathematical.Multiply;
import peakaboo.filter.plugins.mathematical.Subtraction;
import peakaboo.filter.plugins.noise.AggressiveWaveletNoiseFilter;
import peakaboo.filter.plugins.noise.FourierLowPass;
import peakaboo.filter.plugins.noise.FlatAveraging;
import peakaboo.filter.plugins.noise.SavitskyGolaySmoothing;
import peakaboo.filter.plugins.noise.SpringSmoothing;
import peakaboo.filter.plugins.noise.WaveletNoiseFilter;
import peakaboo.filter.plugins.noise.WeightedAveraging;


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
		newPluginLoader.registerPlugin(DataToWavelet.class);
		newPluginLoader.registerPlugin(Identity.class);
		newPluginLoader.registerPlugin(SubFilter.class);
		newPluginLoader.registerPlugin(SpectrumNormalization.class);
		newPluginLoader.registerPlugin(WaveletToData.class);
		
		newPluginLoader.registerPlugin(BruknerRemoval.class);
		newPluginLoader.registerPlugin(LinearTrimRemoval.class);
		newPluginLoader.registerPlugin(PolynomialRemoval.class);
		
		newPluginLoader.registerPlugin(Addition.class);
		newPluginLoader.registerPlugin(Derivative.class);
		newPluginLoader.registerPlugin(Integrate.class);
		newPluginLoader.registerPlugin(Multiply.class);
		newPluginLoader.registerPlugin(Subtraction.class);
		
		newPluginLoader.registerPlugin(AggressiveWaveletNoiseFilter.class);
		newPluginLoader.registerPlugin(FourierLowPass.class);
		newPluginLoader.registerPlugin(FlatAveraging.class);
		newPluginLoader.registerPlugin(WeightedAveraging.class);
		newPluginLoader.registerPlugin(SavitskyGolaySmoothing.class);
		newPluginLoader.registerPlugin(SpringSmoothing.class);
		newPluginLoader.registerPlugin(WaveletNoiseFilter.class);
		newPluginLoader.registerPlugin(Interpolation.class);
		
		
		
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
