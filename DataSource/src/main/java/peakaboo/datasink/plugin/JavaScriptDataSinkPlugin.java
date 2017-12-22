package peakaboo.datasink.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import bolt.scripting.BoltInterface;
import bolt.scripting.languages.JavascriptLanguage;
import bolt.scripting.plugin.BoltScriptPlugin;
import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.fileformat.FileFormat;
import peakaboo.datasource.model.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.model.components.interaction.Interaction;
import peakaboo.datasource.model.components.interaction.SimpleInteraction;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;
import peakaboo.datasource.model.components.scandata.SimpleScanData;
import scitypes.ISpectrum;
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
				e.printStackTrace();
			}
			js.initialize();
		} catch (Exception e) {
			e.printStackTrace();
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
	public void write(DataSource source, OutputStream destination) throws IOException {
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
