package peakaboo.filter;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import peakaboo.common.Version;
import peakaboo.filter.filters.advanced.DataToWavelet;
import peakaboo.filter.filters.advanced.FilterPartialSpectrum;
import peakaboo.filter.filters.advanced.Identity;
import peakaboo.filter.filters.advanced.Interpolation;
import peakaboo.filter.filters.advanced.SpectrumNormalization;
import peakaboo.filter.filters.advanced.WaveletToData;
import peakaboo.filter.filters.background.BruknerRemoval;
import peakaboo.filter.filters.background.LinearTrimRemoval;
import peakaboo.filter.filters.background.PolynomialRemoval;
import peakaboo.filter.filters.mathematical.Addition;
import peakaboo.filter.filters.mathematical.Derivative;
import peakaboo.filter.filters.mathematical.Integrate;
import peakaboo.filter.filters.mathematical.Multiply;
import peakaboo.filter.filters.mathematical.Subtraction;
import peakaboo.filter.filters.noise.AggressiveWaveletNoiseFilter;
import peakaboo.filter.filters.noise.FourierLowPass;
import peakaboo.filter.filters.noise.MovingAverage;
import peakaboo.filter.filters.noise.SavitskyGolaySmoothing;
import peakaboo.filter.filters.noise.SpringSmoothing;
import peakaboo.filter.filters.noise.WaveletNoiseFilter;
import peakaboo.filter.filters.programming.JPython;
import peakaboo.filter.filters.programming.Java;
import peakaboo.filter.model.AbstractFilter;
import bolt.plugin.BoltPluginLoader;
import bolt.plugin.ClassInheritanceException;
import bolt.plugin.ClassInstantiationException;

import commonenvironment.Env;

import fava.functionable.FList;

public class FilterLoader
{

	private static BoltPluginLoader<AbstractFilter> pluginLoader;
	
	public static synchronized FList<AbstractFilter> getAvailableFilters()
	{

		try {
			
			FList<AbstractFilter> filters = new FList<AbstractFilter>(); 
			
			if (pluginLoader == null)
			{

				BoltPluginLoader<AbstractFilter> newPluginLoader = new BoltPluginLoader<AbstractFilter>(AbstractFilter.class);
				

				//register built-in plugins
				newPluginLoader.registerPlugin(DataToWavelet.class);
				newPluginLoader.registerPlugin(Identity.class);
				newPluginLoader.registerPlugin(FilterPartialSpectrum.class);
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
				newPluginLoader.registerPlugin(MovingAverage.class);
				newPluginLoader.registerPlugin(SavitskyGolaySmoothing.class);
				newPluginLoader.registerPlugin(SpringSmoothing.class);
				newPluginLoader.registerPlugin(WaveletNoiseFilter.class);
				newPluginLoader.registerPlugin(Interpolation.class);
				
				newPluginLoader.registerPlugin(Java.class);
				newPluginLoader.registerPlugin(JPython.class);
				
				
				
				//load plugins from local
				newPluginLoader.register();
				
				//load plugins from the application data directory
				File appDataDir = Env.appDataDirectory(Version.program_name);
				appDataDir.mkdirs();
				newPluginLoader.register(appDataDir);
				
				pluginLoader = newPluginLoader;
				
			}
			
			filters.addAll(pluginLoader.getNewInstancesForAllPlugins());
			
			Collections.sort(filters, new Comparator<AbstractFilter>() {

				@Override
				public int compare(AbstractFilter f1, AbstractFilter f2)
				{
					return f1.getFilterName().compareTo(f1.getFilterName());
				}});
			
			return filters;
			
		} catch (ClassInheritanceException e) {
			e.printStackTrace();
		} catch (ClassInstantiationException e) {
			e.printStackTrace();
		}
				
		return null;
		
	}
	
}
