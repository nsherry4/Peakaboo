package org.peakaboo.cli.commands;

import java.io.File;
import java.util.List;

import org.peakaboo.cli.HeadlessDataLoader.HeadlessLoadException;
import org.peakaboo.dataset.io.DataInputAdapter;

/** Shared helpers for commands that open datasets. */
public final class Datasets {

	private Datasets() {}

	/**
	 * Converts local files to data inputs, checking they exist first so that a
	 * typo'd path fails by name rather than as a format detection failure.
	 */
	public static List<DataInputAdapter> adapt(List<File> files) throws HeadlessLoadException {
		for (File file : files) {
			if (!file.exists()) {
				throw new HeadlessLoadException("No such file: " + file);
			}
		}
		return DataInputAdapter.fromFilenames(files.stream().map(File::getAbsolutePath).toList());
	}

}
