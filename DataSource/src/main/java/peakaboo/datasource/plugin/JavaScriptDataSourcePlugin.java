package peakaboo.datasource.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.bolt.scripting.BoltInterface;
import net.sciencestudio.bolt.scripting.languages.JavascriptLanguage;
import net.sciencestudio.bolt.scripting.plugin.BoltScriptPlugin;
import peakaboo.common.PeakabooLog;
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


public class JavaScriptDataSourcePlugin implements DataSourcePlugin, BoltScriptPlugin {

	private BoltInterface js;
	private SimpleScanData scanData;
	private File scriptFile;
	
	private Interaction interaction = new SimpleInteraction();
	
	public JavaScriptDataSourcePlugin() {		

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
	public FileFormat getFileFormat() {
		String ext = lookup("formatExtension", null);
		if (ext == null) {
			return new SimpleFileFormat(
					true, 
					lookup("formatName", "Unknown JavaScript Data Source"), 
					lookup("formatDesc", "Unknown JavaScript Data Source")
			);
		} else {
			return new SimpleFileFormat(
					true, 
					lookup("formatName", "Unknown JavaScript Data Source"), 
					lookup("formatDesc", "Unknown JavaScript Data Source"), 
					ext
			);
		}
	}


	@Override
	public ScanData getScanData() {
		return scanData;
	}


	public void read(Path file) throws Exception {
		scanData = new SimpleScanData(file.getFileName().toString());
		
		
		String contents = StringInput.contents(Files.newBufferedReader(file));
		List<List<Double>> result = (List<List<Double>>) js.call("read", contents);
		
		for (List<Double> scan : result) {
			ISpectrum spectrum = new ISpectrum(scan.size());
			for (Double entry : scan) {
				spectrum.add(entry.floatValue());
			}
			scanData.add(spectrum);
		}
		
	}

	@Override
	public void read(List<Path> files) throws Exception {
		read(files.get(0));
	}

	@Override
	public String pluginVersion() {
		return lookup("pluginVersion", "None");
	}

	
	@Override
	public String pluginName() {
		return getFileFormat().getFormatName();
	}

	@Override
	public String pluginDescription() {
		return getFileFormat().getFormatDescription();
	}
	
	
	
	
	
	
	@Override
	public void setInteraction(Interaction interaction) {
		this.interaction = interaction;
	}
	
	public Interaction getInteraction() {
		return interaction;
	}
	
	
	
	
	

	
	@Override
	public Optional<Metadata> getMetadata() {
		//Not Implemented
		return Optional.empty();
	}

	@Override
	public Optional<DataSize> getDataSize() {
		//Not Implemented
		return Optional.empty();
	}

	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		//Not Implemented
		return Optional.empty();
	}
	
	@Override
	public Optional<Group> getParameters(List<Path> paths) {
		return Optional.empty();
	}

	
	/*
	 * JS plugins get a pass from UUIDs since we want to keep them as simple as possible.
	 */
	@Override
	public String pluginUUID() {
		return scriptFile.getAbsolutePath();
	}
	
}
