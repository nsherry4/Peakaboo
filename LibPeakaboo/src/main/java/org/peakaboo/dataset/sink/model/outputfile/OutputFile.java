package org.peakaboo.dataset.sink.model.outputfile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public interface OutputFile extends AutoCloseable {

	/**
	 * Returns an {@link OutputStream} for this OutputFile which the consumer is
	 * responsible for flushing and closing.
	 * 
	 * @throws IOException
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * If writing to an OutputStream is not an option, this will return a temporary
	 * file that can be written to. Calling both this method and
	 * {@link OutputFile#getOutputStream()} is not allowed and may cause the
	 * OutputFile to fail. If this method is called, the contents of this file will
	 * be re-read and written to an {@link OutputStream} when this OutputFile is
	 * closed.
	 */
	Path getFallback();

}
