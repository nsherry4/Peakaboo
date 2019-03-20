package net.sciencestudio.bolt.plugin.core.loader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;
import net.sciencestudio.bolt.plugin.core.exceptions.BoltImportException;
import net.sciencestudio.bolt.plugin.java.BoltJar;

public abstract class BoltDirectoryLoader<T extends BoltPlugin> implements BoltManagedLoader<T> {

	private File directory;
	private boolean managed;
	
	public BoltDirectoryLoader(File directory, boolean managed) {
		this.directory = directory;
		this.managed = managed;
	}
	
	public static File getLocalDirectory(Class<?> targetClass) {
		if (BoltJar.isClassInJar(targetClass)) {
			return BoltJar.getJarForClass(targetClass).getParentFile();
		} else {
			return new File(".").getAbsoluteFile();
		}
	}
	
	public boolean ensure() {
		return directory.mkdirs();
	}
	
	public boolean isManaged() {
		return managed;
	}
	
	public File getDirectory() {
		return directory;
	}
	
	/**
	 * Returns a list of managed files from the managed plugin directory
	 */
	protected List<File> scanFiles(Predicate<File> filter) {
		ensure();
		try {
			return Files.list(directory.toPath())
					.map(Path::toFile)
					.filter(filter::test)
					.collect(Collectors.toList());
		} catch (IOException e) {
			Bolt.logger().log(Level.WARNING, "Cannot list managed plugin directory contents", e);
		}
		return new ArrayList<>();
	}	
	
	protected boolean managedNotEmpty(BoltContainer<T> container) {
		if (!managed) {
			//don't test jars if this is not a local path (eg dir a jar was run from)
			return true;
		}
		try {
			return container.getPlugins().getAll().size() > 0;
		} catch (Throwable e) {
			return false;
		}
	}
	

	
	@Override
	public boolean canImport(File file) {
		try {
			if (!managed) { return false; }
			BoltContainer<T> container = build(file);
			if (container == null) { return false; }
			return !container.getPlugins().getAll().isEmpty();
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public void doImport(File file) throws BoltImportException {
		if (importedFile(file).exists()) {
			String msg = "Importing " + file.getAbsolutePath() + " failed, file already exists";
			Bolt.logger().log(Level.INFO, msg);
			throw new BoltImportException(msg);
		}
		
		try {
			Files.copy(file.toPath(), importedFile(file).toPath());
		} catch (IOException e) {
			String msg = "Importing " + file.getAbsolutePath() + " failed";
			Bolt.logger().log(Level.WARNING, msg, e);
			throw new BoltImportException(msg, e);
		}
		
	}
	

	
	/**
	 * Determines the name of a File, were it to be imported to the given directory.
	 */
	public File importedFile(File file) {
		ensure();
		return new File(directory.getAbsolutePath() + File.separator + file.getName());
	}

	protected static URL fileToURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
}
