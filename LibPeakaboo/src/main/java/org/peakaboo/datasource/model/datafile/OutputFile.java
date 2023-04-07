package org.peakaboo.datasource.model.datafile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public interface OutputFile extends AutoCloseable {

	/**
	 * Returns an {@link OutputStream} for this OutputFile which the consumer is responsible for flushing and closing. 
	 * 
	 * @throws IOException
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * If writing to an OutputStream is not an option, this will return a temporary
	 * file that can be written to.
	 */
	Path getFallback();

	/**
	 * After writing to the {@link File} returned by
	 * {@link OutputFile#getFallbackFile()}, this will load the contents of that
	 * file into the real {@link OutputStream} target.
	 */
	void finishFallback();

}
