package org.peakaboo.framework.bolt.plugin.core.loader;

import java.io.File;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltImportException;

public interface BoltManagedLoader<T extends BoltPlugin> extends BoltLoader<T> {

	boolean canImport(File file);
	void doImport(File file) throws BoltImportException;
	BoltContainer<T> build(File file);
		
}
