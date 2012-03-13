package peakaboo.filter;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import peakaboo.common.Version;
import peakaboo.filter.filters.advanced.DataToWavelet;
import peakaboo.filter.filters.advanced.Identity;
import peakaboo.filter.filters.advanced.FilterPartialSpectrum;
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
import bolt.plugin.BoltPluginLoader;
import bolt.plugin.ClassInheritanceException;
import bolt.plugin.ClassInstantiationException;

import commonenvironment.Env;

import fava.functionable.FList;

public class FilterLoader
{

	private static BoltPluginLoader<AbstractFilter> pluginLoader;
	
	public static FList<AbstractFilter> getAvailableFilters()
	{

		try {
			
			FList<AbstractFilter> filters = new FList<AbstractFilter>(); 
			
			if (pluginLoader == null)
			{
			
				
				pluginLoader = new BoltPluginLoader<AbstractFilter>(AbstractFilter.class);
				

				//register built-in plugins
				pluginLoader.registerPlugin(DataToWavelet.class);
				pluginLoader.registerPlugin(Identity.class);
				pluginLoader.registerPlugin(FilterPartialSpectrum.class);
				pluginLoader.registerPlugin(SpectrumNormalization.class);
				pluginLoader.registerPlugin(WaveletToData.class);
				
				pluginLoader.registerPlugin(BruknerRemoval.class);
				pluginLoader.registerPlugin(LinearTrimRemoval.class);
				pluginLoader.registerPlugin(PolynomialRemoval.class);
				
				pluginLoader.registerPlugin(Addition.class);
				pluginLoader.registerPlugin(Derivative.class);
				pluginLoader.registerPlugin(Integrate.class);
				pluginLoader.registerPlugin(Multiply.class);
				pluginLoader.registerPlugin(Subtraction.class);
				
				pluginLoader.registerPlugin(AggressiveWaveletNoiseFilter.class);
				pluginLoader.registerPlugin(FourierLowPass.class);
				pluginLoader.registerPlugin(MovingAverage.class);
				pluginLoader.registerPlugin(SavitskyGolaySmoothing.class);
				pluginLoader.registerPlugin(SpringSmoothing.class);
				pluginLoader.registerPlugin(WaveletNoiseFilter.class);
				
				pluginLoader.registerPlugin(Java.class);
				pluginLoader.registerPlugin(JPython.class);
				
				
				
				//load plugins from local
				pluginLoader.register();
				
				//load plugins from the application data directory
				File appDataDir = Env.appDataDirectory(Version.program_name);
				appDataDir.mkdirs();
				pluginLoader.register(appDataDir);
				
				
				
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
