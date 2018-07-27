package peakaboo.filter.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.script.ScriptException;

import net.sciencestudio.autodialog.model.Value;
import net.sciencestudio.bolt.scripting.BoltInterface;
import net.sciencestudio.bolt.scripting.languages.JavascriptLanguage;
import net.sciencestudio.bolt.scripting.plugin.BoltScriptPlugin;
import peakaboo.common.PeakabooLog;
import peakaboo.filter.model.FilterType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.util.StringInput;

public class JavaScriptFilterPlugin implements FilterPlugin, BoltScriptPlugin {

	private BoltInterface js;
	private File scriptFile;
	
	private boolean enabled = true;
	
	
	public JavaScriptFilterPlugin() {		

	}
	
	
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void setScriptFile(File file) {
		try {
			js = new BoltInterface(new JavascriptLanguage(), "");
			this.scriptFile = file;
			try {
				js.setScript(StringInput.contents(this.scriptFile));
			} catch (FileNotFoundException e) {
				PeakabooLog.get().log(Level.SEVERE, "Error setting Java Script Data Source source code", e);
			}
			js.initialize();
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Error initializing Java Script Data Source plugin", e);
		}
	}
	
	private <T> T lookup(String var, T fallback) {
		Object o = js.get(var);
		T val = (T)o;
		if (val != null) {
			return val;
		}
		return fallback;
	}

	
	@Override
	public boolean pluginEnabled() {
		return true;
	}


	@Override
	public String pluginVersion() {
		return lookup("pluginVersion", "None");
	}

	
	@Override
	public String pluginName() {
		return getFilterName();
	}

	@Override
	public String pluginDescription() {
		return getFilterDescription();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getFilterName() {
		return lookup("filterName", "None");
	}

	@Override
	public String getFilterDescription() {
		return lookup("filterDescription", "None");
	}

	@Override
	public FilterType getFilterType() {
		String typeString = lookup("filterType", "ADVANCED");
		FilterType type;
		try {
			type = FilterType.valueOf(typeString);
		} catch (IllegalArgumentException e) {
			type = FilterType.ADVANCED;
		}
		return type;
	}





	@Override
	public PlotPainter getPainter() {
		//Not Supported
		return null;
	}

	@Override
	public boolean canFilterSubset() {
		return lookup("canFilterSubset", true);
	}

	@Override
	public ReadOnlySpectrum filter(ReadOnlySpectrum data, boolean cache) {
		
		float[] copy = data.backingArrayCopy();
		float[] result;
		
		try {
			result = (float[]) js.call("filter", copy);
		} catch (NoSuchMethodException | ScriptException e) {
			PeakabooLog.get().log(Level.SEVERE, "Error calling Java Script Filter plugin", e);
			result = data.backingArrayCopy();
		}
		
		return new ISpectrum(result);
		
	}

	
	
	
	
	
	////////////////////////////////////////////////////
	// Parameters
	////////////////////////////////////////////////////
	
	@Override
	public List<Value<?>> getParameters() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public void setParameters(List<Value<?>> params) {
		// TODO Auto-generated method stub
		
	}
	
	
	public String toString()
	{
		return this.getFilterName();
	}


	/*
	 * JS plugins get a pass from UUIDs since we want to keep them as simple as possible.
	 */
	@Override
	public String pluginUUID() {
		return scriptFile.getAbsolutePath();
	}
	
	
	
	
}
