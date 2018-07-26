package peakaboo.filter.model;

import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import net.sciencestudio.bolt.plugin.java.BoltJavaPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import net.sciencestudio.bolt.scripting.plugin.IBoltScriptPluginLoader;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.common.PluginManager;
import peakaboo.filter.plugins.FilterPlugin;
import peakaboo.filter.plugins.JavaFilterPlugin;
import peakaboo.filter.plugins.JavaScriptFilterPlugin;
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


public class FilterPluginManager extends PluginManager<FilterPlugin> {


	public static FilterPluginManager SYSTEM = new FilterPluginManager();
	
	public FilterPluginManager() {
		super(Configuration.appDir("Plugins/Filter"));
	}
	
	@Override
	protected void loadCustomPlugins() {
		
		
		try {
			BoltJavaPluginLoader<JavaFilterPlugin> newPluginLoader = javaLoader();
			

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
			BoltJavaPluginLoader<JavaFilterPlugin> javaLoader = javaLoader();
			BoltPluginController<JavaFilterPlugin> plugin = javaLoader.registerPlugin(clazz);
			if (plugin != null) {
				PeakabooLog.get().info("Registered Filter Plugin " + plugin.getName() + " from " + plugin.getSource());
			}
			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.WARNING, "Error registering filter plugin " + clazz.getName(), e);
		}
	}

	@Override
	protected BoltJavaPluginLoader<JavaFilterPlugin> javaLoader() throws ClassInheritanceException {
		return new BoltJavaPluginLoader<JavaFilterPlugin>(getPlugins(), JavaFilterPlugin.class);
	}

	@Override
	protected IBoltScriptPluginLoader<? extends FilterPlugin> scriptLoader() {
		return new IBoltScriptPluginLoader<>(getPlugins(), JavaScriptFilterPlugin.class);
	}
	
	
}
