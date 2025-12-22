package org.peakaboo.filter.model;

import java.io.File;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.PeakabooPluginRegistry;
import org.peakaboo.filter.plugins.advanced.DatasetNormalizationFilter;
import org.peakaboo.filter.plugins.advanced.IdentityFilter;
import org.peakaboo.filter.plugins.advanced.PeakDetectorFilter;
import org.peakaboo.filter.plugins.advanced.SpectrumNormalizationFilter;
import org.peakaboo.filter.plugins.advanced.SubFilter;
import org.peakaboo.filter.plugins.background.BruknerBackgroundFilter;
import org.peakaboo.filter.plugins.background.ComptonBackgroundFilter;
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
import org.peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;

public class FilterRegistry extends PeakabooPluginRegistry<Filter> {

	private static FilterRegistry SYSTEM;
	public static void init(File filterDir) {
		try {
			if (SYSTEM == null) {
				SYSTEM = new FilterRegistry(filterDir);
				SYSTEM.load();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load filter plugins", e);
		}
	}
	public static FilterRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	public Optional<Filter> fromSaved(String saved) {
		try {
			SavedPlugin loaded = SavedPlugin.load(saved);
			return fromSaved(loaded);
		} catch (DruthersLoadException e) {
			return Optional.empty();
		}
		
	}
	
	@Override
	public Optional<Filter> fromSaved(SavedPlugin saved) {
		var lookup = FilterRegistry.system().getByUUID(saved.uuid);
		var created = lookup.flatMap(d -> d.create());
		
		if (created.isEmpty()) {
			return Optional.empty();
		}
		var filter = created.get();
		
		filter.initialize();
		filter.getParameterGroup().deserialize(saved.settings);
		return Optional.of(filter);
	}

    public FilterRegistry() {
        super("filter");

        var builtins = new BoltJavaBuiltinLoader<>(this, Filter.class);

        builtins.load(IdentityFilter.class);
        builtins.load(SubFilter.class);
        builtins.load(SpectrumNormalizationFilter.class);
        builtins.load(DatasetNormalizationFilter.class);

        builtins.load(BruknerBackgroundFilter.class);
        builtins.load(LinearTrimBackgroundFilter.class);
        builtins.load(PolynomialBackgroundFilter.class);
        builtins.load(SquareSnipBackgroundFilter.class);
        builtins.load(SpectrumBackgroundFilter.class);

        builtins.load(ComptonBackgroundFilter.class);

        builtins.load(AdditionMathFilter.class);
        builtins.load(DerivativeMathFilter.class);
        builtins.load(IntegralMathFilter.class);
        builtins.load(MultiplicationMathFilter.class);
        builtins.load(SubtractionMathFilter.class);

        builtins.load(FourierNoiseFilter.class);
        builtins.load(WeightedAverageNoiseFilter.class);
        builtins.load(SavitskyGolayNoiseFilter.class);
        builtins.load(SpringNoiseFilter.class);
        builtins.load(LowStatisticsNoiseFilter.class);

        builtins.load(PeakDetectorFilter.class);

        addLoader(builtins);


        // Load plugins from within an AIO jar containing the app + plugins
        addLoader(new BoltJarDirectoryLoader<>(this, Filter.class));
    }

	public FilterRegistry(File filterDir) {
		this();

        if (filterDir != null) {
            addLoader(new BoltJarDirectoryLoader<>(this, Filter.class, filterDir));
        }
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
