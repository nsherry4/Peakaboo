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
import net.sciencestudio.bolt.plugin.core.issue.BoltIssue;
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
	
	/**
	 * On unmanaged directories, tests to make sure that containers are not empty.
	 * Does not test on managed directories, because that should generate a
	 * {@link BoltIssue}
	 */
	protected boolean unmanagedNotEmpty(BoltContainer<T> container) {
		if (managed) {
			//don't test jars if this is not a local path (eg dir a jar was run from)
			return true;
		}
		try {
			return container.size() > 0;
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
			if (container.isEmpty()) { return false; }
			if (importedFile(file).exists()) {
				// if the destination file already exists, we want to test if the new file would
				// consitute an upgrade for the existing file. If it does, then we can go ahead
				// and replace the existing file with the new one. If it doesn't, we can't
				// import it. TODO: implement a renaming scheme for these kinds of name
				// collisions
				if (isUpgrade(file)) {
					return true;
				}
			}
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public void doImport(File file) throws BoltImportException {
		BoltContainer<T> importing = build(file);
		
		//if a file of the same name already exists, but is not an upgrade, error out
		if (importedFile(file).exists()) {
			if (isUpgrade(file)) {
				//if this is an upgrade of an older file, we delete the older file
				build(importedFile(file)).delete();
			} else {
				String msg = "Importing " + file.getAbsolutePath() + " failed, file already exists";
				Bolt.logger().log(Level.INFO, msg);
				throw new BoltImportException(msg);		
			}
		}
		
		
		for (BoltContainer<T> existing : getContainers()) {
			if (importing.isUpgradeFor(existing)) {
				// TODO: Do we delete when the plugins in another (differently named) container
				// are all superceeded by newer versions?
				existing.delete();
			}
		}
		
		try {
			Files.copy(file.toPath(), importedFile(file).toPath());
		} catch (IOException e) {
			String msg = "Importing " + file.getAbsolutePath() + " failed";
			Bolt.logger().log(Level.WARNING, msg, e);
			throw new BoltImportException(msg, e);
		}
		
	}
	
	
	
	private boolean isUpgrade(File file) {
		if (!importedFile(file).exists()) {
			return false;
		}
		BoltContainer<T> importing = build(file);
		BoltContainer<T> existing = build(importedFile(file));
		
		if (existing.isEmpty()) {
			return true;
		}
		
		return importing.isUpgradeFor(existing);
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
