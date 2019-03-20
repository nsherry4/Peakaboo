package net.sciencestudio.bolt.plugin.core.loader;

import java.io.File;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;
import net.sciencestudio.bolt.plugin.core.exceptions.BoltImportException;

public interface BoltManagedLoader<T extends BoltPlugin> extends BoltLoader<T> {

	boolean canImport(File file);
	void doImport(File file) throws BoltImportException;
	BoltContainer<T> build(File file);
		
}
