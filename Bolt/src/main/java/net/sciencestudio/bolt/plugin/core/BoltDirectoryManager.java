package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.exceptions.BoltImportException;

public abstract class BoltDirectoryManager<P extends BoltPlugin> {

	public abstract File getDirectory();
	public abstract BoltPluginSet<P> pluginsInFile(File file);
	public abstract List<File> managedFiles();
	
	
	/**
	 * Checks the name (not the path) of the given file, and checks to see if an 
	 * identically named file is in the managed plugins folder
	 * @param file the file to examine
	 * @return true if the managed plugins folder already contains a file with this name, false otherwise
	 */
	private boolean hasFile(File file) {
		File newFilename = importFilePath(file);
		return newFilename.exists();
	}
	
	private File importFilePath(File file) {
		ensure();
		return new File(getDirectory().getAbsolutePath() + File.separator + file.getName());
	}
	
	public boolean ensure() {
		return getDirectory().mkdirs();
	}
	
	/**
	 * Returns a list of managed files from the managed plugin directory
	 */
	protected List<File> scanFiles(Predicate<Path> filter) {
		ensure();
		try {
			return Files.list(getDirectory().toPath())
					.filter(filter::test)
					.filter(p -> this.fileContainsPlugins(p.toFile()))
					.map(path -> path.toFile())
					.collect(Collectors.toList());
		} catch (IOException e) {
			Bolt.logger().log(Level.WARNING, "Cannot list managed plugin directory contents", e);
		}
		return new ArrayList<>();
	}
	
	
	BoltPluginSet<? extends BoltPlugin> importOrUpgradeFile(File file) throws BoltImportException {
		if (!fileContainsPlugins(file)) {
			return null;
		}
		
		Optional<File> upgradeTarget = getUpgradeTarget(file);
		//looks like this jar is an upgrade for an existing jar
		if (upgradeTarget.isPresent()) {
			removeFile(upgradeTarget.get());
		}
		
		return importFile(file);
	}
	
	boolean removeFile(File file) {
		ensure();
		Path path = file.toPath();
		
		if (!path.toString().startsWith(getDirectory().toString())) {
			Bolt.logger().log(Level.WARNING, "File " + file + " does not appear to be in the managed directory");
			return false;
		}
		
		if (!managedFiles().contains(file)) {
			Bolt.logger().log(Level.WARNING, "File " + file + " does not appear to be managed by this directory manager");
			return false;
		}
		
		try {
			Files.delete(path);
			return true;
		} catch (IOException e) {
			Bolt.logger().log(Level.WARNING, "Failed to delete plugin file", e);
			return false;
		}
	}
	
	boolean fileContainsPlugins(File file) {
		return pluginsInFile(file).size() > 0;
	}

	/**
	 * Checks to see if the new jar file is an upgrade for the old jar 
	 * in the plugins directory. It does this by making sure that all plugins 
	 * contained in the original jar are contained in the new jar, and that their 
	 * versions are the same or newer.
	 * @param jar the jar to examine
	 * @return true if the given jar is an upgrade for an existing managed jar, false otherwise
	 */
	private boolean fileIsUpgradeFor(File newFile, File oldFile) {
		BoltPluginSet<P> oldSet = pluginsInFile(newFile);
		BoltPluginSet<P> newSet = pluginsInFile(oldFile);
		
		boolean match = newSet.isUpgradeFor(oldSet);
		return match;
	}
	
	private BoltPluginSet<P> importFile(File file) throws BoltImportException {
		ensure();
		
		BoltPluginSet<P> plugins = pluginsInFile(file);
		
		
		if (plugins.size() == 0) {
			String msg = "Importing " + file.getAbsolutePath() + " failed, it does not contain any plugins";
			Bolt.logger().log(Level.INFO, msg);
			throw new BoltImportException("msg");
		}
		
		if (hasFile(file)) {
			String msg = "Importing " + file.getAbsolutePath() + " failed, file already exists";
			Bolt.logger().log(Level.INFO, msg);
			throw new BoltImportException(msg);
		}
		
		try {
			Files.copy(file.toPath(), importFilePath(file).toPath());
		} catch (IOException e) {
			String msg = "Importing " + file.getAbsolutePath() + " failed";
			Bolt.logger().log(Level.WARNING, msg, e);
			throw new BoltImportException(msg, e);
		}
		
		return plugins;
	}
	
	private Optional<File> getUpgradeTarget(File file) {
		for (File managedJar : managedFiles()) {
			boolean isUpgrade = fileIsUpgradeFor(file, managedJar);
			if (isUpgrade) {
				return Optional.of(managedJar);
			}
		}

		return Optional.empty();
	}
	

}
