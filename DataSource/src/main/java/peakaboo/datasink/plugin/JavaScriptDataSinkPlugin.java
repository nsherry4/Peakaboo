package peakaboo.datasink.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.logging.Level;

import net.sciencestudio.bolt.scripting.BoltInterface;
import net.sciencestudio.bolt.scripting.languages.JavascriptLanguage;
import net.sciencestudio.bolt.scripting.plugin.BoltScriptPlugin;
import peakaboo.common.PeakabooLog;
import peakaboo.datasource.model.DataSource;
import scitypes.util.StringInput;


public class JavaScriptDataSinkPlugin implements DataSinkPlugin, BoltScriptPlugin {

	private BoltInterface js;
	private File scriptFile;
	
	public JavaScriptDataSinkPlugin() {		

	}
	
	@Override
	public void setScriptFile(File file) {
		try {
			js = new BoltInterface(new JavascriptLanguage(), "");
			this.scriptFile = file;
			try {
				js.setScript(StringInput.contents(this.scriptFile));
			} catch (FileNotFoundException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to set JavaScript Data Sink Plugin Source Code", e);
			}
			js.initialize();
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to set JavaScript Data Sink Plugin Source Code", e);
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
	public void write(DataSource source, Path destination) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String pluginName() {
		return getFormatName();
	}

	@Override
	public String pluginDescription() {
		return getFormatDescription();
	}

	@Override
	public String pluginVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFormatExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFormatName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFormatDescription() {
		// TODO Auto-generated method stub
		return null;
	}




}
