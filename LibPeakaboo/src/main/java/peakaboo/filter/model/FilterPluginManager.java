package peakaboo.filter.model;

import java.io.File;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltClassloaderPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import net.sciencestudio.bolt.plugin.java.IBoltJavaPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import peakaboo.common.PeakabooLog;
import peakaboo.filter.plugins.FilterPlugin;
import peakaboo.filter.plugins.JavaFilterPlugin;
import peakaboo.filter.plugins.advanced.IdentityFilter;
import peakaboo.filter.plugins.advanced.SpectrumNormalizationFilter;
import peakaboo.filter.plugins.advanced.SubFilter;
import peakaboo.filter.plugins.background.BruknerBackgroundFilter;
import peakaboo.filter.plugins.background.LinearTrimBackgroundFilter;
import peakaboo.filter.plugins.background.PolynomialBackgroundFilter;
import peakaboo.filter.plugins.background.SquareSnipBackgroundFilter;
import peakaboo.filter.plugins.mathematical.AdditionMathFilter;
import peakaboo.filter.plugins.mathematical.DerivativeMathFilter;
import peakaboo.filter.plugins.mathematical.IntegralMathFilter;
import peakaboo.filter.plugins.mathematical.MultiplicationMathFilter;
import peakaboo.filter.plugins.mathematical.SubtractionMathFilter;
import peakaboo.filter.plugins.noise.FourierNoiseFilter;
import peakaboo.filter.plugins.noise.LowStatisticsNoiseFilter;
import peakaboo.filter.plugins.noise.SavitskyGolayNoiseFilter;
import peakaboo.filter.plugins.noise.SpringNoiseFilter;
import peakaboo.filter.plugins.noise.WaveletNoiseFilter;
import peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;


public class FilterPluginManager extends BoltPluginManager<FilterPlugin> {


	public static FilterPluginManager SYSTEM;
	
	public static void init(File filterDir) {
		if (SYSTEM == null) {
			SYSTEM = new FilterPluginManager(filterDir);
			SYSTEM.load();
		}
	}
	
	public FilterPluginManager(File filterDir) {
		super(filterDir);
	}
	
	@Override
	protected void loadCustomPlugins() {
		
		
		try {
			BoltClassloaderPluginLoader<JavaFilterPlugin> newPluginLoader = javaLoader(getPlugins());
			

			//register built-in plugins
			newPluginLoader.registerPlugin(IdentityFilter.class);
			newPluginLoader.registerPlugin(SubFilter.class);
			newPluginLoader.registerPlugin(SpectrumNormalizationFilter.class);
			
			newPluginLoader.registerPlugin(BruknerBackgroundFilter.class);
			newPluginLoader.registerPlugin(LinearTrimBackgroundFilter.class);
			newPluginLoader.registerPlugin(PolynomialBackgroundFilter.class);
			newPluginLoader.registerPlugin(SquareSnipBackgroundFilter.class);
			
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
			newPluginLoader.registerPlugin(LowStatisticsNoiseFilter.class);

			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load Filter plugins", e);
		}
		
		
	}
	
	public synchronized void registerPlugin(Class<? extends JavaFilterPlugin> clazz) {
		try {
			BoltClassloaderPluginLoader<JavaFilterPlugin> javaLoader = javaLoader(getPlugins());
			BoltPluginController<JavaFilterPlugin> plugin = javaLoader.registerPlugin(clazz);
			if (plugin != null) {
				PeakabooLog.get().info("Registered Filter Plugin " + plugin.getName() + " from " + plugin.getSource());
			}
			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.WARNING, "Error registering filter plugin " + clazz.getName(), e);
		}
	}

	@Override
	protected BoltClassloaderPluginLoader<JavaFilterPlugin> javaLoader(BoltPluginSet<FilterPlugin> pluginset) throws ClassInheritanceException {
		return new IBoltJavaPluginLoader<JavaFilterPlugin>(pluginset, JavaFilterPlugin.class);
	}

	@Override
	protected BoltFilesytstemPluginLoader<? extends FilterPlugin> scriptLoader(BoltPluginSet<FilterPlugin> pluginset) {
		return null;
	}
	
	
}
